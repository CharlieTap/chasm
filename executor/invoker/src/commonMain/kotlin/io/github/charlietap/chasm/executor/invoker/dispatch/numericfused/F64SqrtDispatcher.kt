package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64SqrtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64SqrtDispatcher(
    instruction: FusedNumericInstruction.F64Sqrt,
) = F64SqrtDispatcher(
    instruction = instruction,
    executor = ::F64SqrtExecutor,
)

internal inline fun F64SqrtDispatcher(
    instruction: FusedNumericInstruction.F64Sqrt,
    crossinline executor: Executor<FusedNumericInstruction.F64Sqrt>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
