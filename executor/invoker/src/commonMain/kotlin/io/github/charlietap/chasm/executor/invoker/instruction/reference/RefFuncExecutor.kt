package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun RefFuncExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefFunc,
) {
    vstack.push(instruction.reference)
}
