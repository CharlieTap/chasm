package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64MulWideSExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericInstruction.I64MulWideS,
) {
    val b = vstack.popI64()
    val a = vstack.popI64()

    val aU = a.toULong()
    val bU = b.toULong()

    val (lowU, highUBase) = mulWideUnsigned(aU, bU)

    var highU = highUBase
    if (a < 0) highU = highU - bU
    if (b < 0) highU = highU - aU

    vstack.pushI64(lowU.toLong())
    vstack.pushI64(highU.toLong())
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
