package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RotlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32RotlDispatcher(
    instruction: FusedNumericInstruction.I32Rotl,
) = I32RotlDispatcher(
    instruction = instruction,
    executor = ::I32RotlExecutor,
)

internal inline fun I32RotlDispatcher(
    instruction: FusedNumericInstruction.I32Rotl,
    crossinline executor: Executor<FusedNumericInstruction.I32Rotl>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
