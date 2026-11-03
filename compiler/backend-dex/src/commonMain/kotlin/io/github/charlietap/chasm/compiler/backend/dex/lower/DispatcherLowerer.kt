package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal const val CHUNK_WINDOW_WORDS = 5

internal fun LowerContext.lowerDispatcher(
    chunks: List<MethodRef>,
    table: LongArray,
    cost: Int,
) {
    emit.moveObject(Registers.CONTEXT, registers.contextParameter)
    emit.move(Registers.DEPTH, registers.depthParameter)
    emit.moveObject(registers.binding, registers.bindingParameter)
    chargeStackBudget(cost)
    emit.const(Registers.I0, frameSlots)
    emit.invoke(OPEN_FRAME, Registers.CONTEXT, Registers.I0)
    emit.moveResultWide(Registers.W0)
    emit.moveWide(registers.savedStack, Registers.W0)
    emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
    emit.move(registers.frameBase, Registers.I0)
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
    storeParameters()
    resetFrame(Registers.OBJECT)
    loadObject(Registers.OBJECT, Resource.Longs(table), "[J")
    emit.const(Registers.NARROW, 0)
    emit.place(LOOP)
    emit.branchZero(Cond.Lt, Registers.NARROW, NEGATIVE)
    emit.const(Registers.I0, 0)
    emit.const(Registers.I1, table.size - 1)
    emit.place(SEARCH)
    emit.branchCompare(Cond.Gt, Registers.I0, Registers.I1, MISSING)
    emit.binary(BinaryOp.AddInt, Registers.I2, Registers.I0, Registers.I1)
    emit.literal(LiteralOp.UshrInt, Registers.I2, Registers.I2, 1)
    emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    emit.const(Registers.I3, 32)
    emit.binary(BinaryOp.ShrLong, Registers.W1, Registers.W0, Registers.I3)
    emit.convert(Conversion.LongToInt, Registers.I3, Registers.W1)
    emit.branchCompare(Cond.Eq, Registers.I3, Registers.NARROW, FOUND)
    emit.branchCompare(Cond.Lt, Registers.I3, Registers.NARROW, HIGHER)
    emit.literal(LiteralOp.AddInt, Registers.I1, Registers.I2, -1)
    emit.gotoLabel(SEARCH)
    emit.place(HIGHER)
    emit.literal(LiteralOp.AddInt, Registers.I0, Registers.I2, 1)
    emit.gotoLabel(SEARCH)
    emit.place(FOUND)
    emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
    emit.literal(LiteralOp.UshrInt, Registers.I1, Registers.I0, ENTRY_BITS)
    emit.const(Registers.I2, ENTRY_MASK)
    emit.binary(BinaryOp.AndInt, Registers.I3, Registers.I0, Registers.I2)
    emit.packedSwitch(Registers.I1, 0, chunks.indices.map { index -> "call$index" })
    emit.place(MISSING)
    emit.invoke(UNREACHABLE)
    returnUnreachable()
    val window = registers.window
    chunks.forEachIndexed { index, chunk ->
        emit.place("call$index")
        emit.moveObject(window, registers.binding)
        emit.moveObject(window + 1, Registers.CONTEXT)
        emit.move(window + 2, Registers.DEPTH)
        emit.move(window + 3, registers.frameBase)
        emit.move(window + 4, Registers.I3)
        emit.invokeRange(chunk, window, CHUNK_WINDOW_WORDS)
        emit.moveResult(Registers.NARROW)
        emit.gotoLabel(LOOP)
    }
    emit.place(NEGATIVE)
    emit.const(Registers.I0, Chunk.RETURN)
    emit.branchCompare(Cond.Ne, Registers.NARROW, Registers.I0, LEAVE)
    returnFromFrame()
    emit.place(LEAVE)
    closeDispatcherFrame()
    returnAnything()
}

private fun LowerContext.storeParameters() {
    if (parameterCount == 0) return
    if (argumentArray) {
        emit.moveObject(Registers.W1, registers.parameter(0))
        emit.const(Registers.I0, 0)
        frameIndex(Registers.I2, 0)
        emit.const(Registers.I3, parameterCount)
        return emit.invoke(ARRAYCOPY, Registers.W1, Registers.I0, Registers.OBJECT, Registers.I2, Registers.I3)
    }
    for (index in 0 until parameterCount) {
        val parameter = registers.parameter(parameterOffsets[index])
        when (parameterDescriptors[index]) {
            "I" -> {
                emit.move(Registers.I1, parameter)
                emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
            }
            "F" -> {
                emit.move(Registers.I1, parameter)
                emit.invoke(FLOAT_TO_RAW_INT_BITS, Registers.I1)
                emit.moveResult(Registers.I1)
                emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
            }
            "D" -> {
                emit.moveWide(Registers.W0, parameter)
                emit.invoke(DOUBLE_TO_RAW_LONG_BITS, Registers.W0, Registers.W0 + 1)
                emit.moveResultWide(Registers.W0)
            }
            else -> emit.moveWide(Registers.W0, parameter)
        }
        frameIndex(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
}

private fun LowerContext.returnFromFrame() {
    if (resultCount == 0) {
        closeDispatcherFrame()
        return emit.returnVoid()
    }
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
    frameIndex(Registers.I2, 0)
    if (resultCount == 1) {
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        closeDispatcherFrame()
        return returnEncoded(Registers.W0)
    }
    emit.const(Registers.I3, resultCount)
    emit.newArray(Registers.W2, Registers.I3, "[J")
    emit.const(Registers.I0, 0)
    emit.invoke(ARRAYCOPY, Registers.OBJECT, Registers.I2, Registers.W2, Registers.I0, Registers.I3)
    closeDispatcherFrame()
    emit.returnValue(Registers.W2)
}

private fun LowerContext.closeDispatcherFrame() {
    emit.moveWide(Registers.W3, registers.savedStack)
    emit.invoke(CLOSE_FRAME, Registers.CONTEXT, Registers.W3, Registers.W3 + 1)
}

internal const val ENTRY_BITS = 16

internal const val ENTRY_MASK = 0xFFFF

private const val LOOP = "loop"

private const val SEARCH = "search"

private const val HIGHER = "higher"

private const val FOUND = "found"

private const val MISSING = "missing"

private const val NEGATIVE = "negative"

private const val LEAVE = "leave"
