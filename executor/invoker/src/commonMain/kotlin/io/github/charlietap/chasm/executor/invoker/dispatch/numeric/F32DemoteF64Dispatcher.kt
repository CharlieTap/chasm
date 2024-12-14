package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32DemoteF64Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32DemoteF64Dispatcher(
    instruction: NumericInstruction.F32DemoteF64,
) = F32DemoteF64Dispatcher(
    instruction = instruction,
    executor = ::F32DemoteF64Executor,
)

internal inline fun F32DemoteF64Dispatcher(
    instruction: NumericInstruction.F32DemoteF64,
    crossinline executor: Executor<NumericInstruction.F32DemoteF64>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
