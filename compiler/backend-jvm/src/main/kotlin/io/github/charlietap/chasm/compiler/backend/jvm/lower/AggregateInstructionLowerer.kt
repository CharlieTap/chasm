package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.PackedType
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_COPY
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_GET
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_LEN
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW_DEFAULT
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW_FIXED
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_SET
import io.github.charlietap.chasm.compiler.backend.abi.I31_GET_SIGNED
import io.github.charlietap.chasm.compiler.backend.abi.I31_GET_UNSIGNED
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.REF_CAST_STRUCT_GET
import io.github.charlietap.chasm.compiler.backend.abi.REF_I31
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_GET
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_GET_STRUCT_GET
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_NEW
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_NEW_DEFAULT
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_SET
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import org.objectweb.asm.Opcodes

internal fun lowerAggregateInstruction(
    instruction: AggregateInstruction,
    context: LowerContext,
) {
    when (val i = instruction) {
        is AggregateInstruction.StructGetS -> context.structGet(i.addressSlot, i.fieldIndex, i.destinationSlot)
        is AggregateInstruction.StructGetSignedS -> context.structGet(i.addressSlot, i.fieldIndex, i.destinationSlot, i.packedType, signed = true)
        is AggregateInstruction.StructGetUnsignedS -> context.structGet(i.addressSlot, i.fieldIndex, i.destinationSlot, i.packedType, signed = false)
        is AggregateInstruction.StructSetI -> context.heapStore(STRUCT_SET, i.addressSlot, immediate(i.fieldIndex), immediate(i.value))
        is AggregateInstruction.StructSetS -> context.heapStore(STRUCT_SET, i.addressSlot, immediate(i.fieldIndex), slot(i.valueSlot))
        is AggregateInstruction.StructGetStructGetS -> {
            context.pushContext()
            context.pushLong(slot(i.addressSlot))
            context.emit.int(i.firstFieldIndex)
            context.emit.int(i.secondFieldIndex)
            context.emit.invokeStatic(STRUCT_GET_STRUCT_GET)
            context.storeLong(i.destinationSlot)
        }
        is AggregateInstruction.RefCastStructGetS -> {
            context.pushContext()
            context.pushLong(slot(i.referenceSlot))
            context.pushResource(Resource.TypeTest(i.typeTest))
            context.emit.int(i.fieldIndex)
            context.emit.invokeStatic(REF_CAST_STRUCT_GET)
            context.storeLong(i.destinationSlot)
        }
        is AggregateInstruction.LocalSetStructGetS -> {
            context.copyToSlot(i.localSlot, slot(i.sourceSlot))
            context.structGet(i.sourceSlot, i.fieldIndex, i.destinationSlot)
        }
        is AggregateInstruction.StructNewS -> context.structNew(i.type, i.firstFieldSlot, i.destinationSlot)
        is AggregateInstruction.StructNewDefaultS -> {
            context.pushContext()
            context.pushResource(Resource.RuntimeType(i.type.index))
            context.pushResource(context.declarations.structDefaults(i.type))
            context.emit.invokeStatic(STRUCT_NEW_DEFAULT)
            context.storeLong(i.destinationSlot)
        }
        is AggregateInstruction.ArrayGetI -> context.arrayGet(i.addressSlot, immediate(i.field), i.destinationSlot)
        is AggregateInstruction.ArrayGetS -> context.arrayGet(i.addressSlot, slot(i.fieldSlot), i.destinationSlot)
        is AggregateInstruction.ArrayGetSignedI -> context.arrayGet(i.addressSlot, immediate(i.field), i.destinationSlot, i.packedType, signed = true)
        is AggregateInstruction.ArrayGetSignedS -> context.arrayGet(i.addressSlot, slot(i.fieldSlot), i.destinationSlot, i.packedType, signed = true)
        is AggregateInstruction.ArrayGetUnsignedI -> context.arrayGet(i.addressSlot, immediate(i.field), i.destinationSlot, i.packedType, signed = false)
        is AggregateInstruction.ArrayGetUnsignedS -> context.arrayGet(i.addressSlot, slot(i.fieldSlot), i.destinationSlot, i.packedType, signed = false)
        is AggregateInstruction.ArraySetIi -> context.heapStore(ARRAY_SET, i.addressSlot, immediate(i.field), immediate(i.value))
        is AggregateInstruction.ArraySetIs -> context.heapStore(ARRAY_SET, i.addressSlot, slot(i.fieldSlot), immediate(i.value))
        is AggregateInstruction.ArraySetSi -> context.heapStore(ARRAY_SET, i.addressSlot, immediate(i.field), slot(i.valueSlot))
        is AggregateInstruction.ArraySetSs -> context.heapStore(ARRAY_SET, i.addressSlot, slot(i.fieldSlot), slot(i.valueSlot))
        is AggregateInstruction.ArrayLenS -> {
            context.pushContext()
            context.pushLong(slot(i.addressSlot))
            context.emit.invokeStatic(ARRAY_LEN)
            context.storeLong(i.destinationSlot)
        }
        is AggregateInstruction.ArrayNewDefaultI -> context.arrayNewDefault(i.type, immediate(i.size), i.destinationSlot)
        is AggregateInstruction.ArrayNewDefaultS -> context.arrayNewDefault(i.type, slot(i.sizeSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewFixedS -> context.arrayNewFixed(i.type, i.firstElementSlot, i.size, i.destinationSlot)
        is AggregateInstruction.ArrayCopyIii ->
            context.arrayCopy(immediate(i.elementsToCopy), immediate(i.sourceOffset), immediate(i.destinationOffset), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopyIis ->
            context.arrayCopy(immediate(i.elementsToCopy), immediate(i.sourceOffset), slot(i.destinationOffsetSlot), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopyIsi ->
            context.arrayCopy(immediate(i.elementsToCopy), slot(i.sourceOffsetSlot), immediate(i.destinationOffset), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopyIss ->
            context.arrayCopy(immediate(i.elementsToCopy), slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopySii ->
            context.arrayCopy(slot(i.elementsToCopySlot), immediate(i.sourceOffset), immediate(i.destinationOffset), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopySis ->
            context.arrayCopy(slot(i.elementsToCopySlot), immediate(i.sourceOffset), slot(i.destinationOffsetSlot), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopySsi ->
            context.arrayCopy(slot(i.elementsToCopySlot), slot(i.sourceOffsetSlot), immediate(i.destinationOffset), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.ArrayCopySss ->
            context.arrayCopy(slot(i.elementsToCopySlot), slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot), i.sourceAddressSlot, i.destinationAddressSlot)
        is AggregateInstruction.RefI31I -> context.refI31(immediate(i.value), i.destinationSlot)
        is AggregateInstruction.RefI31S -> context.refI31(slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.I31GetSignedS -> context.slotHelper(I31_GET_SIGNED, i.valueSlot, i.destinationSlot)
        is AggregateInstruction.I31GetUnsignedS -> context.slotHelper(I31_GET_UNSIGNED, i.valueSlot, i.destinationSlot)
        else -> context.lowerHelperCall(i.helperCall() ?: unsupported(i))
    }
}

private fun LowerContext.structGet(
    addressSlot: Int,
    field: Int,
    destination: Int,
    packed: PackedType? = null,
    signed: Boolean = false,
) {
    pushContext()
    pushLong(slot(addressSlot))
    emit.int(field)
    emit.invokeStatic(STRUCT_GET)
    if (packed != null) unpack(packed, signed)
    storeLong(destination)
}

private fun LowerContext.arrayGet(
    addressSlot: Int,
    index: Operand,
    destination: Int,
    packed: PackedType? = null,
    signed: Boolean = false,
) {
    pushContext()
    pushLong(slot(addressSlot))
    pushInt(index)
    emit.invokeStatic(ARRAY_GET)
    if (packed != null) unpack(packed, signed)
    storeLong(destination)
}

private fun LowerContext.heapStore(
    method: MethodRef,
    addressSlot: Int,
    index: Operand,
    value: Operand,
) {
    pushContext()
    pushLong(slot(addressSlot))
    pushInt(index)
    pushLong(value)
    emit.invokeStatic(method)
}

private fun LowerContext.structNew(
    type: TypeIndex,
    firstFieldSlot: Int,
    destination: Int,
) {
    val fields = declarations.structFields(type).size
    pushContext()
    pushResource(Resource.RuntimeType(type.index))
    pushSlots(firstFieldSlot, fields)
    emit.invokeStatic(STRUCT_NEW)
    storeLong(destination)
}

private fun LowerContext.arrayNewDefault(
    type: TypeIndex,
    size: Operand,
    destination: Int,
) {
    pushContext()
    pushResource(Resource.RuntimeType(type.index))
    pushInt(size)
    emit.long(declarations.arrayDefault(type))
    emit.invokeStatic(ARRAY_NEW_DEFAULT)
    storeLong(destination)
}

private fun LowerContext.arrayNewFixed(
    type: TypeIndex,
    firstElementSlot: Int,
    size: Int,
    destination: Int,
) {
    pushContext()
    pushResource(Resource.RuntimeType(type.index))
    pushSlots(firstElementSlot, size)
    emit.int(size)
    emit.invokeStatic(ARRAY_NEW_FIXED)
    storeLong(destination)
}

private fun LowerContext.arrayCopy(
    count: Operand,
    sourceOffset: Operand,
    destinationOffset: Operand,
    sourceSlot: Int,
    destinationSlot: Int,
) {
    pushContext()
    pushLong(slot(sourceSlot))
    pushInt(sourceOffset)
    pushLong(slot(destinationSlot))
    pushInt(destinationOffset)
    pushInt(count)
    emit.invokeStatic(ARRAY_COPY)
}

private fun LowerContext.refI31(
    value: Operand,
    destination: Int,
) {
    pushInt(value)
    emit.invokeStatic(REF_I31)
    storeLong(destination)
}

private fun LowerContext.pushSlots(
    first: Int,
    count: Int,
) {
    emit.int(maxOf(count, 1))
    emit.newLongArray()
    for (index in 0 until count) {
        emit.insn(Opcodes.DUP)
        emit.int(index)
        pushLong(slot(first + index))
        emit.insn(Opcodes.LASTORE)
    }
}

private fun LowerContext.unpack(
    packed: PackedType,
    signed: Boolean,
) {
    emit.insn(Opcodes.L2I)
    when (packed) {
        PackedType.I8 -> if (signed) {
            emit.insn(Opcodes.I2B)
        } else {
            emit.int(0xFF)
            emit.insn(Opcodes.IAND)
        }
        PackedType.I16 -> emit.insn(if (signed) Opcodes.I2S else Opcodes.I2C)
    }
    emit.insn(Opcodes.I2L)
}
