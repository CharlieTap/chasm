package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncSatF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64U,
) = I32TruncSatF64UDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF64UExecutor,
)

internal inline fun I32TruncSatF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncSatF64U,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncSatF64U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
