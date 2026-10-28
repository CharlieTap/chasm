@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding.diagnostic

import com.github.michaelbull.result.get
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.ModuleNameSubsection
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.diagnostic.FunctionProvenance
import io.github.charlietap.chasm.compiler.diagnostic.FunctionProvenanceCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.MemoryAccess
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.ast.module.Module as AstModule
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal class InterpreterTrapSource private constructor(
    override val instance: ModuleInstance,
    private val binary: ByteArray,
    private val config: ModuleConfig,
) : TrapSource {

    override fun frames(
        runtime: RuntimeStore,
        snapshot: TrapSnapshot,
    ): ModuleFrames? {
        val module = WasmModuleDecoder(config.copy(decodeNameSection = true), binary).get() ?: return null
        return InterpreterFrames(instance, binary, config, module, runtime, snapshot)
    }

    companion object {
        operator fun invoke(
            instance: ModuleInstance,
            module: Module,
        ): TrapSource? {
            val binary = module.binary ?: return null
            return InterpreterTrapSource(instance, binary, module.config)
        }
    }
}

private class InterpreterFrames(
    private val instance: ModuleInstance,
    private val binary: ByteArray,
    private val config: ModuleConfig,
    private val module: AstModule,
    private val runtime: RuntimeStore,
    private val snapshot: TrapSnapshot,
) : ModuleFrames {

    private val names = module.customs.filterIsInstance<NameData>().flatMap { data -> data.subsections }
    private val functionNames = names.filterIsInstance<FunctionNameSubsection>()
        .flatMap { subsection -> subsection.nameMap }
        .associate { association -> association.idx.toInt() to association.name.name }

    private val compiler by lazy { FunctionProvenanceCompiler(runtime, module, instance) }
    private val functions = HashMap<Int, FunctionResolution?>()

    override val moduleName: String? = names.filterIsInstance<ModuleNameSubsection>().firstOrNull()?.name?.name

    override fun functionName(
        functionIndex: Int,
    ): String? = functionNames[functionIndex]

    override fun site(
        address: Int,
        functionIndex: Int,
        frameIndex: Int,
    ): TrapSite? {
        val resolution = if (functions.containsKey(address)) {
            functions[address]
        } else {
            resolve(functionIndex, frameIndex).also { functions[address] = it }
        }
        return resolution?.site(snapshot.ips[frameIndex] - snapshot.entryIps[frameIndex], this)
    }

    private fun resolve(
        functionIndex: Int,
        frameIndex: Int,
    ): FunctionResolution? {
        val definedIndex = module.functions.indexOfFirst { function -> function.idx.toInt() == functionIndex }
        if (definedIndex < 0) return null
        val provenance = compiler.compile(definedIndex).get()
            ?.takeIf { compiled ->
                compiled.matchesInstalled(runtime.program, snapshot.entryIps[frameIndex], snapshot.endIps[frameIndex])
            } ?: return null
        val body = module.functions[definedIndex].body.instructions
        val offsets = WasmFunctionBodyOffsets(config, binary, definedIndex)
            ?.takeIf { offsets -> offsets.size == body.size } ?: return null
        return FunctionResolution(provenance, body, offsets)
    }

    /** Null unless the reconstructed range is actually out of bounds. */
    fun memoryAccess(
        source: SourceSite,
    ): MemoryAccess? {
        val instruction = source.instruction as? MemoryInstruction ?: return null
        val linked = source.linked ?: return null
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
                val segment = operands.sourceAddress ?: return null
                val segmentLength = operands.segmentLength ?: return null
                // The source range lies in a data segment, not in memory.
                if (segment + length > segmentLength) return null
                access(MemoryAccess.Kind.INIT, instruction.memoryIndex.toInt(), base, length)
            }
            is MemoryInstruction.MemoryCopy -> {
                val length = operands.length ?: return null
                val from = operands.sourceAddress ?: return null
                // The source range is checked first, so prefer it when both are out of bounds.
                access(MemoryAccess.Kind.COPY, instruction.srcIndex.toInt(), from, length)
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

    private fun outOfBounds(
        access: MemoryAccess,
    ): Boolean = access.address + access.width > access.memorySize
}

private class SourceSite(
    override val offset: Int,
    val instruction: Instruction,
    val linked: LinkedInstruction?,
    private val frames: InterpreterFrames,
) : TrapSite {
    override fun memoryAccess(): MemoryAccess? = frames.memoryAccess(this)
}

private class FunctionResolution(
    private val provenance: FunctionProvenance,
    private val body: List<Instruction>,
    private val offsets: IntArray,
) {

    fun site(
        compiledOffset: Int,
        frames: InterpreterFrames,
    ): SourceSite? {
        if (compiledOffset !in 0 until provenance.size) return null
        val start = provenance.sourceStarts[compiledOffset]
        val end = provenance.sourceEnds[compiledOffset]
        if (start == FunctionProvenance.NO_SOURCE || start !in body.indices || end <= start) return null
        val chosen = (start until minOf(end, body.size)).firstOrNull { index -> body[index].canTrap() } ?: start
        return SourceSite(
            offset = offsets[chosen],
            instruction = body[chosen],
            linked = provenance.linkedInstruction(compiledOffset),
            frames = frames,
        )
    }
}

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
