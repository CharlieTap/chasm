package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import kotlin.math.absoluteValue

internal inline fun F32AbsExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Abs,
) {
    context.stack.unaryOperation(Float::absoluteValue)
}
