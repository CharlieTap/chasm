package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.absoluteValue

internal inline fun F32AbsExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Abs,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = float.absoluteValue.toRawBits().toLong()

    instruction.destination(result, stack)
}
