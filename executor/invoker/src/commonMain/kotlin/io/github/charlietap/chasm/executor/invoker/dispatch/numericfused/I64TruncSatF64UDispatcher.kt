package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncSatF64UDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF64U,
) = I64TruncSatF64UDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF64UExecutor,
)

internal inline fun I64TruncSatF64UDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF64U,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncSatF64U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
