package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32CtzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32CtzDispatcher(
    instruction: FusedNumericInstruction.I32Ctz,
) = I32CtzDispatcher(
    instruction = instruction,
    executor = ::I32CtzExecutor,
)

internal inline fun I32CtzDispatcher(
    instruction: FusedNumericInstruction.I32Ctz,
    crossinline executor: Executor<FusedNumericInstruction.I32Ctz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
