package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeS,
) = I64GeSDispatcher(
    instruction = instruction,
    executor = ::I64GeSExecutor,
)

internal inline fun I64GeSDispatcher(
    instruction: NumericInstruction.I64GeS,
    crossinline executor: Executor<NumericInstruction.I64GeS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
