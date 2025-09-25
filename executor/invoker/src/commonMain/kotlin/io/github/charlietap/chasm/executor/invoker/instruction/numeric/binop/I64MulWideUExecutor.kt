package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64MulWideUExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericInstruction.I64MulWideU,
) {
    val b = vstack.popI64()
    val a = vstack.popI64()

    val aU = a.toULong()
    val bU = b.toULong()

    val mask32 = 0xFFFFFFFFUL

    val aLo = aU and mask32
    val aHi = aU shr 32
    val bLo = bU and mask32
    val bHi = bU shr 32

    val loLo = aLo * bLo
    val loHi = loLo shr 32

    val mid1 = aHi * bLo
    val mid2 = aLo * bHi

    val sum = loHi + (mid1 and mask32) + (mid2 and mask32)

    val low = ((loLo and mask32) or ((sum and mask32) shl 32)).toLong()
    val high = ((aHi * bHi) + (mid1 shr 32) + (mid2 shr 32) + (sum shr 32)).toLong()

    vstack.pushI64(low)
    vstack.pushI64(high)
}
