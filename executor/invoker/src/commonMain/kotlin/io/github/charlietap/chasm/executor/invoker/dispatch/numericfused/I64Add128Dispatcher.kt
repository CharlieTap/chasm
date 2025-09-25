package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64Add128Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64Add128Dispatcher(
    instruction: FusedNumericInstruction.I64Add128,
) = I64Add128Dispatcher(
    instruction = instruction,
    executor = ::I64Add128Executor,
)

internal inline fun I64Add128Dispatcher(
    instruction: FusedNumericInstruction.I64Add128,
    crossinline executor: Executor<FusedNumericInstruction.I64Add128>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
