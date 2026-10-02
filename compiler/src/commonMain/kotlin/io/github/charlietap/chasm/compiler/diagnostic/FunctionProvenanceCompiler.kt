package io.github.charlietap.chasm.compiler.diagnostic

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.FunctionCompiler
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.context.createCompilerContext
import io.github.charlietap.chasm.compiler.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.program.SourceProvenanceRecorder
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LinkWasmCallDispatchers
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver

/**
 * Offsets are relative to the function's entry. [sourceStarts] and
 * [sourceEnds] give the half-open range of body instructions being compiled
 * when each instruction was emitted, or [NO_SOURCE] for synthesised code.
 */
class FunctionProvenance internal constructor(
    val instructions: Array<DispatchableInstruction>,
    val sourceStarts: IntArray,
    val sourceEnds: IntArray,
    private val emitted: List<DispatchableInstruction>,
    private val linked: List<LinkedInstruction>,
) {
    val size: Int
        get() = instructions.size

    fun linkedInstruction(offset: Int): LinkedInstruction? {
        val instruction = instructions.getOrNull(offset) ?: return null
        for (index in emitted.indices.reversed()) {
            if (emitted[index] === instruction) return linked[index]
        }
        return null
    }

    /** Compares length and the dispatcher class at each offset. */
    fun matchesInstalled(
        program: Program,
        entryIp: Int,
        endIp: Int,
    ): Boolean {
        if (endIp - entryIp != instructions.size || endIp > program.size) return false
        for (offset in instructions.indices) {
            if (instructions[offset]::class != program.instructions[entryIp + offset]::class) return false
        }
        return true
    }

    companion object {
        const val NO_SOURCE = SourceProvenanceRecorder.NO_SOURCE
    }
}

/**
 * Compilation is deterministic for the same module, instance and store, so
 * callers check [FunctionProvenance.matchesInstalled] before trusting a result.
 * The module-wide compiler context is built once and shared by every [compile].
 */
class FunctionProvenanceCompiler(
    store: Store,
    private val module: Module,
    instance: ModuleInstance,
) {
    private var emitted = ArrayList<DispatchableInstruction>()
    private var linked = ArrayList<LinkedInstruction>()
    private val workspace = FunctionCompilerWorkspace()
    private val context = createCompilerContext(
        module = module,
        types = ModuleTypeResolver(module),
        store = store,
        instance = instance,
        runtimeTypes = instance.runtimeTypes,
        diagnostics = CompilerDiagnostics(instructionObserver = ::observe),
    )

    fun compile(definedFunctionIndex: Int): Result<FunctionProvenance, ModuleTrapError> = binding {
        emitted = ArrayList()
        linked = ArrayList()
        val function = module.functions[definedFunctionIndex]
        val program = Program(maxOf(function.body.instructions.size, 1))
        val recorder = SourceProvenanceRecorder()
        val builder = ProgramBuilder(program).also { it.provenance = recorder }
        FunctionCompiler(context, function, program, workspace, builder).bind()
        LinkWasmCallDispatchers(program, 0, ::observe)

        val instructions = Array(program.size) { offset -> program.instructions[offset] }
        val starts = recorder.sourceStarts().copyOf(program.size)
        val ends = recorder.sourceEnds().copyOf(program.size)
        for (offset in recorder.size until program.size) {
            starts[offset] = FunctionProvenance.NO_SOURCE
            ends[offset] = FunctionProvenance.NO_SOURCE
        }
        FunctionProvenance(
            instructions = instructions,
            sourceStarts = starts,
            sourceEnds = ends,
            emitted = emitted,
            linked = linked,
        )
    }

    private fun observe(
        dispatchableInstruction: DispatchableInstruction,
        instruction: LinkedInstruction,
    ) {
        emitted.add(dispatchableInstruction)
        linked.add(instruction)
    }
}
