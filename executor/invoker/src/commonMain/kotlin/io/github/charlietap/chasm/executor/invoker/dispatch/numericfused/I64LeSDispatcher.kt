package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64LeSDispatcher(
    instruction: FusedNumericInstruction.I64LeS,
) = I64LeSDispatcher(
    instruction = instruction,
    executor = ::I64LeSExecutor,
)

internal inline fun I64LeSDispatcher(
    instruction: FusedNumericInstruction.I64LeS,
    crossinline executor: Executor<FusedNumericInstruction.I64LeS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
} 
