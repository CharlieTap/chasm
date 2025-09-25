package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Sub128,
) {
    val aLo = instruction.leftLow(vstack)
    val aHi = instruction.leftHigh(vstack)
    val bLo = instruction.rightLow(vstack)
    val bHi = instruction.rightHigh(vstack)

    val aLoU = aLo.toULong()
    val bLoU = bLo.toULong()
    val borrow = if (aLoU < bLoU) 1UL else 0UL
    val diffLo = aLoU - bLoU
    val diffHi = aHi.toULong() - bHi.toULong() - borrow

    instruction.destinationLow(diffLo.toLong(), vstack)
    instruction.destinationHigh(diffHi.toLong(), vstack)
}
