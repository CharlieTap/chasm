package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64Extend8SDispatcher(
    instruction: FusedNumericInstruction.I64Extend8S,
) = I64Extend8SDispatcher(
    instruction = instruction,
    executor = ::I64Extend8SExecutor,
)

internal inline fun I64Extend8SDispatcher(
    instruction: FusedNumericInstruction.I64Extend8S,
    crossinline executor: Executor<FusedNumericInstruction.I64Extend8S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
