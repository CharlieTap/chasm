package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.F32ReinterpretI32Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32ReinterpretI32Dispatcher(
    instruction: FusedNumericInstruction.F32ReinterpretI32,
) = F32ReinterpretI32Dispatcher(
    instruction = instruction,
    executor = ::F32ReinterpretI32Executor,
)

internal inline fun F32ReinterpretI32Dispatcher(
    instruction: FusedNumericInstruction.F32ReinterpretI32,
    crossinline executor: Executor<FusedNumericInstruction.F32ReinterpretI32>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
