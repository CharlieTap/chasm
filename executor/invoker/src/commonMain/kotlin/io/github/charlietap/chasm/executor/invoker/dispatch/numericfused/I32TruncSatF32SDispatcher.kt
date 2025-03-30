package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncSatF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF32S,
) = I32TruncSatF32SDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF32SExecutor,
)

internal inline fun I32TruncSatF32SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF32S,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncSatF32S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
