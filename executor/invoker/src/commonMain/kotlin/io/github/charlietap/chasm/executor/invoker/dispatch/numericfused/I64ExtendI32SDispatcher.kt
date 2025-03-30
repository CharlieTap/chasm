package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ExtendI32SDispatcher(
    instruction: FusedNumericInstruction.I64ExtendI32S,
) = I64ExtendI32SDispatcher(
    instruction = instruction,
    executor = ::I64ExtendI32SExecutor,
)

internal inline fun I64ExtendI32SDispatcher(
    instruction: FusedNumericInstruction.I64ExtendI32S,
    crossinline executor: Executor<FusedNumericInstruction.I64ExtendI32S>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
