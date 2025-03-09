package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32WrapI64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32WrapI64Dispatcher(
    instruction: FusedNumericInstruction.I32WrapI64,
) = I32WrapI64Dispatcher(
    instruction = instruction,
    executor = ::I32WrapI64Executor,
)

internal inline fun I32WrapI64Dispatcher(
    instruction: FusedNumericInstruction.I32WrapI64,
    crossinline executor: Executor<FusedNumericInstruction.I32WrapI64>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
