package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32GtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32GtDispatcher(
    instruction: FusedNumericInstruction.F32Gt,
) = F32GtDispatcher(
    instruction = instruction,
    executor = ::F32GtExecutor,
)

internal inline fun F32GtDispatcher(
    instruction: FusedNumericInstruction.F32Gt,
    crossinline executor: Executor<FusedNumericInstruction.F32Gt>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
