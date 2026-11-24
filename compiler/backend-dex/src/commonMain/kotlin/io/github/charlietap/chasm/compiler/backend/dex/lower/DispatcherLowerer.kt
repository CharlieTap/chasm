package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.takesReferenceArray
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal const val CHUNK_WINDOW_WORDS = 5

internal const val OBJECT_FRAME_TYPE = "[Ljava/lang/Object;"

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
    emit.const(Registers.I0, frameSlots)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I0, OBJECT_FRAME_TYPE)
    emit.moveObject(registers.objectFrame, Registers.SECOND_OBJECT)
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
    storeParameters()
    resetFrame(Registers.OBJECT)
    loadLongs(Registers.OBJECT, table)
    val loop = emit.freshLabel()
    val negative = emit.freshLabel()
    val missing = emit.freshLabel()
    val leave = emit.freshLabel()
    val calls = emit.labels(chunks.size)
    emit.const(Registers.NARROW, 0)
    emit.place(loop)
    emit.branchZero(Cond.Lt, Registers.NARROW, negative)
    emit.const(Registers.I1, table.size)
    emit.branchCompare(Cond.Ge, Registers.NARROW, Registers.I1, missing)
    emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.NARROW)
    emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
    emit.literal(LiteralOp.UshrInt, Registers.I1, Registers.I0, ENTRY_BITS)
    emit.const(Registers.I2, ENTRY_MASK)
    emit.binary(BinaryOp.AndInt, Registers.I3, Registers.I0, Registers.I2)
    emit.packedSwitch(Registers.I1, 0, chunks.indices.map { index -> calls[index] })
    emit.place(missing)
    trap(UNREACHABLE)
    val window = registers.window
    chunks.forEachIndexed { index, chunk ->
        emit.place(calls[index])
        emit.moveObject(window, registers.binding)
        emit.moveObject(window + 1, Registers.CONTEXT)
        emit.move(window + 2, Registers.DEPTH)
        emit.move(window + 3, registers.frameBase)
        emit.move(window + 4, Registers.I3)
        emit.moveObject(window + 5, registers.objectFrame)
        emit.invokeRange(chunk, window, CHUNK_WINDOW_WORDS + 1)
        emit.moveResult(Registers.NARROW)
        emit.gotoLabel(loop)
    }
    emit.place(negative)
    emit.const(Registers.I0, Chunk.RETURN)
    emit.branchCompare(Cond.Ne, Registers.NARROW, Registers.I0, leave)
    returnFromFrame()
    emit.place(leave)
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
        emit.invoke(ARRAYCOPY, Registers.W1, Registers.I0, Registers.OBJECT, Registers.I2, Registers.I3)
        if (!takesReferenceArray(facts.frame.type, module.referenceDescriptor)) return
        emit.moveObject(Registers.W1, registers.parameter(1))
        emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
        facts.frame.type.params.types.forEachIndexed { index, type ->
            if (type !is ValueType.Reference) return@forEachIndexed
            emit.const(Registers.I1, index)
            emit.arrayGet(ArrayKind.Object, Registers.THIRD_OBJECT, Registers.W1, Registers.I1)
            emit.arrayPut(ArrayKind.Object, Registers.THIRD_OBJECT, Registers.SECOND_OBJECT, Registers.I1)
        }
        return
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
            OBJECT_TYPE -> {
                emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
                emit.moveObject(Registers.THIRD_OBJECT, parameter)
                emit.const(Registers.I1, index)
                emit.arrayPut(ArrayKind.Object, Registers.THIRD_OBJECT, Registers.SECOND_OBJECT, Registers.I1)
                continue
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
    if (resultCount == 1 && resultDescriptor == OBJECT_TYPE) {
        emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
        emit.const(Registers.I1, 0)
        emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I1)
        closeDispatcherFrame()
        return emit.returnValue(Registers.OBJECT)
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
    val types = facts.frame.type.results.types
    if (types.any { type -> type is ValueType.Reference }) {
        emit.const(Registers.I0, resultCount)
        emit.invoke(RESULT_REFERENCES, Registers.CONTEXT, Registers.I0)
        emit.moveResultObject(Registers.THIRD_OBJECT)
        emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
        types.forEachIndexed { index, type ->
            if (type !is ValueType.Reference) return@forEachIndexed
            emit.const(Registers.I1, index)
            emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I1)
            emit.arrayPut(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I1)
        }
    }
    closeDispatcherFrame()
    emit.returnValue(Registers.W2)
}

private fun LowerContext.closeDispatcherFrame() {
    emit.moveWide(Registers.W3, registers.savedStack)
    emit.invoke(CLOSE_FRAME, Registers.CONTEXT, Registers.W3, Registers.W3 + 1)
}

internal const val ENTRY_BITS = 16

internal const val ENTRY_MASK = 0xFFFF
