package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32SqrtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32SqrtDispatcher(
    instruction: NumericInstruction.F32Sqrt,
) = F32SqrtDispatcher(
    instruction = instruction,
    executor = ::F32SqrtExecutor,
)

internal inline fun F32SqrtDispatcher(
    instruction: NumericInstruction.F32Sqrt,
    crossinline executor: Executor<NumericInstruction.F32Sqrt>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
