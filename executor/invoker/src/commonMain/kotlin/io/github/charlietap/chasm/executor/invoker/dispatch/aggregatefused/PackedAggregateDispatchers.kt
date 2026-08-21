package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.type.PackedType

internal fun PackedArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedI): DispatchableInstruction {
    val shift = instruction.packedType.signedShift()
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val field = instruction.field
    return DispatchableInstruction { vstack, _, _, context, nextIp ->
        val packed = context.heap.getArrayElementTrusted(vstack.getFrameSlot(addressSlot), field)
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

internal fun PackedArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedS): DispatchableInstruction {
    val shift = instruction.packedType.signedShift()
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldSlot = instruction.fieldSlot
    return DispatchableInstruction { vstack, _, _, context, nextIp ->
        val address = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getArrayElementTrusted(address, vstack.getFrameSlot(fieldSlot).toInt())
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

internal fun PackedArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedI): DispatchableInstruction {
    val mask = instruction.packedType.unsignedMask()
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val field = instruction.field
    return DispatchableInstruction { vstack, _, _, context, nextIp ->
        val packed = context.heap.getArrayElementTrusted(vstack.getFrameSlot(addressSlot), field)
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

internal fun PackedArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedS): DispatchableInstruction {
    val mask = instruction.packedType.unsignedMask()
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldSlot = instruction.fieldSlot
    return DispatchableInstruction { vstack, _, _, context, nextIp ->
        val address = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getArrayElementTrusted(address, vstack.getFrameSlot(fieldSlot).toInt())
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

internal fun PackedStructGetSignedDispatcher(instruction: AggregateSuperInstruction.StructGetSignedS): DispatchableInstruction {
    val shift = instruction.packedType.signedShift()
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldIndex = instruction.fieldIndex
    return DispatchableInstruction { vstack, _, _, context, nextIp ->
        val reference = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getStructFieldTrusted(reference, fieldIndex)
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

internal fun PackedStructGetUnsignedDispatcher(instruction: AggregateSuperInstruction.StructGetUnsignedS): DispatchableInstruction {
    val mask = instruction.packedType.unsignedMask()
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldIndex = instruction.fieldIndex
    return DispatchableInstruction { vstack, _, _, context, nextIp ->
        val reference = vstack.getFrameSlot(addressSlot)
        val packed = context.heap.getStructFieldTrusted(reference, fieldIndex)
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

private fun PackedType.signedShift(): Int = when (this) {
    PackedType.I8 -> Long.SIZE_BITS - Byte.SIZE_BITS
    PackedType.I16 -> Long.SIZE_BITS - Short.SIZE_BITS
}

private fun PackedType.unsignedMask(): Long = when (this) {
    PackedType.I8 -> UByte.MAX_VALUE.toLong()
    PackedType.I16 -> UShort.MAX_VALUE.toLong()
}
