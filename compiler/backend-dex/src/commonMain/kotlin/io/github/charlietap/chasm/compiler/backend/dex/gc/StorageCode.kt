package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_INTS_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_REFERENCES_FIELD
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.compiler.backend.dex.lower.LowerContext
import io.github.charlietap.chasm.compiler.backend.dex.lower.Registers
import io.github.charlietap.chasm.compiler.backend.dex.lower.loadObject
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal fun LowerContext.allocateStruct(
    storage: StructStorage,
    type: Int,
) {

    loadObject(Registers.SECOND_OBJECT, Resource.Rtt(type), RTT_TYPE)
    emit.newInstance(Registers.OBJECT, storage.className)
    emit.invokeConstructor(storage.className, RTT_TYPE, Registers.OBJECT, Registers.SECOND_OBJECT)

    if (storage.spilledInts > 0) {
        emit.const(Registers.I1, storage.spilledInts)
        emit.newArray(Registers.SECOND_OBJECT, Registers.I1, SPILLED_INTS_TYPE)
        emit.putField(Registers.SECOND_OBJECT, Registers.OBJECT, storage.className, SPILLED_INTS_FIELD, SPILLED_INTS_TYPE)
    }
    if (storage.spilledReferences > 0) {
        emit.const(Registers.I1, storage.spilledReferences)
        emit.newArray(Registers.SECOND_OBJECT, Registers.I1, SPILLED_REFERENCES_TYPE)
        emit.putField(Registers.SECOND_OBJECT, Registers.OBJECT, storage.className, SPILLED_REFERENCES_FIELD, SPILLED_REFERENCES_TYPE)
    }
}

internal fun LowerContext.getLane(
    destination: Int,
    receiver: Int,
    storage: StructStorage,
    lane: Lane,
) {

    val owner = module.storagePlans.laneOwner(storage, lane)

    when {
        lane.spilled && lane.kind == LaneKind.INT -> {
            emit.getField(Registers.THIRD_OBJECT, receiver, owner, SPILLED_INTS_FIELD, SPILLED_INTS_TYPE)
            emit.const(Registers.NARROW, lane.index)
            emit.arrayGet(ArrayKind.Int, destination, Registers.THIRD_OBJECT, Registers.NARROW)
        }
        lane.spilled -> {
            emit.getField(Registers.THIRD_OBJECT, receiver, owner, SPILLED_REFERENCES_FIELD, SPILLED_REFERENCES_TYPE)
            emit.const(Registers.NARROW, lane.index)
            emit.arrayGet(ArrayKind.Object, destination, Registers.THIRD_OBJECT, Registers.NARROW)
        }
        lane.kind == LaneKind.INT -> emit.getField(destination, receiver, owner, lane.fieldName(), "I")
        else -> emit.getField(destination, receiver, owner, lane.fieldName(), OBJECT_TYPE)
    }
}

internal fun LowerContext.putLane(
    value: Int,
    receiver: Int,
    storage: StructStorage,
    lane: Lane,
) {

    val owner = module.storagePlans.laneOwner(storage, lane)

    when {
        lane.spilled && lane.kind == LaneKind.INT -> {
            emit.getField(Registers.THIRD_OBJECT, receiver, owner, SPILLED_INTS_FIELD, SPILLED_INTS_TYPE)
            emit.const(Registers.NARROW, lane.index)
            emit.arrayPut(ArrayKind.Int, value, Registers.THIRD_OBJECT, Registers.NARROW)
        }
        lane.spilled -> {
            emit.getField(Registers.THIRD_OBJECT, receiver, owner, SPILLED_REFERENCES_FIELD, SPILLED_REFERENCES_TYPE)
            emit.const(Registers.NARROW, lane.index)
            emit.arrayPut(ArrayKind.Object, value, Registers.THIRD_OBJECT, Registers.NARROW)
        }
        lane.kind == LaneKind.INT -> emit.putField(value, receiver, owner, lane.fieldName(), "I")
        else -> emit.putField(value, receiver, owner, lane.fieldName(), OBJECT_TYPE)
    }
}

internal fun LowerContext.combineLanes(
    pair: Int,
    low: Int,
    high: Int,
) {
    emit.convert(Conversion.IntToLong, pair, low)
    emit.constWide(Registers.W1, LOW_HALF)
    emit.binary(BinaryOp.AndLong, pair, pair, Registers.W1)
    emit.convert(Conversion.IntToLong, Registers.W1, high)
    emit.const(Registers.NARROW, HALF_BITS)
    emit.binary(BinaryOp.ShlLong, Registers.W1, Registers.W1, Registers.NARROW)
    emit.binary(BinaryOp.OrLong, pair, pair, Registers.W1)
}

internal fun LowerContext.extendPacked(
    type: FieldType,
    register: Int,
    signed: Boolean?,
) {
    when {
        signed != true -> Unit
        packedType(type) == PackedType.I8 -> emit.convert(Conversion.IntToByte, register, register)
        packedType(type) == PackedType.I16 -> emit.convert(Conversion.IntToShort, register, register)
    }
}

internal fun LowerContext.maskPacked(
    type: FieldType,
    register: Int,
) {
    when (packedType(type)) {
        PackedType.I8 -> emit.literal(LiteralOp.AndInt, register, register, BYTE_MASK)
        PackedType.I16 -> emit.convert(Conversion.IntToChar, register, register)
        null -> Unit
    }
}

private fun packedType(type: FieldType): PackedType? {
    return (type.storageType as? StorageType.Packed)?.type
}

internal const val SPILLED_INTS_TYPE = "[I"

internal const val SPILLED_REFERENCES_TYPE = "[Ljava/lang/Object;"

internal const val HALF_BITS = 32

private const val LOW_HALF = 0xFFFFFFFFL

private const val BYTE_MASK = 0xFF
