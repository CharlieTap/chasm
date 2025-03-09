package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncSatF32SDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF32S,
) = I64TruncSatF32SDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF32SExecutor,
)

internal inline fun I64TruncSatF32SDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF32S,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncSatF32S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
