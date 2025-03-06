package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32GeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32GeDispatcher(
    instruction: FusedNumericInstruction.F32Ge,
) = F32GeDispatcher(
    instruction = instruction,
    executor = ::F32GeExecutor,
)

internal inline fun F32GeDispatcher(
    instruction: FusedNumericInstruction.F32Ge,
    crossinline executor: Executor<FusedNumericInstruction.F32Ge>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
} 
