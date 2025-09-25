package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64Add128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Add128,
) {
    val aLo = instruction.leftLow(vstack)
    val aHi = instruction.leftHigh(vstack)
    val bLo = instruction.rightLow(vstack)
    val bHi = instruction.rightHigh(vstack)

    val sumLo = aLo.toULong() + bLo.toULong()
    val carry = if (sumLo < aLo.toULong()) 1UL else 0UL
    val sumHi = aHi.toULong() + bHi.toULong() + carry
    instruction.destinationLow(sumLo.toLong(), vstack)
    instruction.destinationHigh(sumHi.toLong(), vstack)
}
