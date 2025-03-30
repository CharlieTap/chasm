package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32Extend16SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32Extend16SDispatcher(
    instruction: FusedNumericInstruction.I32Extend16S,
) = I32Extend16SDispatcher(
    instruction = instruction,
    executor = ::I32Extend16SExecutor,
)

internal inline fun I32Extend16SDispatcher(
    instruction: FusedNumericInstruction.I32Extend16S,
    crossinline executor: Executor<FusedNumericInstruction.I32Extend16S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
