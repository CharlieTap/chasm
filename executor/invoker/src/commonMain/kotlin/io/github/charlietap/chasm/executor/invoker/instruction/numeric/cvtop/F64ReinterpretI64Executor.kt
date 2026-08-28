package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun F64ReinterpretI64Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64ReinterpretI64,
) {
    vstack.convertOperation(Double::fromBits)
}
