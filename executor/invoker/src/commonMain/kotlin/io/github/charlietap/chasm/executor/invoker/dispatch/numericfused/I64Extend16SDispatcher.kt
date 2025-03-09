package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64Extend16SDispatcher(
    instruction: FusedNumericInstruction.I64Extend16S,
) = I64Extend16SDispatcher(
    instruction = instruction,
    executor = ::I64Extend16SExecutor,
)

internal inline fun I64Extend16SDispatcher(
    instruction: FusedNumericInstruction.I64Extend16S,
    crossinline executor: Executor<FusedNumericInstruction.I64Extend16S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
