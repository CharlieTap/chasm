package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64CtzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64CtzDispatcher(
    instruction: FusedNumericInstruction.I64Ctz,
) = I64CtzDispatcher(
    instruction = instruction,
    executor = ::I64CtzExecutor,
)

internal inline fun I64CtzDispatcher(
    instruction: FusedNumericInstruction.I64Ctz,
    crossinline executor: Executor<FusedNumericInstruction.I64Ctz>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
