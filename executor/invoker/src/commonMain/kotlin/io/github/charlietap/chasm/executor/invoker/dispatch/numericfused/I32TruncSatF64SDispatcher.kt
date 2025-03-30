package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncSatF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64S,
) = I32TruncSatF64SDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF64SExecutor,
)

internal inline fun I32TruncSatF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64S,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncSatF64S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
