package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.shr
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64ShrSExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrS,
) {
    vstack.binaryOperation(Long::shr)
}
