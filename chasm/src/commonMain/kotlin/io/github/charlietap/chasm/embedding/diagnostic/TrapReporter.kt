package io.github.charlietap.chasm.embedding.diagnostic

import com.github.michaelbull.result.get
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.ModuleNameSubsection
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.diagnostic.FunctionProvenance
import io.github.charlietap.chasm.compiler.diagnostic.FunctionProvenanceCompiler
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.MemoryAccess
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.error.WasmFrame
import io.github.charlietap.chasm.embedding.error.WasmTrap
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.ast.module.Module as AstModule
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

/**
 * A failure while resolving yields the error without a trap, never a
 * different error. [instantiation] describes an instantiation whose start
 * function trapped, before its instance could be registered.
 */
internal fun executionError(
    store: Store,
    error: ModuleTrapError,
    instantiation: Instantiation? = null,
): ChasmError.ExecutionError {
    val message = error.toString()
    if (error !is InvocationError.Trapped) return ChasmError.ExecutionError(message)
    val trap = try {
        TrapReporter(store, error.snapshot, instantiation).report(error.error, message)
    } catch (_: Exception) {
        null
    }
    return ChasmError.ExecutionError(message, trap)
}

/** Function addresses from [firstFunctionAddress] were allocated by this instantiation. */
internal class Instantiation(
    val module: Module,
    val firstFunctionAddress: Int,
)

private class TrapReporter(
    private val store: Store,
    private val snapshot: TrapSnapshot,
    private val instantiation: Instantiation?,
) {
    private val runtime = store.store
    private val modules = ArrayList<Pair<ModuleInstance, ModuleResolution?>>()
    private val functions = HashMap<Int, FunctionResolution?>()

    fun report(error: InvocationError, message: String): WasmTrap {
        var faultSite: SourceSite? = null
        val frames = List(snapshot.frameCount) { index ->
            val (frame, site) = frame(index)
            if (index == 0) faultSite = site
            frame
        }
        val reason = reasonOf(error)
        return WasmTrap(
            reason = reason,
            message = message,
            frames = frames,
            omittedFrames = snapshot.omittedFrames,
            traceComplete = snapshot.complete,
            memoryAccess = if (reason == TrapReason.MEMORY_OUT_OF_BOUNDS) faultSite?.let(::memoryAccess) else null,
            omissionIndex = snapshot.omissionIndex,
        )
    }

    private fun frame(index: Int): Pair<WasmFrame, SourceSite?> {
        val address = snapshot.functionAddresses[index]
        val function = runtime.functions[address] as FunctionInstance.WasmFunction
        val instance = function.module
        val functionIndex = instance.functionAddresses.indexOfFirst { candidate -> candidate.address == address }
        val module = moduleResolution(instance)
        val site = functionResolution(address, functionIndex, instance, module, index)
            ?.site(snapshot.ips[index] - snapshot.entryIps[index])
        val frame = WasmFrame(
            moduleName = module?.moduleName,
            functionIndex = functionIndex,
            functionName = module?.functionNames?.get(functionIndex) ?: exportName(instance, address),
            wasmOffset = site?.offset,
        )
        return frame to site
    }

    private fun moduleResolution(instance: ModuleInstance): ModuleResolution? {
        modules.firstOrNull { (candidate, _) -> candidate === instance }?.let { (_, resolution) -> return resolution }
        val source = store.diagnostics.find(instance) ?: startSource(instance)
        val resolution = source?.let { found ->
            WasmModuleDecoder(found.config.copy(decodeNameSection = true), found.binary).get()
                ?.let { module -> ModuleResolution(found, module, runtime) }
        }
        modules.add(instance to resolution)
        return resolution
    }

    // The instance being created is the only one owning functions allocated
    // by this instantiation; an imported start function belongs elsewhere.
    private fun startSource(instance: ModuleInstance): TrapSource? {
        val pending = instantiation ?: return null
        val functions = runtime.functions
        for (address in pending.firstFunctionAddress until functions.size) {
            val function = functions[address]
            if (function is FunctionInstance.WasmFunction && function.module === instance) {
                return TrapSource(instance, pending.module)
            }
        }
        return null
    }

    private fun functionResolution(
        address: Int,
        functionIndex: Int,
        instance: ModuleInstance,
        module: ModuleResolution?,
        frameIndex: Int,
    ): FunctionResolution? {
        if (functions.containsKey(address)) return functions[address]
        val resolution = module?.let {
            val definedIndex = module.ast.functions.indexOfFirst { function -> function.idx.toInt() == functionIndex }
            if (definedIndex < 0) return@let null
            val provenance = module.compiler.compile(definedIndex).get()
                ?.takeIf { compiled ->
                    compiled.matchesInstalled(runtime.program, snapshot.entryIps[frameIndex], snapshot.endIps[frameIndex])
                } ?: return@let null
            val body = module.ast.functions[definedIndex].body.instructions
            val offsets = WasmFunctionBodyOffsets(module.source.config, module.source.binary, definedIndex)
                ?.takeIf { offsets -> offsets.size == body.size } ?: return@let null
            FunctionResolution(provenance, body, offsets)
        }
        functions[address] = resolution
        return resolution
    }

    /** Null unless the reconstructed range is actually out of bounds. */
    private fun memoryAccess(site: SourceSite): MemoryAccess? {
        val instruction = site.instruction as? MemoryInstruction ?: return null
        val linked = site.linked ?: return null
        val operands = MemoryOperands(linked, snapshot.innermostFrameSlots) ?: return null
        val base = operands.address ?: return null
        val access = when (instruction) {
            is MemoryInstruction.Load -> access(
                kind = MemoryAccess.Kind.READ,
                memoryIndex = instruction.memoryIndex.toInt(),
                address = base + operands.offset,
                width = instruction.width().toLong(),
            )
            is MemoryInstruction.Store -> access(
                kind = MemoryAccess.Kind.WRITE,
                memoryIndex = instruction.memoryIndex.toInt(),
                address = base + operands.offset,
                width = instruction.width().toLong(),
            )
            is MemoryInstruction.MemoryFill -> access(
                kind = MemoryAccess.Kind.FILL,
                memoryIndex = instruction.memoryIndex.toInt(),
                address = base,
                width = operands.length ?: return null,
            )
            is MemoryInstruction.MemoryInit -> {
                val length = operands.length ?: return null
                val source = operands.sourceAddress ?: return null
                val segmentLength = operands.segmentLength ?: return null
                // The source range lies in a data segment, not in memory.
                if (source + length > segmentLength) return null
                access(MemoryAccess.Kind.INIT, instruction.memoryIndex.toInt(), base, length)
            }
            is MemoryInstruction.MemoryCopy -> {
                val length = operands.length ?: return null
                val source = operands.sourceAddress ?: return null
                // The source range is checked first, so prefer it when both are out of bounds.
                access(MemoryAccess.Kind.COPY, instruction.srcIndex.toInt(), source, length)
                    ?.takeIf(::outOfBounds)
                    ?: access(MemoryAccess.Kind.COPY, instruction.dstIndex.toInt(), base, length)
            }
            else -> null
        }
        return access?.takeIf(::outOfBounds)
    }

    private fun access(
        kind: MemoryAccess.Kind,
        memoryIndex: Int,
        address: Long,
        width: Long,
    ): MemoryAccess? {
        val memorySize = snapshot.memorySizes.getOrNull(memoryIndex)?.takeIf { size -> size >= 0 } ?: return null
        return MemoryAccess(kind, memoryIndex, address, width, memorySize)
    }

    private fun outOfBounds(access: MemoryAccess): Boolean = access.address + access.width > access.memorySize
}

private class ModuleResolution(
    val source: TrapSource,
    val ast: AstModule,
    store: RuntimeStore,
) {
    val compiler by lazy { FunctionProvenanceCompiler(store, ast, source.instance) }

    private val names = ast.customs.filterIsInstance<NameData>().flatMap { data -> data.subsections }

    val moduleName: String? = names.filterIsInstance<ModuleNameSubsection>().firstOrNull()?.name?.name

    val functionNames: Map<Int, String> = names.filterIsInstance<FunctionNameSubsection>()
        .flatMap { subsection -> subsection.nameMap }
        .associate { association -> association.idx.toInt() to association.name.name }
}

private class SourceSite(
    val offset: Int,
    val instruction: Instruction,
    val linked: LinkedInstruction?,
)

private class FunctionResolution(
    private val provenance: FunctionProvenance,
    private val body: List<Instruction>,
    private val offsets: IntArray,
) {

    fun site(compiledOffset: Int): SourceSite? {
        if (compiledOffset !in 0 until provenance.size) return null
        val start = provenance.sourceStarts[compiledOffset]
        val end = provenance.sourceEnds[compiledOffset]
        if (start == FunctionProvenance.NO_SOURCE || start !in body.indices || end <= start) return null
        val chosen = (start until minOf(end, body.size)).firstOrNull { index -> body[index].canTrap() } ?: start
        return SourceSite(
            offset = offsets[chosen],
            instruction = body[chosen],
            linked = provenance.linkedInstruction(compiledOffset),
        )
    }
}

private fun exportName(instance: ModuleInstance, address: Int): String? = instance.exports.firstOrNull { export ->
    (export.value as? ExternalValue.Function)?.address?.address == address
}?.name?.name

private fun MemoryInstruction.Load.width(): Int = when (this) {
    is MemoryInstruction.Load.I32.I32Load -> 4
    is MemoryInstruction.Load.I32.I32Load8S, is MemoryInstruction.Load.I32.I32Load8U -> 1
    is MemoryInstruction.Load.I32.I32Load16S, is MemoryInstruction.Load.I32.I32Load16U -> 2
    is MemoryInstruction.Load.I64.I64Load -> 8
    is MemoryInstruction.Load.I64.I64Load8S, is MemoryInstruction.Load.I64.I64Load8U -> 1
    is MemoryInstruction.Load.I64.I64Load16S, is MemoryInstruction.Load.I64.I64Load16U -> 2
    is MemoryInstruction.Load.I64.I64Load32S, is MemoryInstruction.Load.I64.I64Load32U -> 4
    is MemoryInstruction.Load.F32.F32Load -> 4
    is MemoryInstruction.Load.F64.F64Load -> 8
}

private fun MemoryInstruction.Store.width(): Int = when (this) {
    is MemoryInstruction.Store.I32.I32Store -> 4
    is MemoryInstruction.Store.I32.I32Store8 -> 1
    is MemoryInstruction.Store.I32.I32Store16 -> 2
    is MemoryInstruction.Store.I64.I64Store -> 8
    is MemoryInstruction.Store.I64.I64Store8 -> 1
    is MemoryInstruction.Store.I64.I64Store16 -> 2
    is MemoryInstruction.Store.I64.I64Store32 -> 4
    is MemoryInstruction.Store.F32.F32Store -> 4
    is MemoryInstruction.Store.F64.F64Store -> 8
}

private fun reasonOf(error: InvocationError): TrapReason = when (error) {
    InvocationError.Unreachable -> TrapReason.UNREACHABLE
    InvocationError.MemoryOperationOutOfBounds -> TrapReason.MEMORY_OUT_OF_BOUNDS
    InvocationError.CannotDivideIntegerByZero -> TrapReason.INTEGER_DIVIDE_BY_ZERO
    InvocationError.IntegerOverflow -> TrapReason.INTEGER_OVERFLOW
    InvocationError.ConvertOperationFailed -> TrapReason.INVALID_CONVERSION
    InvocationError.TableOperationOutOfBounds -> TrapReason.TABLE_OUT_OF_BOUNDS
    InvocationError.IndirectCallHasIncorrectFunctionType -> TrapReason.INDIRECT_CALL_TYPE_MISMATCH
    InvocationError.NullReferenceExpected,
    InvocationError.NonNullReferenceExpected,
    InvocationError.UnexpectedReferenceValue,
    InvocationError.IndirectCallOnANonFunctionReference,
    -> TrapReason.NULL_REFERENCE
    InvocationError.FailedToCastReference -> TrapReason.CAST_FAILURE
    InvocationError.ArrayOperationOutOfBounds -> TrapReason.ARRAY_OUT_OF_BOUNDS
    InvocationError.CallStackExhausted -> TrapReason.CALL_STACK_EXHAUSTED
    InvocationError.GuestHeapOutOfMemory -> TrapReason.GUEST_HEAP_EXHAUSTED
    InvocationError.FuelExhausted -> TrapReason.FUEL_EXHAUSTED
    InvocationError.Interrupted -> TrapReason.INTERRUPTED
    is InvocationError.HostFunctionError,
    is InvocationError.HostFunctionInconsistentWithType,
    -> TrapReason.HOST_FUNCTION_FAILURE
    InvocationError.ThrownException,
    InvocationError.UncaughtException,
    -> TrapReason.UNCAUGHT_EXCEPTION
    else -> TrapReason.OTHER
}
