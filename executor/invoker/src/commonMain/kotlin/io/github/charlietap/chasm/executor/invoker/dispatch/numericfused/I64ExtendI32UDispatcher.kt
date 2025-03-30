package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I64ExtendI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ExtendI32UDispatcher(
    instruction: FusedNumericInstruction.I64ExtendI32U,
) = I64ExtendI32UDispatcher(
    instruction = instruction,
    executor = ::I64ExtendI32UExecutor,
)

internal inline fun I64ExtendI32UDispatcher(
    instruction: FusedNumericInstruction.I64ExtendI32U,
    crossinline executor: Executor<FusedNumericInstruction.I64ExtendI32U>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
