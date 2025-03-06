package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64AbsExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64AbsDispatcher(
    instruction: FusedNumericInstruction.F64Abs,
) = F64AbsDispatcher(
    instruction = instruction,
    executor = ::F64AbsExecutor,
)

internal inline fun F64AbsDispatcher(
    instruction: FusedNumericInstruction.F64Abs,
    crossinline executor: Executor<FusedNumericInstruction.F64Abs>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
