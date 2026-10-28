package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.type.PackedType

fun ArrayGetSignedIDispatcher(
    addressSlot: Int,
    field: Int,
    destinationSlot: Int,
    packedType: PackedType,
): DispatchableInstruction {
    val shift = packedType.signedShift()
    return DispatchableInstruction { vstack, context, nextIp ->
        val packed = context.heap.getArrayElementTrusted(vstack.getFrameSlot(addressSlot), field)
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

fun ArrayGetSignedSDispatcher(
    addressSlot: Int,
    fieldSlot: Int,
    destinationSlot: Int,
    packedType: PackedType,
): DispatchableInstruction {
    val shift = packedType.signedShift()
    return DispatchableInstruction { vstack, context, nextIp ->
        val address = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getArrayElementTrusted(address, vstack.getFrameSlot(fieldSlot).toInt())
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

fun ArrayGetUnsignedIDispatcher(
    addressSlot: Int,
    field: Int,
    destinationSlot: Int,
    packedType: PackedType,
): DispatchableInstruction {
    val mask = packedType.unsignedMask()
    return DispatchableInstruction { vstack, context, nextIp ->
        val packed = context.heap.getArrayElementTrusted(vstack.getFrameSlot(addressSlot), field)
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

fun ArrayGetUnsignedSDispatcher(
    addressSlot: Int,
    fieldSlot: Int,
    destinationSlot: Int,
    packedType: PackedType,
): DispatchableInstruction {
    val mask = packedType.unsignedMask()
    return DispatchableInstruction { vstack, context, nextIp ->
        val address = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getArrayElementTrusted(address, vstack.getFrameSlot(fieldSlot).toInt())
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

fun StructGetSignedSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    fieldIndex: Int,
    packedType: PackedType,
): DispatchableInstruction {
    val shift = packedType.signedShift()
    return DispatchableInstruction { vstack, context, nextIp ->
        val reference = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getStructFieldTrusted(reference, fieldIndex)
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

fun StructGetUnsignedSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    fieldIndex: Int,
    packedType: PackedType,
): DispatchableInstruction {
    val mask = packedType.unsignedMask()
    return DispatchableInstruction { vstack, context, nextIp ->
        val reference = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getStructFieldTrusted(reference, fieldIndex)
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

internal fun PackedType.signedShift(): Int = when (this) {
    PackedType.I8 -> Long.SIZE_BITS - Byte.SIZE_BITS
    PackedType.I16 -> Long.SIZE_BITS - Short.SIZE_BITS
}

internal fun PackedType.unsignedMask(): Long = when (this) {
    PackedType.I8 -> UByte.MAX_VALUE.toLong()
    PackedType.I16 -> UShort.MAX_VALUE.toLong()
}
