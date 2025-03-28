package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64SubDispatcher(
    instruction: FusedNumericInstruction.F64Sub,
) = F64SubDispatcher(
    instruction = instruction,
    executor = ::F64SubExecutor,
)

internal inline fun F64SubDispatcher(
    instruction: FusedNumericInstruction.F64Sub,
    crossinline executor: Executor<FusedNumericInstruction.F64Sub>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
