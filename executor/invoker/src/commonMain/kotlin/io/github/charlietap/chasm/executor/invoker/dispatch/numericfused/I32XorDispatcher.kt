package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32XOrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32XorDispatcher(
    instruction: FusedNumericInstruction.I32Xor,
) = I32XorDispatcher(
    instruction = instruction,
    executor = ::I32XOrExecutor,
)

internal inline fun I32XorDispatcher(
    instruction: FusedNumericInstruction.I32Xor,
    crossinline executor: Executor<FusedNumericInstruction.I32Xor>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
