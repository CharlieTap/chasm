package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64Sub128Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: NumericInstruction.I64Sub128,
) {
    val bHi = vstack.popI64()
    val bLo = vstack.popI64()
    val aHi = vstack.popI64()
    val aLo = vstack.popI64()

    val aLoU = aLo.toULong()
    val bLoU = bLo.toULong()
    val borrow = if (aLoU < bLoU) 1UL else 0UL
    val diffLo = aLoU - bLoU

    val aHiU = aHi.toULong()
    val bHiU = bHi.toULong()
    val diffHi = aHiU - bHiU - borrow

    vstack.pushI64(diffLo.toLong())
    vstack.pushI64(diffHi.toLong())
}
