package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncSatF64UDispatcher(
    instruction: NumericInstruction.I64TruncSatF64U,
) = I64TruncSatF64UDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF64UExecutor,
)

internal inline fun I64TruncSatF64UDispatcher(
    instruction: NumericInstruction.I64TruncSatF64U,
    crossinline executor: Executor<NumericInstruction.I64TruncSatF64U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
