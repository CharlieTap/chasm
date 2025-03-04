package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64LtExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun F64LtDispatcher(
    instruction: FusedNumericInstruction.F64Lt,
) = F64LtDispatcher(
    instruction = instruction,
    executor = ::F64LtExecutor,
)

internal inline fun F64LtDispatcher(
    instruction: FusedNumericInstruction.F64Lt,
    crossinline executor: Executor<FusedNumericInstruction.F64Lt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
