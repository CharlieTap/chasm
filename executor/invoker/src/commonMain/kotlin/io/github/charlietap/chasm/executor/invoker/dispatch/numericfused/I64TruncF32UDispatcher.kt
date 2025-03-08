package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncF32UDispatcher(
    instruction: FusedNumericInstruction.I64TruncF32U,
) = I64TruncF32UDispatcher(
    instruction = instruction,
    executor = ::I64TruncF32UExecutor,
)

internal inline fun I64TruncF32UDispatcher(
    instruction: FusedNumericInstruction.I64TruncF32U,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncF32U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
