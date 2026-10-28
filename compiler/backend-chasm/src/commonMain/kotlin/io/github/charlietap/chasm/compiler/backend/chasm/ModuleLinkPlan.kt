package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable

internal class ModuleLinkPlan(
    val functions: List<CirFunction>,
    val baseIp: Int,
    val entryIps: IntArray,
    val output: Array<DispatchableInstruction?>,
) {
    val exceptionTables = arrayOfNulls<FunctionExceptionTable>(functions.size)
    val frameSlots = IntArray(functions.size)

    fun link(
        context: LinkContext,
        index: Int,
        functionLinker: FunctionLinker,
    ) {
        exceptionTables[index] = functionLinker(context, functions[index], entryIps[index], output, baseIp)
        frameSlots[index] = context.frameSlots
    }
}
