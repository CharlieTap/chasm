package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64PopcntExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64PopcntDispatcher(
    instruction: FusedNumericInstruction.I64Popcnt,
) = I64PopcntDispatcher(
    instruction = instruction,
    executor = ::I64PopcntExecutor,
)

internal inline fun I64PopcntDispatcher(
    instruction: FusedNumericInstruction.I64Popcnt,
    crossinline executor: Executor<FusedNumericInstruction.I64Popcnt>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
