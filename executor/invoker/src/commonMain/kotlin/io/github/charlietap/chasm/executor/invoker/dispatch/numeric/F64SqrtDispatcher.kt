package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64SqrtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64SqrtDispatcher(
    instruction: NumericInstruction.F64Sqrt,
) = F64SqrtDispatcher(
    instruction = instruction,
    executor = ::F64SqrtExecutor,
)

internal inline fun F64SqrtDispatcher(
    instruction: NumericInstruction.F64Sqrt,
    crossinline executor: Executor<NumericInstruction.F64Sqrt>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
