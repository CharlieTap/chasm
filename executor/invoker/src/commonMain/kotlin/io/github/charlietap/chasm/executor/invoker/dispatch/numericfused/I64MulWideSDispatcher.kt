package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64MulWideSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64MulWideSDispatcher(
    instruction: FusedNumericInstruction.I64MulWideS,
) = I64MulWideSDispatcher(
    instruction = instruction,
    executor = ::I64MulWideSExecutor,
)

internal inline fun I64MulWideSDispatcher(
    instruction: FusedNumericInstruction.I64MulWideS,
    crossinline executor: Executor<FusedNumericInstruction.I64MulWideS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
