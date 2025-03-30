package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncF64SDispatcher(
    instruction: FusedNumericInstruction.I64TruncF64S,
) = I64TruncF64SDispatcher(
    instruction = instruction,
    executor = ::I64TruncF64SExecutor,
)

internal inline fun I64TruncF64SDispatcher(
    instruction: FusedNumericInstruction.I64TruncF64S,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncF64S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
