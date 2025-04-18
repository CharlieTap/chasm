package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64MulDispatcher(
    instruction: FusedNumericInstruction.I64Mul,
) = I64MulDispatcher(
    instruction = instruction,
    executor = ::I64MulExecutor,
)

internal inline fun I64MulDispatcher(
    instruction: FusedNumericInstruction.I64Mul,
    crossinline executor: Executor<FusedNumericInstruction.I64Mul>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
