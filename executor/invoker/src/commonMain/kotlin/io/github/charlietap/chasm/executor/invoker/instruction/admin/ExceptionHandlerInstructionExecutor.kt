package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun ExceptionHandlerInstructionExecutor(
    context: ExecutionContext,
    instruction: AdminInstruction.Handler,
) {
    context.cstack.popHandler()
}
