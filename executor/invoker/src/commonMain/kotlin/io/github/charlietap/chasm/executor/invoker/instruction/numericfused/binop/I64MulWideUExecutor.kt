package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64MulWideUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64MulWideU,
) {
    val a = instruction.left(vstack)
    val b = instruction.right(vstack)

    val mask32 = 0xFFFFFFFFUL
    val aU = a.toULong()
    val bU = b.toULong()
    val aLo = aU and mask32
    val aHi = aU shr 32
    val bLo = bU and mask32
    val bHi = bU shr 32
    val loLo = aLo * bLo
    val loHi = loLo shr 32
    val mid1 = aHi * bLo
    val mid2 = aLo * bHi
    val sum = loHi + (mid1 and mask32) + (mid2 and mask32)
    val low = (loLo and mask32) or ((sum and mask32) shl 32)
    val high = (aHi * bHi) + (mid1 shr 32) + (mid2 shr 32) + (sum shr 32)

    instruction.destinationLow(low.toLong(), vstack)
    instruction.destinationHigh(high.toLong(), vstack)
}
