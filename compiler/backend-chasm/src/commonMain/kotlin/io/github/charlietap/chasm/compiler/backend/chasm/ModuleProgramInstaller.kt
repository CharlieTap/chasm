package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.compiler.backend.chasm.ext.localInitialValues
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.compiler.backend.chasm.link.PendingWasmCall
import io.github.charlietap.chasm.compiler.backend.chasm.link.PendingWasmCallLinker
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.function.classifyLocalInitialization
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.Program

internal typealias ModuleProgramInstaller = (Program, LinkContext, ModuleLinkPlan, List<List<PendingWasmCall>>) -> Unit

internal fun ModuleProgramInstaller(
    program: Program,
    context: LinkContext,
    plan: ModuleLinkPlan,
    pendingWasmCalls: List<List<PendingWasmCall>>,
) = ModuleProgramInstaller(
    program = program,
    context = context,
    plan = plan,
    pendingWasmCalls = pendingWasmCalls,
    pendingWasmCallLinker = ::PendingWasmCallLinker,
)

internal inline fun ModuleProgramInstaller(
    program: Program,
    context: LinkContext,
    plan: ModuleLinkPlan,
    pendingWasmCalls: List<List<PendingWasmCall>>,
    crossinline pendingWasmCallLinker: PendingWasmCallLinker,
) {
    val baseIp = plan.baseIp
    val output = plan.output
    check(program.size == baseIp) { "program changed while the module was linked" }
    try {
        for (index in plan.functions.indices) {
            val function = plan.functions[index]
            val callStrategy = (context.functions[function.index.index] as FunctionInstance.WasmFunction).callStrategy
            callStrategy.frameSlots = plan.frameSlots[index]
            callStrategy.localInitialization = classifyLocalInitialization(function.frame.localInitialValues())
            callStrategy.entryIp = plan.entryIps[index]
        }
        for (calls in pendingWasmCalls) {
            for (call in calls) output[call.ip - baseIp] = pendingWasmCallLinker(call)
        }
        @Suppress("UNCHECKED_CAST")
        program.append(output as Array<DispatchableInstruction>, output.size)
        for (table in plan.exceptionTables) table?.let(program::registerExceptionTable)
    } catch (exception: Throwable) {
        program.truncate(baseIp)
        for (function in plan.functions) {
            (context.functions.getOrNull(function.index.index) as? FunctionInstance.WasmFunction)?.callStrategy?.entryIp = -1
        }
        throw exception
    }
}
