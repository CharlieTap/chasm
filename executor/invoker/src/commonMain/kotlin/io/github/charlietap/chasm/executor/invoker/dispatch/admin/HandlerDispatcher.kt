package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.ExceptionHandlerInstructionExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

internal fun HandlerDispatcher(
    handler: ExceptionHandler,
) = HandlerDispatcher(
    handler = handler,
    executor = ::ExceptionHandlerInstructionExecutor,
)

internal inline fun HandlerDispatcher(
    handler: ExceptionHandler,
    crossinline executor: Executor<AdminInstruction.Handler>,
): DispatchableInstruction = { context ->
    executor(context, AdminInstruction.Handler(handler))
}
