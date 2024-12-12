package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefFuncExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction

fun RefFuncDispatcher(
    instruction: ReferenceInstruction.RefFunc,
) = RefFuncDispatcher(
    instruction = instruction,
    executor = ::RefFuncExecutor,
)

internal inline fun RefFuncDispatcher(
    instruction: ReferenceInstruction.RefFunc,
    crossinline executor: Executor<ReferenceInstruction.RefFunc>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
