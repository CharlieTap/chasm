package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.LiteralOp

internal fun LowerContext.publish(
    slot: Int,
    pair: Int,
) {
    val root = if (chunk != null) slot else references.rootIndex[slot]
    if (root <= Byte.MAX_VALUE) {
        emit.literal(LiteralOp.AddInt, registers.activationIndex, registers.activationFp, root)
    } else {
        emit.const(registers.activationIndex, root)
        emit.binary(BinaryOp.AddInt, registers.activationIndex, registers.activationIndex, registers.activationFp)
    }
    emit.arrayPut(ArrayKind.Wide, pair, registers.activationArray, registers.activationIndex)
}

internal fun LowerContext.refreshActivation() {
    if (!references.writesThrough) return
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(registers.activationArray)
}

internal fun LowerContext.closeActivation() {
    if (!references.writesThrough || chunk != null) return
    emit.moveWide(Registers.W3, registers.activationSaved)
    emit.invoke(CLOSE_FRAME, Registers.CONTEXT, Registers.W3, Registers.W3 + 1)
}
