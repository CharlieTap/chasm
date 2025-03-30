package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64TruncSatF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64TruncSatF64SDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF64S,
) = I64TruncSatF64SDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF64SExecutor,
)

internal inline fun I64TruncSatF64SDispatcher(
    instruction: FusedNumericInstruction.I64TruncSatF64S,
    crossinline executor: Executor<FusedNumericInstruction.I64TruncSatF64S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
