package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64RemUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64RemUDispatcher(
    instruction: FusedNumericInstruction.I64RemU,
) = I64RemUDispatcher(
    instruction = instruction,
    executor = ::I64RemUExecutor,
)

internal inline fun I64RemUDispatcher(
    instruction: FusedNumericInstruction.I64RemU,
    crossinline executor: Executor<FusedNumericInstruction.I64RemU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
