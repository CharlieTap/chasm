package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction

internal inline fun RefFuncExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefFunc,
) {
    context.vstack.push(instruction.reference)
}
