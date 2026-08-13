package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.field
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.type.PackedType

internal fun PackedArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedI): DispatchableInstruction? {
    val shift = instruction.packedType?.signedShift() ?: return null
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val field = instruction.field
    return DispatchableInstruction { vstack, _, store, _, nextIp ->
        val packed = store.array(vstack.getFrameSlot(addressSlot).toArrayAddress()).field(field)
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

internal fun PackedArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedS): DispatchableInstruction? {
    val shift = instruction.packedType?.signedShift() ?: return null
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldSlot = instruction.fieldSlot
    return DispatchableInstruction { vstack, _, store, _, nextIp ->
        val address = vstack.getFrameSlot(addressSlot).toArrayAddress()
        val packed = store.array(address).field(vstack.getFrameSlot(fieldSlot).toInt())
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

internal fun PackedArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedI): DispatchableInstruction? {
    val mask = instruction.packedType?.unsignedMask() ?: return null
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val field = instruction.field
    return DispatchableInstruction { vstack, _, store, _, nextIp ->
        val packed = store.array(vstack.getFrameSlot(addressSlot).toArrayAddress()).field(field)
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

internal fun PackedArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedS): DispatchableInstruction? {
    val mask = instruction.packedType?.unsignedMask() ?: return null
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldSlot = instruction.fieldSlot
    return DispatchableInstruction { vstack, _, store, _, nextIp ->
        val address = vstack.getFrameSlot(addressSlot).toArrayAddress()
        val packed = store.array(address).field(vstack.getFrameSlot(fieldSlot).toInt())
        vstack.setFrameSlot(destinationSlot, packed and mask)
        nextIp
    }
}

internal fun PackedStructGetSignedDispatcher(instruction: AggregateSuperInstruction.StructGetSignedS): DispatchableInstruction? {
    val shift = instruction.packedType?.signedShift() ?: return null
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldIndex = instruction.fieldIndex
    return DispatchableInstruction { vstack, _, store, _, nextIp ->
        val packed = store.struct(vstack.getFrameSlot(addressSlot).toStructAddress()).field(fieldIndex)
        vstack.setFrameSlot(destinationSlot, (packed shl shift) shr shift)
        nextIp
    }
}

internal fun PackedStructGetUnsignedDispatcher(instruction: AggregateSuperInstruction.StructGetUnsignedS): DispatchableInstruction? {
    val mask = instruction.packedType?.unsignedMask() ?: return null
    val addressSlot = instruction.addressSlot
    val destinationSlot = instruction.destinationSlot
    val fieldIndex = instruction.fieldIndex
    return DispatchableInstruction { vstack, _, store, _, nextIp ->
        val packed = store.struct(vstack.getFrameSlot(addressSlot).toStructAddress()).field(fieldIndex)
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
