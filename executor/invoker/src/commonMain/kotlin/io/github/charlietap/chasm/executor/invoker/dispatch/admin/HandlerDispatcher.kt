package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.ExceptionHandlerInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

internal fun HandlerDispatcher(
    handler: ExceptionHandler,
) = HandlerDispatcher(
    handler = handler,
    executor = ::ExceptionHandlerInstructionExecutor,
)

internal inline fun HandlerDispatcher(
    handler: ExceptionHandler,
    crossinline executor: Executor<AdminInstruction.Handler>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, AdminInstruction.Handler(handler))
}
