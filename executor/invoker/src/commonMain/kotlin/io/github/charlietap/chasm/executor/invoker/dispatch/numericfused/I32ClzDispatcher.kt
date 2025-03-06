package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32ClzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ClzDispatcher(
    instruction: FusedNumericInstruction.I32Clz,
) = I32ClzDispatcher(
    instruction = instruction,
    executor = ::I32ClzExecutor,
)

internal inline fun I32ClzDispatcher(
    instruction: FusedNumericInstruction.I32Clz,
    crossinline executor: Executor<FusedNumericInstruction.I32Clz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
