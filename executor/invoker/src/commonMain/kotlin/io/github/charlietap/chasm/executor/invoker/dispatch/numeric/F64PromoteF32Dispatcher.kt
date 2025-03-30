package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64PromoteF32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64PromoteF32Dispatcher(
    instruction: NumericInstruction.F64PromoteF32,
) = F64PromoteF32Dispatcher(
    instruction = instruction,
    executor = ::F64PromoteF32Executor,
)

internal inline fun F64PromoteF32Dispatcher(
    instruction: NumericInstruction.F64PromoteF32,
    crossinline executor: Executor<NumericInstruction.F64PromoteF32>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
