package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShrSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ShrSDispatcher(
    instruction: FusedNumericInstruction.I32ShrS,
) = I32ShrSDispatcher(
    instruction = instruction,
    executor = ::I32ShrSExecutor,
)

internal inline fun I32ShrSDispatcher(
    instruction: FusedNumericInstruction.I32ShrS,
    crossinline executor: Executor<FusedNumericInstruction.I32ShrS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
