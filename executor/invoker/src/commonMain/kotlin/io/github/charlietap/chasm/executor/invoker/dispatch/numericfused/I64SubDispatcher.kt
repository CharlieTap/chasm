package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64SubExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun I64SubDispatcher(
    instruction: FusedNumericInstruction.I64Sub,
) = I64SubDispatcher(
    instruction = instruction,
    executor = ::I64SubExecutor,
)

internal inline fun I64SubDispatcher(
    instruction: FusedNumericInstruction.I64Sub,
    crossinline executor: Executor<FusedNumericInstruction.I64Sub>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
