package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RotrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32RotrDispatcher(
    instruction: FusedNumericInstruction.I32Rotr,
) = I32RotrDispatcher(
    instruction = instruction,
    executor = ::I32RotrExecutor,
)

internal inline fun I32RotrDispatcher(
    instruction: FusedNumericInstruction.I32Rotr,
    crossinline executor: Executor<FusedNumericInstruction.I32Rotr>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
