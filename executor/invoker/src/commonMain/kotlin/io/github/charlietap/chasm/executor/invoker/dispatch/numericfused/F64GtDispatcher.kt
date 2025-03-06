package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64GtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64GtDispatcher(
    instruction: FusedNumericInstruction.F64Gt,
) = F64GtDispatcher(
    instruction = instruction,
    executor = ::F64GtExecutor,
)

internal inline fun F64GtDispatcher(
    instruction: FusedNumericInstruction.F64Gt,
    crossinline executor: Executor<FusedNumericInstruction.F64Gt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
} 
