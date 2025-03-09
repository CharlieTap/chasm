package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64DivUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64DivUDispatcher(
    instruction: FusedNumericInstruction.I64DivU,
) = I64DivUDispatcher(
    instruction = instruction,
    executor = ::I64DivUExecutor,
)

internal inline fun I64DivUDispatcher(
    instruction: FusedNumericInstruction.I64DivU,
    crossinline executor: Executor<FusedNumericInstruction.I64DivU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
