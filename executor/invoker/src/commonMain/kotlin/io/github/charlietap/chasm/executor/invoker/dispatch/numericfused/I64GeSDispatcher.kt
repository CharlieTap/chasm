package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64GeSDispatcher(
    instruction: FusedNumericInstruction.I64GeS,
) = I64GeSDispatcher(
    instruction = instruction,
    executor = ::I64GeSExecutor,
)

internal inline fun I64GeSDispatcher(
    instruction: FusedNumericInstruction.I64GeS,
    crossinline executor: Executor<FusedNumericInstruction.I64GeS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
