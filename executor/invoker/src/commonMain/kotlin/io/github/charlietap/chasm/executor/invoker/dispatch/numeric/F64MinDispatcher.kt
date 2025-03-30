package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MinExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64MinDispatcher(
    instruction: NumericInstruction.F64Min,
) = F64MinDispatcher(
    instruction = instruction,
    executor = ::F64MinExecutor,
)

internal inline fun F64MinDispatcher(
    instruction: NumericInstruction.F64Min,
    crossinline executor: Executor<NumericInstruction.F64Min>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
