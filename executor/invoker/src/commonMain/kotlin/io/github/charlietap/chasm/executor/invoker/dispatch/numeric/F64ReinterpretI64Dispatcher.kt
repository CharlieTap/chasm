package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F64ReinterpretI64Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64ReinterpretI64Dispatcher(
    instruction: NumericInstruction.F64ReinterpretI64,
) = F64ReinterpretI64Dispatcher(
    instruction = instruction,
    executor = ::F64ReinterpretI64Executor,
)

internal inline fun F64ReinterpretI64Dispatcher(
    instruction: NumericInstruction.F64ReinterpretI64,
    crossinline executor: Executor<NumericInstruction.F64ReinterpretI64>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
