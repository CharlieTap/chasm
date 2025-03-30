package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32AndExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32AndDispatcher(
    instruction: FusedNumericInstruction.I32And,
) = I32AndDispatcher(
    instruction = instruction,
    executor = ::I32AndExecutor,
)

internal inline fun I32AndDispatcher(
    instruction: FusedNumericInstruction.I32And,
    crossinline executor: Executor<FusedNumericInstruction.I32And>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
