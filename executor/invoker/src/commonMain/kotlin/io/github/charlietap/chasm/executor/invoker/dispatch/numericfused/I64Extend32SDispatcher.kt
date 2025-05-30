package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64Extend32SDispatcher(
    instruction: FusedNumericInstruction.I64Extend32S,
) = I64Extend32SDispatcher(
    instruction = instruction,
    executor = ::I64Extend32SExecutor,
)

internal inline fun I64Extend32SDispatcher(
    instruction: FusedNumericInstruction.I64Extend32S,
    crossinline executor: Executor<FusedNumericInstruction.I64Extend32S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
