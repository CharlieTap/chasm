package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RotrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64RotrDispatcher(
    instruction: FusedNumericInstruction.I64Rotr,
) = I64RotrDispatcher(
    instruction = instruction,
    executor = ::I64RotrExecutor,
)

internal inline fun I64RotrDispatcher(
    instruction: FusedNumericInstruction.I64Rotr,
    crossinline executor: Executor<FusedNumericInstruction.I64Rotr>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
