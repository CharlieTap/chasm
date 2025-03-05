package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import kotlin.math.absoluteValue

internal inline fun F32AbsExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Abs,
) {
    context.vstack.unaryOperation(Float::absoluteValue)
}
