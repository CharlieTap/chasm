package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.executor.invoker.ext.nearest
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32NearestExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Nearest,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = float.nearest().toRawBits().toLong()

    instruction.destination(result, stack)
}
