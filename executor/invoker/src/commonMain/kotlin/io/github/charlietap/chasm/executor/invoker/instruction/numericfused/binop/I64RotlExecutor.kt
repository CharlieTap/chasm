package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.rotateLeft
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64RotlExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Rotl,
) {
    val left = instruction.left(vstack)
    val right = instruction.right(vstack)
    val result = left.rotateLeft(right)

    instruction.destination(result, vstack)
}
