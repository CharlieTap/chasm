package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64MaxExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64MaxDispatcher(
    instruction: FusedNumericInstruction.F64Max,
) = F64MaxDispatcher(
    instruction = instruction,
    executor = ::F64MaxExecutor,
)

internal inline fun F64MaxDispatcher(
    instruction: FusedNumericInstruction.F64Max,
    crossinline executor: Executor<FusedNumericInstruction.F64Max>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
