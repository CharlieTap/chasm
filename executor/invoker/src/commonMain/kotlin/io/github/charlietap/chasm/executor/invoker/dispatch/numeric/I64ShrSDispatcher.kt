package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShrSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64ShrSDispatcher(
    instruction: NumericInstruction.I64ShrS,
) = I64ShrSDispatcher(
    instruction = instruction,
    executor = ::I64ShrSExecutor,
)

internal inline fun I64ShrSDispatcher(
    instruction: NumericInstruction.I64ShrS,
    crossinline executor: Executor<NumericInstruction.I64ShrS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
