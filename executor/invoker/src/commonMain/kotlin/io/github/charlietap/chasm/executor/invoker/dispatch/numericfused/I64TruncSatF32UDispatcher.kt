package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncSatF32UDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF32U,
) = I64TruncSatF32UDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF32UExecutor,
)

internal inline fun I64TruncSatF32UDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF32U,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncSatF32U>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
