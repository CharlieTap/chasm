package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ExtendI32UDispatcher(
    instruction: NumericInstruction.I64ExtendI32U,
) = I64ExtendI32UDispatcher(
    instruction = instruction,
    executor = ::I64ExtendI32UExecutor,
)

internal inline fun I64ExtendI32UDispatcher(
    instruction: NumericInstruction.I64ExtendI32U,
    crossinline executor: Executor<NumericInstruction.I64ExtendI32U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
