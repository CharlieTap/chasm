package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64AndExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64AndDispatcher(
    instruction: FusedNumericInstruction.I64And,
) = I64AndDispatcher(
    instruction = instruction,
    executor = ::I64AndExecutor,
)

internal inline fun I64AndDispatcher(
    instruction: FusedNumericInstruction.I64And,
    crossinline executor: Executor<FusedNumericInstruction.I64And>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
