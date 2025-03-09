package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64SubDispatcher(
    instruction: FusedNumericInstruction.I64Sub,
) = I64SubDispatcher(
    instruction = instruction,
    executor = ::I64SubExecutor,
)

internal inline fun I64SubDispatcher(
    instruction: FusedNumericInstruction.I64Sub,
    crossinline executor: Executor<FusedNumericInstruction.I64Sub>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
