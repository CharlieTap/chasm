package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RotlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64RotlDispatcher(
    instruction: FusedNumericInstruction.I64Rotl,
) = I64RotlDispatcher(
    instruction = instruction,
    executor = ::I64RotlExecutor,
)

internal inline fun I64RotlDispatcher(
    instruction: FusedNumericInstruction.I64Rotl,
    crossinline executor: Executor<FusedNumericInstruction.I64Rotl>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
