package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64Sub128Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64Sub128Dispatcher(
    instruction: FusedNumericInstruction.I64Sub128,
) = I64Sub128Dispatcher(
    instruction = instruction,
    executor = ::I64Sub128Executor,
)

internal inline fun I64Sub128Dispatcher(
    instruction: FusedNumericInstruction.I64Sub128,
    crossinline executor: Executor<FusedNumericInstruction.I64Sub128>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
