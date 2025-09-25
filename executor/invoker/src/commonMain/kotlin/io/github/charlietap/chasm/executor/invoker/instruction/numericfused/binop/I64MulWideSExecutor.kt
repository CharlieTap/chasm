package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64MulWideSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64MulWideS,
) {
    val a = instruction.left(vstack)
    val b = instruction.right(vstack)

    val aU = a.toULong()
    val bU = b.toULong()
    val (lowU, highUBase) = mulWideUnsigned(aU, bU)
    var highU = highUBase
    if (a < 0) highU -= bU
    if (b < 0) highU -= aU

    instruction.destinationLow(lowU.toLong(), vstack)
    instruction.destinationHigh(highU.toLong(), vstack)
}

private inline fun mulWideUnsigned(a: ULong, b: ULong): Pair<ULong, ULong> {
    val mask32 = 0xFFFFFFFFUL
    val aLo = a and mask32
    val aHi = a shr 32
    val bLo = b and mask32
    val bHi = b shr 32
    val loLo = aLo * bLo
    val loHi = loLo shr 32
    val mid1 = aHi * bLo
    val mid2 = aLo * bHi
    val sum = loHi + (mid1 and mask32) + (mid2 and mask32)
    val low = (loLo and mask32) or ((sum and mask32) shl 32)
    val high = (aHi * bHi) + (mid1 shr 32) + (mid2 shr 32) + (sum shr 32)
    return Pair(low, high)
}
