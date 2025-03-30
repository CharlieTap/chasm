package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncF64UDispatcher(
    instruction: FusedNumericInstruction.I64TruncF64U,
) = I64TruncF64UDispatcher(
    instruction = instruction,
    executor = ::I64TruncF64UExecutor,
)

internal inline fun I64TruncF64UDispatcher(
    instruction: FusedNumericInstruction.I64TruncF64U,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncF64U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
