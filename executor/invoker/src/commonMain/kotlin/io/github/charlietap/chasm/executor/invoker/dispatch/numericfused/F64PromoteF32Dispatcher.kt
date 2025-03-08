package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64PromoteF32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64PromoteF32Dispatcher(
    instruction: FusedNumericInstruction.F64PromoteF32,
) = F64PromoteF32Dispatcher(
    instruction = instruction,
    executor = ::F64PromoteF32Executor,
)

internal inline fun F64PromoteF32Dispatcher(
    instruction: FusedNumericInstruction.F64PromoteF32,
    crossinline executor: Executor<FusedNumericInstruction.F64PromoteF32>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
