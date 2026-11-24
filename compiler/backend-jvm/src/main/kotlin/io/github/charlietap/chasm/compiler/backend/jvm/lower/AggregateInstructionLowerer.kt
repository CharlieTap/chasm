package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_METER
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_REMAINING_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.CAST_FAILED
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_DATA_RANGE
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_ELEMENT_RANGE
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.COPY_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.COPY_DATA
import io.github.charlietap.chasm.compiler.backend.abi.COPY_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_VALUE_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.INIT_ARRAY_DATA
import io.github.charlietap.chasm.compiler.backend.abi.INIT_ARRAY_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.REFILL_ALLOCATION
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.STORAGE_ARRAY_DATA_FIELD
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.analysis.gc.FieldStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.compiler.backend.jvm.gc.BYTE_MASK
import io.github.charlietap.chasm.compiler.backend.jvm.gc.HALF_BITS
import io.github.charlietap.chasm.compiler.backend.jvm.gc.StorageElement
import io.github.charlietap.chasm.compiler.backend.jvm.gc.allocateStruct
import io.github.charlietap.chasm.compiler.backend.jvm.gc.getLane
import io.github.charlietap.chasm.compiler.backend.jvm.gc.putField
import io.github.charlietap.chasm.compiler.backend.jvm.gc.storageElement
import io.github.charlietap.chasm.runtime.execution.ARRAY_OVERHEAD
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import org.objectweb.asm.Opcodes

internal fun lowerAggregateInstruction(
    instruction: AggregateInstruction,
    context: LowerContext,
) {
    when (val i = instruction) {
        is AggregateInstruction.StructNewS -> context.structNew(i.type, i.firstFieldSlot, i.destinationSlot)
        is AggregateInstruction.StructNewDefaultS -> context.structNew(i.type, null, i.destinationSlot)
        is AggregateInstruction.StructGetS -> context.structGet(i.addressSlot, i.type, i.fieldIndex, i.destinationSlot, Extension.None)
        is AggregateInstruction.StructGetSignedS -> context.structGet(i.addressSlot, i.type, i.fieldIndex, i.destinationSlot, Extension.Signed)
        is AggregateInstruction.StructGetUnsignedS -> context.structGet(i.addressSlot, i.type, i.fieldIndex, i.destinationSlot, Extension.Unsigned)
        is AggregateInstruction.StructSetI -> context.structSet(i.addressSlot, i.type, i.fieldIndex, immediate(i.value))
        is AggregateInstruction.StructSetS -> context.structSet(i.addressSlot, i.type, i.fieldIndex, slot(i.valueSlot))
        is AggregateInstruction.RefCastStructGetS -> context.refCastStructGet(i.referenceSlot, i.typeTest, i.type, i.fieldIndex, i.destinationSlot)
        is AggregateInstruction.StructGetStructGetS -> context.structGetStructGet(i)
        is AggregateInstruction.LocalSetStructGetS -> {
            context.copyToSlot(i.localSlot, slot(i.sourceSlot))
            context.structGet(i.sourceSlot, i.type, i.fieldIndex, i.destinationSlot, Extension.None)
        }
        is AggregateInstruction.ArrayNewDefaultI -> context.arrayNew(i.type, immediate(i.size), null, i.destinationSlot)
        is AggregateInstruction.ArrayNewDefaultS -> context.arrayNew(i.type, slot(i.sizeSlot), null, i.destinationSlot)
        is AggregateInstruction.ArrayNewIi -> context.arrayNew(i.type, immediate(i.size), immediate(i.value), i.destinationSlot)
        is AggregateInstruction.ArrayNewIs -> context.arrayNew(i.type, immediate(i.size), slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewSi -> context.arrayNew(i.type, slot(i.sizeSlot), immediate(i.value), i.destinationSlot)
        is AggregateInstruction.ArrayNewSs -> context.arrayNew(i.type, slot(i.sizeSlot), slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewFixedS -> context.arrayNewFixed(i.type, i.firstElementSlot, i.size, i.destinationSlot)
        is AggregateInstruction.ArrayNewDataIi ->
            context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, immediate(i.sourceOffset), immediate(i.arrayLength), i.destinationSlot)
        is AggregateInstruction.ArrayNewDataIs ->
            context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, immediate(i.sourceOffset), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewDataSi ->
            context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, slot(i.sourceOffsetSlot), immediate(i.arrayLength), i.destinationSlot)
        is AggregateInstruction.ArrayNewDataSs ->
            context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, slot(i.sourceOffsetSlot), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementIi ->
            context.arrayNewElement(i.type, i.element.index, immediate(i.sourceOffset), immediate(i.arrayLength), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementIs ->
            context.arrayNewElement(i.type, i.element.index, immediate(i.sourceOffset), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementSi ->
            context.arrayNewElement(i.type, i.element.index, slot(i.sourceOffsetSlot), immediate(i.arrayLength), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementSs ->
            context.arrayNewElement(i.type, i.element.index, slot(i.sourceOffsetSlot), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayGetI -> context.arrayGet(i.addressSlot, i.type, immediate(i.field), i.destinationSlot, Extension.None)
        is AggregateInstruction.ArrayGetS -> context.arrayGet(i.addressSlot, i.type, slot(i.fieldSlot), i.destinationSlot, Extension.None)
        is AggregateInstruction.ArrayGetSignedI -> context.arrayGet(i.addressSlot, i.type, immediate(i.field), i.destinationSlot, Extension.Signed)
        is AggregateInstruction.ArrayGetSignedS -> context.arrayGet(i.addressSlot, i.type, slot(i.fieldSlot), i.destinationSlot, Extension.Signed)
        is AggregateInstruction.ArrayGetUnsignedI -> context.arrayGet(i.addressSlot, i.type, immediate(i.field), i.destinationSlot, Extension.Unsigned)
        is AggregateInstruction.ArrayGetUnsignedS -> context.arrayGet(i.addressSlot, i.type, slot(i.fieldSlot), i.destinationSlot, Extension.Unsigned)
        is AggregateInstruction.ArraySetIi -> context.arraySet(i.addressSlot, i.type, immediate(i.field), immediate(i.value))
        is AggregateInstruction.ArraySetIs -> context.arraySet(i.addressSlot, i.type, slot(i.fieldSlot), immediate(i.value))
        is AggregateInstruction.ArraySetSi -> context.arraySet(i.addressSlot, i.type, immediate(i.field), slot(i.valueSlot))
        is AggregateInstruction.ArraySetSs -> context.arraySet(i.addressSlot, i.type, slot(i.fieldSlot), slot(i.valueSlot))
        is AggregateInstruction.ArrayLenS -> context.arrayLen(i.addressSlot, i.type, i.destinationSlot)
        is AggregateInstruction.ArrayCopyIii ->
            context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset), i.sourceAddressSlot, immediate(i.sourceOffset), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayCopyIis ->
            context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, immediate(i.sourceOffset), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayCopyIsi ->
            context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset), i.sourceAddressSlot, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayCopyIss ->
            context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayCopySii ->
            context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset), i.sourceAddressSlot, immediate(i.sourceOffset), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayCopySis ->
            context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, immediate(i.sourceOffset), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayCopySsi ->
            context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset), i.sourceAddressSlot, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayCopySss ->
            context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayFillIii ->
            context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset), immediate(i.fillValue), immediate(i.elementsToFill))
        is AggregateInstruction.ArrayFillIis ->
            context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), immediate(i.fillValue), immediate(i.elementsToFill))
        is AggregateInstruction.ArrayFillIsi ->
            context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset), slot(i.fillValueSlot), immediate(i.elementsToFill))
        is AggregateInstruction.ArrayFillIss ->
            context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), slot(i.fillValueSlot), immediate(i.elementsToFill))
        is AggregateInstruction.ArrayFillSii ->
            context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset), immediate(i.fillValue), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayFillSis ->
            context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), immediate(i.fillValue), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayFillSsi ->
            context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset), slot(i.fillValueSlot), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayFillSss ->
            context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), slot(i.fillValueSlot), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayInitDataIii ->
            context.arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data.index, immediate(i.sourceOffset), immediate(i.elementsToCopy), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataIis ->
            context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, immediate(i.sourceOffset), immediate(i.elementsToCopy), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataIsi ->
            context.arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataIss ->
            context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataSii ->
            context.arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data.index, immediate(i.sourceOffset), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataSis ->
            context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, immediate(i.sourceOffset), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataSsi ->
            context.arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitDataSss ->
            context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementIii ->
            context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element.index, immediate(i.sourceOffset), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayInitElementIis ->
            context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, immediate(i.sourceOffset), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayInitElementIsi ->
            context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayInitElementIss ->
            context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy))
        is AggregateInstruction.ArrayInitElementSii ->
            context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element.index, immediate(i.sourceOffset), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayInitElementSis ->
            context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, immediate(i.sourceOffset), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayInitElementSsi ->
            context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayInitElementSss ->
            context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.RefI31I -> context.refI31(immediate(i.value), i.destinationSlot)
        is AggregateInstruction.RefI31S -> context.refI31(slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.I31GetSignedS -> context.i31Get(i.valueSlot, i.destinationSlot, signed = true)
        is AggregateInstruction.I31GetUnsignedS -> context.i31Get(i.valueSlot, i.destinationSlot, signed = false)
        is AggregateInstruction.AnyConvertExternS -> context.copyToSlot(i.destinationSlot, slot(i.valueSlot))
        is AggregateInstruction.ExternConvertAnyS -> context.copyToSlot(i.destinationSlot, slot(i.valueSlot))
    }
}

private fun LowerContext.structNew(
    type: TypeIndex,
    firstFieldSlot: Int?,
    destination: Int,
) {

    val storage = module.storagePlans.struct(type.index)
    chargeAllocation(storage.bytes)

    emit.visit {
        allocateStruct(storage) {
            pushResource(Resource.Rtt(type.index))
        }
    }

    if (firstFieldSlot != null) {
        storage.fields.forEachIndexed { index, field ->
            emit.insn(Opcodes.DUP)
            putField(storage, field, slot(firstFieldSlot + index))
        }
    }

    storeObject(destination)
}

private fun LowerContext.structGet(
    addressSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
    destination: Int,
    extension: Extension,
) {
    val storage = module.storagePlans.struct(type.index)
    readField(locals.objectSlot(addressSlot), storage, storage.fields[fieldIndex], destination, extension)
}

private fun LowerContext.structSet(
    addressSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
    value: Operand,
) {

    val storage = module.storagePlans.struct(type.index)

    pushReceiver(addressSlot, storage.className)
    putField(storage, storage.fields[fieldIndex], value)
}

private fun LowerContext.refCastStructGet(
    referenceSlot: Int,
    typeTest: ReferenceType,
    type: TypeIndex,
    fieldIndex: Int,
    destination: Int,
) {

    val matches = emit.label()
    branchOnType(slot(referenceSlot), typeTest, matches)
    trap(CAST_FAILED)
    emit.place(matches)

    structGet(referenceSlot, type, fieldIndex, destination, Extension.None)
}

private fun LowerContext.structGetStructGet(instruction: AggregateInstruction.StructGetStructGetS) {

    val first = module.storagePlans.struct(instruction.firstType.index)
    val lane = first.fields[instruction.firstFieldIndex].lanes.single()
    pushReceiver(instruction.addressSlot, first.className)
    emit.visit {
        getLane(first, lane)
    }
    emit.store(Opcodes.ASTORE, locals.scratchObject)

    val second = module.storagePlans.struct(instruction.secondType.index)
    readField(locals.scratchObject, second, second.fields[instruction.secondFieldIndex], instruction.destinationSlot, Extension.None)
}

private fun LowerContext.readField(
    receiver: Int,
    storage: StructStorage,
    field: FieldStorage,
    destination: Int,
    extension: Extension,
) {

    val lanes = field.lanes
    pushCheckedReceiver(receiver, storage.className)

    if (lanes.size == 2) {
        emit.insn(Opcodes.DUP)
        emit.visit {
            getLane(storage, lanes[0])
        }
        emit.insn(Opcodes.I2L)
        emit.long(LOW_HALF)
        emit.insn(Opcodes.LAND)
        emit.store(Opcodes.LSTORE, locals.scratchLong[0])
        emit.visit {
            getLane(storage, lanes[1])
        }
        emit.insn(Opcodes.I2L)
        emit.int(HALF_BITS)
        emit.insn(Opcodes.LSHL)
        emit.load(Opcodes.LLOAD, locals.scratchLong[0])
        emit.insn(Opcodes.LOR)
        storeLong(destination)
        return
    }

    val lane = lanes.single()
    emit.visit {
        getLane(storage, lane)
    }
    if (lane.kind == LaneKind.REFERENCE) {
        storeObject(destination)
        return
    }

    val packed = (field.type.storageType as? StorageType.Packed)?.type
    if (extension == Extension.Signed) {
        when (packed) {
            PackedType.I8 -> emit.insn(Opcodes.I2B)
            PackedType.I16 -> emit.insn(Opcodes.I2S)
            null -> Unit
        }
    }
    storeInt(destination)
}

private fun LowerContext.putField(
    storage: StructStorage,
    field: FieldStorage,
    value: Operand,
) {

    val lane = field.lanes.first()

    emit.visit {
        putField(storage, field, locals.scratchLong[0]) {
            when {
                field.lanes.size == 2 -> pushLong(value)
                lane.kind == LaneKind.REFERENCE -> pushObject(value)
                else -> pushInt(value)
            }
        }
    }
}

private fun LowerContext.chargeAllocation(bytes: Int) {
    emit.int(bytes)
    emit.store(Opcodes.ISTORE, locals.scratchValue)
    chargeScratchBytes()
}

private fun LowerContext.chargeScratchBytes() {

    val charged = emit.label()
    pushContext()
    emit.getField(CONTEXT_TYPE, ALLOCATION_FIELD, ALLOCATION_METER)
    emit.insn(Opcodes.DUP)
    emit.getField(ALLOCATION_METER, ALLOCATION_REMAINING_FIELD, "I")
    emit.load(Opcodes.ILOAD, locals.scratchValue)
    emit.insn(Opcodes.ISUB)
    emit.insn(Opcodes.DUP_X1)
    emit.putField(ALLOCATION_METER, ALLOCATION_REMAINING_FIELD, "I")
    emit.jump(Opcodes.IFGE, charged)
    pushContext()
    emit.load(Opcodes.ILOAD, locals.scratchValue)
    emit.invokeStatic(REFILL_ALLOCATION)
    emit.place(charged)
}

private fun LowerContext.chargeArray(
    length: Operand,
    shift: Int,
) {
    pushInt(length)
    emit.int(shift)
    emit.invokeStatic(CHECK_ARRAY_LENGTH)
    emit.insn(Opcodes.DUP)
    emit.store(Opcodes.ISTORE, locals.scratchInt)
    emit.int(shift)
    emit.insn(Opcodes.ISHL)
    emit.int(ARRAY_OVERHEAD)
    emit.insn(Opcodes.IADD)
    emit.store(Opcodes.ISTORE, locals.scratchValue)
    chargeScratchBytes()
}

private fun LowerContext.arrayNew(
    type: TypeIndex,
    size: Operand,
    value: Operand?,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    val data = locals.storageArrayScratch(element.arrayDescriptor)
    chargeArray(size, storage.elementShift)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    newElements(element)
    emit.store(Opcodes.ASTORE, data)

    if (value != null && !(value is Operand.Immediate && value.bits == 0L)) {
        emit.load(Opcodes.ALOAD, data)
        pushElement(element, value)
        emit.invokeStatic(element.wholeFill)
    }

    wrapArray(type, storage.className, element, data, destination)
}

private fun LowerContext.arrayNewFixed(
    type: TypeIndex,
    firstElementSlot: Int,
    size: Int,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    val data = locals.storageArrayScratch(element.arrayDescriptor)
    chargeAllocation((size shl storage.elementShift) + ARRAY_OVERHEAD)
    emit.int(size)
    newElements(element)
    emit.store(Opcodes.ASTORE, data)

    for (index in 0 until size) {
        emit.load(Opcodes.ALOAD, data)
        emit.int(index)
        pushElement(element, slot(firstElementSlot + index))
        emit.insn(element.storeOpcode)
    }

    wrapArray(type, storage.className, element, data, destination)
}

private fun LowerContext.arrayNewData(
    type: TypeIndex,
    dataIndex: Int,
    width: Int,
    offset: Operand,
    length: Operand,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    val data = locals.storageArrayScratch(element.arrayDescriptor)

    pushResource(Resource.Data(dataIndex))
    pushInt(offset)
    pushInt(length)
    emit.int(width)
    emit.invokeStatic(CHECK_DATA_RANGE)

    chargeArray(length, storage.elementShift)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    newElements(element)
    emit.store(Opcodes.ASTORE, data)

    pushResource(Resource.Data(dataIndex))
    pushInt(offset)
    emit.load(Opcodes.ALOAD, data)
    emit.int(0)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    emit.invokeStatic(COPY_DATA)

    wrapArray(type, storage.className, element, data, destination)
}

private fun LowerContext.arrayNewElement(
    type: TypeIndex,
    segment: Int,
    offset: Operand,
    length: Operand,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    val data = locals.storageArrayScratch(element.arrayDescriptor)

    pushResource(Resource.ReferenceSegment(segment))
    pushInt(offset)
    pushInt(length)
    emit.invokeStatic(CHECK_ELEMENT_RANGE)

    chargeArray(length, storage.elementShift)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    newElements(element)
    emit.store(Opcodes.ASTORE, data)

    pushResource(Resource.ReferenceSegment(segment))
    pushInt(offset)
    emit.load(Opcodes.ALOAD, data)
    emit.int(0)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    emit.invokeStatic(COPY_ELEMENTS)

    wrapArray(type, storage.className, element, data, destination)
}

private fun LowerContext.wrapArray(
    type: TypeIndex,
    className: String,
    element: StorageElement,
    data: Int,
    destination: Int,
) {
    emit.type(Opcodes.NEW, className)
    emit.insn(Opcodes.DUP)
    pushResource(Resource.Rtt(type.index))
    emit.load(Opcodes.ALOAD, data)
    emit.invokeSpecial(MethodRef(className, CONSTRUCTOR, listOf(RTT_TYPE, element.arrayDescriptor), "V"))
    storeObject(destination)
}

private fun LowerContext.arrayGet(
    addressSlot: Int,
    type: TypeIndex,
    index: Operand,
    destination: Int,
    extension: Extension,
) {

    val element = arrayElement(type)
    val data = checkedElements(addressSlot, type, element, index)
    emit.load(Opcodes.ALOAD, data)
    pushIndex(index)
    emit.insn(element.loadOpcode)

    when (element) {
        StorageElement.LONG -> storeLong(destination)
        StorageElement.FLOAT -> storeFloat(destination)
        StorageElement.DOUBLE -> storeDouble(destination)
        StorageElement.REFERENCE -> storeObject(destination)
        StorageElement.BYTE, StorageElement.CHAR, StorageElement.INT -> {
            if (element == StorageElement.BYTE && extension == Extension.Unsigned) {
                emit.int(BYTE_MASK)
                emit.insn(Opcodes.IAND)
            }
            if (element == StorageElement.CHAR && extension == Extension.Signed) {
                emit.insn(Opcodes.I2S)
            }
            storeInt(destination)
        }
    }
}

private fun LowerContext.arraySet(
    addressSlot: Int,
    type: TypeIndex,
    index: Operand,
    value: Operand,
) {

    val element = arrayElement(type)
    val data = checkedElements(addressSlot, type, element, index)
    emit.load(Opcodes.ALOAD, data)
    pushIndex(index)
    pushElement(element, value)
    emit.insn(element.storeOpcode)
}

private fun LowerContext.checkedElements(
    addressSlot: Int,
    type: TypeIndex,
    element: StorageElement,
    index: Operand,
): Int {

    val className = module.storagePlans.array(type.index).className
    val data = locals.storageArrayScratch(element.arrayDescriptor)
    pushReceiver(addressSlot, className)
    emit.getField(className, STORAGE_ARRAY_DATA_FIELD, element.arrayDescriptor)
    emit.store(Opcodes.ASTORE, data)

    val outOfBounds = trapLabel(ARRAY_OUT_OF_BOUNDS)
    if (index is Operand.Slot && locals.kind(index.slot) != SlotKinds.INT) {
        pushInt(index)
        emit.store(Opcodes.ISTORE, locals.scratchInt)
    }
    if (index !is Operand.Immediate || index.bits.toInt() < 0) {
        pushIndex(index)
        emit.jump(Opcodes.IFLT, outOfBounds)
    }
    pushIndex(index)
    emit.load(Opcodes.ALOAD, data)
    emit.insn(Opcodes.ARRAYLENGTH)
    emit.jump(Opcodes.IF_ICMPGE, outOfBounds)

    return data
}

private fun LowerContext.pushIndex(index: Operand) {
    if (index is Operand.Slot && locals.kind(index.slot) != SlotKinds.INT) {
        emit.load(Opcodes.ILOAD, locals.scratchInt)
    } else {
        pushInt(index)
    }
}

private fun LowerContext.arrayLen(
    addressSlot: Int,
    type: TypeIndex?,
    destination: Int,
) {

    if (type == null) {
        pushReceiver(addressSlot, MANAGED_ARRAY)
        emit.invokeStatic(ARRAY_LENGTH)
        storeInt(destination)
        return
    }

    val className = module.storagePlans.array(type.index).className
    pushReceiver(addressSlot, className)
    emit.getField(className, STORAGE_ARRAY_DATA_FIELD, arrayElement(type).arrayDescriptor)
    emit.insn(Opcodes.ARRAYLENGTH)
    storeInt(destination)
}

private fun LowerContext.arrayCopy(
    destinationSlot: Int,
    destinationOffset: Operand,
    sourceSlot: Int,
    sourceOffset: Operand,
    count: Operand,
) {
    pushObject(slot(destinationSlot))
    pushInt(destinationOffset)
    pushObject(slot(sourceSlot))
    pushInt(sourceOffset)
    pushInt(count)
    emit.invokeStatic(COPY_ARRAY)
}

private fun LowerContext.arrayFill(
    addressSlot: Int,
    type: TypeIndex,
    offset: Operand,
    value: Operand,
    count: Operand,
) {

    val element = arrayElement(type)
    pushObject(slot(addressSlot))
    pushInt(offset)
    pushElement(element, value)
    pushInt(count)
    emit.invokeStatic(element.rangeFill)
}

private fun LowerContext.arrayInitData(
    addressSlot: Int,
    destinationOffset: Operand,
    dataIndex: Int,
    sourceOffset: Operand,
    count: Operand,
    width: Int,
) {
    pushObject(slot(addressSlot))
    pushInt(destinationOffset)
    pushResource(Resource.Data(dataIndex))
    pushInt(sourceOffset)
    pushInt(count)
    emit.int(width)
    emit.invokeStatic(INIT_ARRAY_DATA)
}

private fun LowerContext.arrayInitElement(
    addressSlot: Int,
    destinationOffset: Operand,
    segment: Int,
    sourceOffset: Operand,
    count: Operand,
) {
    pushObject(slot(addressSlot))
    pushInt(destinationOffset)
    pushResource(Resource.ReferenceSegment(segment))
    pushInt(sourceOffset)
    pushInt(count)
    emit.invokeStatic(INIT_ARRAY_ELEMENTS)
}

private fun LowerContext.refI31(
    value: Operand,
    destination: Int,
) {
    chargeAllocation(I31_BYTES)
    emit.type(Opcodes.NEW, I31_REFERENCE)
    emit.insn(Opcodes.DUP)
    pushInt(value)
    emit.invokeSpecial(MethodRef(I31_REFERENCE, CONSTRUCTOR, listOf("I"), "V"))
    storeObject(destination)
}

private fun LowerContext.i31Get(
    valueSlot: Int,
    destination: Int,
    signed: Boolean,
) {

    pushReceiver(valueSlot, I31_REFERENCE)
    emit.getField(I31_REFERENCE, I31_VALUE_FIELD, "I")

    if (signed) {
        emit.int(1)
        emit.insn(Opcodes.ISHL)
        emit.int(1)
        emit.insn(Opcodes.ISHR)
    }

    storeInt(destination)
}

private fun LowerContext.arrayElement(type: TypeIndex): StorageElement {
    return storageElement(declarations.arrayElement(type))
}

private fun LowerContext.pushElement(
    element: StorageElement,
    value: Operand,
) {
    when (element) {
        StorageElement.BYTE, StorageElement.CHAR, StorageElement.INT -> pushInt(value)
        StorageElement.LONG -> pushLong(value)
        StorageElement.FLOAT -> pushFloat(value)
        StorageElement.DOUBLE -> pushDouble(value)
        StorageElement.REFERENCE -> pushObject(value)
    }

    val narrowing = element.narrowingOpcode
    if (narrowing != null) {
        emit.insn(narrowing)
    }
}

private fun LowerContext.newElements(element: StorageElement) {
    emit.visit {
        element.newArray(this)
    }
}

private enum class Extension {
    None,
    Signed,
    Unsigned,
}

private const val CONSTRUCTOR = "<init>"

private const val LOW_HALF = 0xFFFF_FFFFL

private const val I31_BYTES = 16
