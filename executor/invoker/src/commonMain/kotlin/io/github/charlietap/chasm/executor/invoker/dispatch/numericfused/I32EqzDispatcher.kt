package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop.I32EqzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32EqzDispatcher(
    instruction: FusedNumericInstruction.I32Eqz,
) = I32EqzDispatcher(
    instruction = instruction,
    executor = ::I32EqzExecutor,
)

internal inline fun I32EqzDispatcher(
    instruction: FusedNumericInstruction.I32Eqz,
    crossinline executor: Executor<FusedNumericInstruction.I32Eqz>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
