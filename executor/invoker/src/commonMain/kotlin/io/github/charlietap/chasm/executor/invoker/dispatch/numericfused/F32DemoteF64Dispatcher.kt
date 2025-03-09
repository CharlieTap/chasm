package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32DemoteF64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32DemoteF64Dispatcher(
    instruction: FusedNumericInstruction.F32DemoteF64,
) = F32DemoteF64Dispatcher(
    instruction = instruction,
    executor = ::F32DemoteF64Executor,
)

internal inline fun F32DemoteF64Dispatcher(
    instruction: FusedNumericInstruction.F32DemoteF64,
    crossinline executor: Executor<FusedNumericInstruction.F32DemoteF64>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
