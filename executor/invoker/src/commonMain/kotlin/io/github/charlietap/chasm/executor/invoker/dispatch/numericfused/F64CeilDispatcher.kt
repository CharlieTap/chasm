package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64CeilExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64CeilDispatcher(
    instruction: FusedNumericInstruction.F64Ceil,
) = F64CeilDispatcher(
    instruction = instruction,
    executor = ::F64CeilExecutor,
)

internal inline fun F64CeilDispatcher(
    instruction: FusedNumericInstruction.F64Ceil,
    crossinline executor: Executor<FusedNumericInstruction.F64Ceil>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
