package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32SqrtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32SqrtDispatcher(
    instruction: FusedNumericInstruction.F32Sqrt,
) = F32SqrtDispatcher(
    instruction = instruction,
    executor = ::F32SqrtExecutor,
)

internal inline fun F32SqrtDispatcher(
    instruction: FusedNumericInstruction.F32Sqrt,
    crossinline executor: Executor<FusedNumericInstruction.F32Sqrt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
