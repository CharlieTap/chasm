package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64TruncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64TruncDispatcher(
    instruction: FusedNumericInstruction.F64Trunc,
) = F64TruncDispatcher(
    instruction = instruction,
    executor = ::F64TruncExecutor,
)

internal inline fun F64TruncDispatcher(
    instruction: FusedNumericInstruction.F64Trunc,
    crossinline executor: Executor<FusedNumericInstruction.F64Trunc>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
