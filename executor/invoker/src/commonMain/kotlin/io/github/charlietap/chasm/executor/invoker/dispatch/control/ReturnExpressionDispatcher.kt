package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExpressionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnExpressionDispatcher(
    instruction: ControlInstruction.ReturnExpression,
) = ReturnExpressionDispatcher(
    instruction = instruction,
    executor = ::ReturnExpressionExecutor,
)

internal inline fun ReturnExpressionDispatcher(
    instruction: ControlInstruction.ReturnExpression,
    crossinline executor: Executor<ControlInstruction.ReturnExpression>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
