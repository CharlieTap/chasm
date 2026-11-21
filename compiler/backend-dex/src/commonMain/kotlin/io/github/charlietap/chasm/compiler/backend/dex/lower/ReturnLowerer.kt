package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.Label
import io.github.charlietap.dexter.LiteralOp

internal fun LowerContext.ret(
    results: List<Operand> = List(resultCount) { Operand.Slot(it) },
) {
    if (chunk != null) return chunkReturn(results)
    when (resultCount) {
        0 -> emit.returnVoid()
        1 -> when (resultDescriptor) {
            "I" -> {
                loadInt(Registers.I0, Registers.W0, results[0])
                emit.returnValue(Registers.I0)
            }
            "F" -> {
                loadFloat(Registers.I0, Registers.W0, results[0])
                emit.returnValue(Registers.I0)
            }
            "D" -> {
                loadDouble(Registers.W0, results[0])
                emit.returnValue(Registers.W0)
            }
            OBJECT_TYPE -> {
                loadReference(Registers.OBJECT, results[0])
                emit.returnValue(Registers.OBJECT)
            }
            else -> {
                loadWide(Registers.W0, results[0])
                emit.returnValue(Registers.W0)
            }
        }
        else -> {
            val types = facts.type.results.types
            val referenceResults = types.any { type -> type is ValueType.Reference }
            emit.const(Registers.I0, resultCount)
            emit.newArray(Registers.OBJECT, Registers.I0, "[J")
            for (index in 0 until resultCount) {
                if (referenceResults && types[index] is ValueType.Reference) continue
                loadWide(Registers.W0, results[index])
                emit.const(Registers.I2, index)
                emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
            }
            if (referenceResults) {
                emit.const(Registers.I0, resultCount)
                emit.invoke(RESULT_REFERENCES, Registers.CONTEXT, Registers.I0)
                emit.moveResultObject(Registers.SECOND_OBJECT)
                types.forEachIndexed { index, type ->
                    if (type !is ValueType.Reference) return@forEachIndexed
                    loadReference(Registers.THIRD_OBJECT, results[index])
                    emit.const(Registers.I1, index)
                    emit.arrayPut(ArrayKind.Object, Registers.THIRD_OBJECT, Registers.SECOND_OBJECT, Registers.I1)
                }
            }
            emit.returnValue(Registers.OBJECT)
        }
    }
}

internal fun LowerContext.returnEncoded(
    pair: Int,
) {
    when (resultDescriptor) {
        "I" -> {
            emit.convert(Conversion.LongToInt, Registers.I0, pair)
            emit.returnValue(Registers.I0)
        }
        "F" -> {
            emit.convert(Conversion.LongToInt, Registers.I0, pair)
            emit.invoke(INT_BITS_TO_FLOAT, Registers.I0)
            emit.moveResult(Registers.I0)
            emit.returnValue(Registers.I0)
        }
        "D" -> {
            emit.invoke(LONG_BITS_TO_DOUBLE, pair, pair + 1)
            emit.moveResultWide(pair)
            emit.returnValue(pair)
        }
        else -> emit.returnValue(pair)
    }
}

internal fun LowerContext.returnAnything() {
    if (chunk != null) return returnCode(Chunk.LEAVE_WITHOUT_RESULT)
    when (resultDescriptor) {
        "V" -> emit.returnVoid()
        "J", "D" -> {
            emit.constWide(Registers.W0, 0L)
            emit.returnValue(Registers.W0)
        }
        OBJECT_TYPE -> {
            emit.const(Registers.OBJECT, 0)
            emit.returnValue(Registers.OBJECT)
        }
        else -> {
            emit.const(Registers.I0, 0)
            emit.returnValue(Registers.I0)
        }
    }
}

internal fun LowerContext.returnUnreachable() = returnAnything()

internal fun LowerContext.trap(method: MethodRef) {
    emit.invoke(method)
    returnUnreachable()
}

internal fun LowerContext.trapLabel(method: MethodRef): Label {
    if (module.options.stacktraces) {
        return outOfLineTrap(method)
    }
    return sharedTrapLabels.getOrPut(method) { outOfLineTrap(method) }
}

private fun LowerContext.outOfLineTrap(method: MethodRef): Label {

    val label = emit.freshLabel()
    val origin = position

    outOfLine += {
        emit.place(label)
        if (module.options.stacktraces) {
            emit.line(line(origin))
        }
        trap(method)
    }

    return label
}

internal fun LowerContext.returnCode(
    code: Int,
) {
    emit.const(Registers.I0, code)
    emit.returnValue(Registers.I0)
}

private fun LowerContext.chunkReturn(
    results: List<Operand>,
) {
    val types = facts.type.results.types
    if (types.any { type -> type is ValueType.Reference }) {
        if (types.any { type -> type !is ValueType.Reference }) {
            emit.invoke(FRAME, Registers.CONTEXT)
            emit.moveResultObject(Registers.OBJECT)
        }
        results.forEachIndexed { index, result ->
            if (types[index] is ValueType.Reference) return@forEachIndexed
            loadWide(Registers.W0, result)
            frameIndex(Registers.I2, index)
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        }
        results.forEachIndexed { index, result ->
            if (types[index] !is ValueType.Reference) return@forEachIndexed
            loadReference(Registers.THIRD_OBJECT, result)
            spillReference(index, Registers.THIRD_OBJECT)
        }
        return returnCode(Chunk.RETURN)
    }
    if (results.isNotEmpty()) {
        emit.invoke(FRAME, Registers.CONTEXT)
        emit.moveResultObject(Registers.OBJECT)
    }
    results.forEachIndexed { index, result ->
        loadWide(Registers.W0, result)
        frameIndex(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    returnCode(Chunk.RETURN)
}

internal fun LowerContext.frameIndex(
    register: Int,
    slot: Int,
) {
    emit.move(register, registers.frameBase)
    if (slot == 0) return
    if (slot <= Short.MAX_VALUE) {
        emit.literal(LiteralOp.AddInt, register, register, slot)
    } else {
        emit.const(Registers.I3, slot)
        emit.binary(BinaryOp.AddInt, register, register, Registers.I3)
    }
}
