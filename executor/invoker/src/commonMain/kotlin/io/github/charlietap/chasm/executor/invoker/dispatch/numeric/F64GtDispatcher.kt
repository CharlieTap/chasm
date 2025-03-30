package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64GtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64GtDispatcher(
    instruction: NumericInstruction.F64Gt,
) = F64GtDispatcher(
    instruction = instruction,
    executor = ::F64GtExecutor,
)

internal inline fun F64GtDispatcher(
    instruction: NumericInstruction.F64Gt,
    crossinline executor: Executor<NumericInstruction.F64Gt>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
