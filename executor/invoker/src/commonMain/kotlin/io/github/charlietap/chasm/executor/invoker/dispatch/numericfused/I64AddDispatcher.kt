package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64AddExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun I64AddDispatcher(
    instruction: FusedNumericInstruction.I64Add,
) = I64AddDispatcher(
    instruction = instruction,
    executor = ::I64AddExecutor,
)

internal inline fun I64AddDispatcher(
    instruction: FusedNumericInstruction.I64Add,
    crossinline executor: Executor<FusedNumericInstruction.I64Add>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
