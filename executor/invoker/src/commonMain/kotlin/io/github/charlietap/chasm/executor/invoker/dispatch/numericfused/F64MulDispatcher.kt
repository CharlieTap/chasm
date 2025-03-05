package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64MulExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64MulDispatcher(
    instruction: FusedNumericInstruction.F64Mul,
) = F64MulDispatcher(
    instruction = instruction,
    executor = ::F64MulExecutor,
)

internal inline fun F64MulDispatcher(
    instruction: FusedNumericInstruction.F64Mul,
    crossinline executor: Executor<FusedNumericInstruction.F64Mul>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
