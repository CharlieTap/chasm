package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32AddDispatcher(
    instruction: FusedNumericInstruction.F32Add,
) = F32AddDispatcher(
    instruction = instruction,
    executor = ::F32AddExecutor,
)

internal inline fun F32AddDispatcher(
    instruction: FusedNumericInstruction.F32Add,
    crossinline executor: Executor<FusedNumericInstruction.F32Add>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
