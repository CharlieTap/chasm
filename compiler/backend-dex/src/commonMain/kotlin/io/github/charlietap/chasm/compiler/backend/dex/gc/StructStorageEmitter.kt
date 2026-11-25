package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_INTS_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_REFERENCES_FIELD
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.Reg

internal class StructRegisters(
    val struct: Reg,
    val array: Reg,
    val index: Reg,
)

internal fun CodeEmitter.allocateStruct(
    storage: StructStorage,
    rtt: Reg,
    registers: StructRegisters,
) {

    val owner = ClassType(storage.className)

    newInstance(registers.struct, owner)
    invokeConstructor(DexConstructor(owner, RTT), registers.struct, rtt)

    if (storage.spilledInts > 0) {
        const(registers.index, storage.spilledInts)
        newArray(registers.array, registers.index, INTS)
        putField(registers.array, registers.struct, DexField(owner, SPILLED_INTS_FIELD, INTS))
    }
    if (storage.spilledReferences > 0) {
        const(registers.index, storage.spilledReferences)
        newArray(registers.array, registers.index, OBJECTS)
        putField(registers.array, registers.struct, DexField(owner, SPILLED_REFERENCES_FIELD, OBJECTS))
    }
}

internal fun CodeEmitter.putLane(
    value: Reg,
    owner: ClassType,
    lane: Lane,
    registers: StructRegisters,
) {
    when {
        lane.spilled && lane.kind == LaneKind.INT -> {
            getField(registers.array, registers.struct, DexField(owner, SPILLED_INTS_FIELD, INTS))
            const(registers.index, lane.index)
            arrayPut(ArrayKind.Int, value, registers.array, registers.index)
        }
        lane.spilled -> {
            getField(registers.array, registers.struct, DexField(owner, SPILLED_REFERENCES_FIELD, OBJECTS))
            const(registers.index, lane.index)
            arrayPut(ArrayKind.Object, value, registers.array, registers.index)
        }
        lane.kind == LaneKind.INT -> putField(value, registers.struct, DexField(owner, lane.fieldName(), DexType.Int))
        else -> putField(value, registers.struct, DexField(owner, lane.fieldName(), DexType.Object))
    }
}

internal fun CodeEmitter.maskPacked(
    type: FieldType,
    register: Reg,
) {
    when ((type.storageType as? StorageType.Packed)?.type) {
        PackedType.I8 -> binary(LiteralOp.AndInt, register, register, literal = PACKED_BYTE_MASK)
        PackedType.I16 -> convert(Conversion.IntToChar, register, register)
        null -> Unit
    }
}

private const val PACKED_BYTE_MASK = 0xFF

private val RTT = ClassType(RTT_TYPE)

private val INTS = DexType.Int.array()

private val OBJECTS = DexType.Object.array()
