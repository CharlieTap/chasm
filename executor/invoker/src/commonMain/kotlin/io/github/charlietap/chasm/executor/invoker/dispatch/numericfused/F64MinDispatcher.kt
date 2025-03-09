package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F64MinExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64MinDispatcher(
    instruction: FusedNumericInstruction.F64Min,
) = F64MinDispatcher(
    instruction = instruction,
    executor = ::F64MinExecutor,
)

internal inline fun F64MinDispatcher(
    instruction: FusedNumericInstruction.F64Min,
    crossinline executor: Executor<FusedNumericInstruction.F64Min>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
