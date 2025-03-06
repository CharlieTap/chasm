package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.absoluteValue

internal inline fun F64AbsExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Abs,
) {
    val stack = context.vstack

    val double = Double.fromBits(instruction.operand(stack))
    val result = double.absoluteValue.toRawBits()

    instruction.destination(result, stack)
}
