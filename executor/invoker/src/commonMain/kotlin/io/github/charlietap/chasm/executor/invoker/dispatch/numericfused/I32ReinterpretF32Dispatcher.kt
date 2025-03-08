package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32ReinterpretF32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ReinterpretF32Dispatcher(
    instruction: FusedNumericInstruction.I32ReinterpretF32,
) = I32ReinterpretF32Dispatcher(
    instruction = instruction,
    executor = ::I32ReinterpretF32Executor,
)

internal inline fun I32ReinterpretF32Dispatcher(
    instruction: FusedNumericInstruction.I32ReinterpretF32,
    crossinline executor: Executor<FusedNumericInstruction.I32ReinterpretF32>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
