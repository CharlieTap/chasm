package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64EqDispatcher(
    instruction: FusedNumericInstruction.I64Eq,
) = I64EqDispatcher(
    instruction = instruction,
    executor = ::I64EqExecutor,
)

internal inline fun I64EqDispatcher(
    instruction: FusedNumericInstruction.I64Eq,
    crossinline executor: Executor<FusedNumericInstruction.I64Eq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
} 
