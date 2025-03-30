package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64ShlExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ShlDispatcher(
    instruction: FusedNumericInstruction.I64Shl,
) = I64ShlDispatcher(
    instruction = instruction,
    executor = ::I64ShlExecutor,
)

internal inline fun I64ShlDispatcher(
    instruction: FusedNumericInstruction.I64Shl,
    crossinline executor: Executor<FusedNumericInstruction.I64Shl>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
