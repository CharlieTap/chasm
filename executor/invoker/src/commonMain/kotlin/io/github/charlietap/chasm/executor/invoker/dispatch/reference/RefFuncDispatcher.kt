package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefFuncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefFuncDispatcher(
    instruction: ReferenceInstruction.RefFunc,
) = RefFuncDispatcher(
    instruction = instruction,
    executor = ::RefFuncExecutor,
)

internal inline fun RefFuncDispatcher(
    instruction: ReferenceInstruction.RefFunc,
    crossinline executor: Executor<ReferenceInstruction.RefFunc>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
