package io.github.charlietap.chasm.compiler.backend.chasm.diagnostic

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.Provenance
import io.github.charlietap.chasm.compiler.backend.chasm.FunctionLayout
import io.github.charlietap.chasm.compiler.backend.chasm.ProgramFunctionLinker
import io.github.charlietap.chasm.compiler.backend.chasm.link.createLinkContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store

class FunctionProvenance internal constructor(
    private val function: CirFunction,
    private val provenance: Provenance,
    private val layout: FunctionLayout,
) {
    val size: Int
        get() = layout.size

    fun sourceStart(
        offset: Int,
    ): Int = layout.position(offset).let { position ->
        if (position < 0) NO_SOURCE else provenance.sourceStart[position]
    }

    fun sourceEnd(
        offset: Int,
    ): Int = layout.position(offset).let { position ->
        if (position < 0) NO_SOURCE else provenance.sourceEnd[position]
    }

    fun instruction(
        offset: Int,
    ): Instruction? = layout.position(offset).takeIf { it >= 0 }?.let(function.instructions::get)

    companion object {
        const val NO_SOURCE = -1
    }
}

class FunctionProvenanceLinker(
    private val store: Store,
    module: Module,
    instance: ModuleInstance,
) {
    private val context = createLinkContext(store, instance, module.definedTypes)

    fun link(
        function: CirFunction,
        program: Program,
        entryIp: Int,
        endIp: Int,
    ): FunctionProvenance? {
        val provenance = checkNotNull(function.provenance) { "function was compiled without provenance" }
        val linked = Program(maxOf(function.instructions.size, 1))
        ProgramFunctionLinker(context, function, linked)
        if (endIp - entryIp != linked.size || entryIp < 0 || endIp > program.size) return null
        for (offset in 0 until linked.size) {
            if (linked.instructions[offset]::class != program.instructions[entryIp + offset]::class) return null
        }
        return FunctionProvenance(function, provenance, FunctionLayout(function, store))
    }
}
