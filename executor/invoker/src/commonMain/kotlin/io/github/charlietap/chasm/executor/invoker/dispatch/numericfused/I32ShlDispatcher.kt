package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ShlDispatcher(
    instruction: FusedNumericInstruction.I32Shl,
) = I32ShlDispatcher(
    instruction = instruction,
    executor = ::I32ShlExecutor,
)

internal inline fun I32ShlDispatcher(
    instruction: FusedNumericInstruction.I32Shl,
    crossinline executor: Executor<FusedNumericInstruction.I32Shl>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
