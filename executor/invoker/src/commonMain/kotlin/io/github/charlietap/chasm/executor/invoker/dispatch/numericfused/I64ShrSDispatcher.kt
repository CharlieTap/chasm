package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64ShrSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ShrSDispatcher(
    instruction: FusedNumericInstruction.I64ShrS,
) = I64ShrSDispatcher(
    instruction = instruction,
    executor = ::I64ShrSExecutor,
)

internal inline fun I64ShrSDispatcher(
    instruction: FusedNumericInstruction.I64ShrS,
    crossinline executor: Executor<FusedNumericInstruction.I64ShrS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
