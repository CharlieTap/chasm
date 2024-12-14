package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ReinterpretF64Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64ReinterpretF64Dispatcher(
    instruction: NumericInstruction.I64ReinterpretF64,
) = I64ReinterpretF64Dispatcher(
    instruction = instruction,
    executor = ::I64ReinterpretF64Executor,
)

internal inline fun I64ReinterpretF64Dispatcher(
    instruction: NumericInstruction.I64ReinterpretF64,
    crossinline executor: Executor<NumericInstruction.I64ReinterpretF64>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}