package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F64ReinterpretI64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64ReinterpretI64Dispatcher(
    instruction: FusedNumericInstruction.F64ReinterpretI64,
) = F64ReinterpretI64Dispatcher(
    instruction = instruction,
    executor = ::F64ReinterpretI64Executor,
)

internal inline fun F64ReinterpretI64Dispatcher(
    instruction: FusedNumericInstruction.F64ReinterpretI64,
    crossinline executor: Executor<FusedNumericInstruction.F64ReinterpretI64>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
