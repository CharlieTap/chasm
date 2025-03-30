package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ReinterpretF64Executor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ReinterpretF64Dispatcher(
    instruction: FusedNumericInstruction.I64ReinterpretF64,
) = I64ReinterpretF64Dispatcher(
    instruction = instruction,
    executor = ::I64ReinterpretF64Executor,
)

internal inline fun I64ReinterpretF64Dispatcher(
    instruction: FusedNumericInstruction.I64ReinterpretF64,
    crossinline executor: Executor<FusedNumericInstruction.I64ReinterpretF64>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
