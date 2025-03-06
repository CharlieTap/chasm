package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64ClzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ClzDispatcher(
    instruction: FusedNumericInstruction.I64Clz,
) = I64ClzDispatcher(
    instruction = instruction,
    executor = ::I64ClzExecutor,
)

internal inline fun I64ClzDispatcher(
    instruction: FusedNumericInstruction.I64Clz,
    crossinline executor: Executor<FusedNumericInstruction.I64Clz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
