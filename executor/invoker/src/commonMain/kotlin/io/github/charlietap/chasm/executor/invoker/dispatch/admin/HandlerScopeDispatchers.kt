package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.PopHandlerInstructionExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.admin.PushHandlerInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun PushHandlerDispatcher(
    instruction: AdminInstruction.PushHandler,
) = PushHandlerDispatcher(
    instruction = instruction,
    executor = ::PushHandlerInstructionExecutor,
)

internal inline fun PushHandlerDispatcher(
    instruction: AdminInstruction.PushHandler,
    crossinline executor: Executor<AdminInstruction.PushHandler>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}

fun PopHandlerDispatcher(
    instruction: AdminInstruction.PopHandler,
) = PopHandlerDispatcher(
    instruction = instruction,
    executor = ::PopHandlerInstructionExecutor,
)

internal inline fun PopHandlerDispatcher(
    instruction: AdminInstruction.PopHandler,
    crossinline executor: Executor<AdminInstruction.PopHandler>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
