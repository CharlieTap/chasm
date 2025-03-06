package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.sqrt

internal inline fun F64SqrtExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Sqrt,
) {
    val stack = context.vstack

    val double = Double.fromBits(instruction.operand(stack))
    val result = sqrt(double).toRawBits()

    instruction.destination(result, stack)
}
