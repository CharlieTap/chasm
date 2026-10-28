package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.executor.invoker.dispatch.admin.FuelAndInterruptCheckDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.FuelCheckDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.InterruptCheckDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal typealias CheckpointLinker = (LinkContext) -> DispatchableInstruction?

internal fun CheckpointLinker(
    context: LinkContext,
): DispatchableInstruction? {
    val fuel = context.fuel
    val interrupt = context.interrupt
    return when {
        fuel != null && interrupt != null -> FuelAndInterruptCheckDispatcher(fuel, interrupt)
        fuel != null -> FuelCheckDispatcher(fuel)
        interrupt != null -> InterruptCheckDispatcher(interrupt)
        else -> null
    }
}
