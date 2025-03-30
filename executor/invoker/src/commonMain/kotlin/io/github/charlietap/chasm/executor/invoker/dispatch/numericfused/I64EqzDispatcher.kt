package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop.I64EqzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64EqzDispatcher(
    instruction: FusedNumericInstruction.I64Eqz,
) = I64EqzDispatcher(
    instruction = instruction,
    executor = ::I64EqzExecutor,
)

internal inline fun I64EqzDispatcher(
    instruction: FusedNumericInstruction.I64Eqz,
    crossinline executor: Executor<FusedNumericInstruction.I64Eqz>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
