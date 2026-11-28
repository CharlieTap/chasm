package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_METER
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_REMAINING_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.CAST_FAILED
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_DATA_RANGE
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_ELEMENT_RANGE
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.COPY_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.COPY_DATA
import io.github.charlietap.chasm.compiler.backend.abi.COPY_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.DATA_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.FILL_BYTES
import io.github.charlietap.chasm.compiler.backend.abi.FILL_CHARS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_DOUBLES
import io.github.charlietap.chasm.compiler.backend.abi.FILL_FLOATS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_INTS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_LONGS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.GUEST_HEAP_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.INIT_ARRAY_DATA
import io.github.charlietap.chasm.compiler.backend.abi.INIT_ARRAY_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.REFILL_ALLOCATION
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.STORAGE_ARRAY_DATA_FIELD
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ArrayElementKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ArrayStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.FieldStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.arrayElementKind
import io.github.charlietap.chasm.compiler.backend.dex.gc.HALF_BITS
import io.github.charlietap.chasm.compiler.backend.dex.gc.allocateStruct
import io.github.charlietap.chasm.compiler.backend.dex.gc.combineLanes
import io.github.charlietap.chasm.compiler.backend.dex.gc.extendPacked
import io.github.charlietap.chasm.compiler.backend.dex.gc.getLane
import io.github.charlietap.chasm.compiler.backend.dex.gc.maskPacked
import io.github.charlietap.chasm.compiler.backend.dex.gc.putLane
import io.github.charlietap.chasm.runtime.execution.ARRAY_OVERHEAD
import io.github.charlietap.chasm.runtime.execution.maximumArrayLength
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal fun lowerManagedAggregateInstruction(
    instruction: AggregateInstruction,
    context: LowerContext,
) {
    when (val i = instruction) {
        is AggregateInstruction.StructNewS -> context.structNew(i.type, i.firstFieldSlot, i.destinationSlot)
        is AggregateInstruction.StructNewDefaultS -> context.structNew(i.type, null, i.destinationSlot)
        is AggregateInstruction.StructGetS -> context.structGet(i.addressSlot, i.type, i.fieldIndex, i.destinationSlot, signed = null)
        is AggregateInstruction.StructGetSignedS -> context.structGet(i.addressSlot, i.type, i.fieldIndex, i.destinationSlot, signed = true)
        is AggregateInstruction.StructGetUnsignedS -> context.structGet(i.addressSlot, i.type, i.fieldIndex, i.destinationSlot, signed = false)
        is AggregateInstruction.StructSetI -> context.structSet(i.addressSlot, i.type, i.fieldIndex, immediate(i.value))
        is AggregateInstruction.StructSetS -> context.structSet(i.addressSlot, i.type, i.fieldIndex, slot(i.valueSlot))
        is AggregateInstruction.RefCastStructGetS -> context.refCastStructGet(i.referenceSlot, i.typeTest, i.type, i.fieldIndex, i.destinationSlot)
        is AggregateInstruction.StructGetStructGetS -> context.structGetStructGet(i)
        is AggregateInstruction.LocalSetStructGetS -> {
            context.copyToSlot(i.localSlot, slot(i.sourceSlot))
            context.structGet(i.sourceSlot, i.type, i.fieldIndex, i.destinationSlot, signed = null)
        }
        is AggregateInstruction.ArrayNewDefaultI -> context.arrayNew(i.type, immediate(i.size.toLong()), null, i.destinationSlot)
        is AggregateInstruction.ArrayNewDefaultS -> context.arrayNew(i.type, slot(i.sizeSlot), null, i.destinationSlot)
        is AggregateInstruction.ArrayNewIi -> context.arrayNew(i.type, immediate(i.size.toLong()), immediate(i.value), i.destinationSlot)
        is AggregateInstruction.ArrayNewIs -> context.arrayNew(i.type, immediate(i.size.toLong()), slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewSi -> context.arrayNew(i.type, slot(i.sizeSlot), immediate(i.value), i.destinationSlot)
        is AggregateInstruction.ArrayNewSs -> context.arrayNew(i.type, slot(i.sizeSlot), slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewFixedS -> context.arrayNewFixed(i.type, i.firstElementSlot, i.size, i.destinationSlot)
        is AggregateInstruction.ArrayGetI -> context.arrayGet(i.addressSlot, i.type, immediate(i.field.toLong()), i.destinationSlot, signed = null)
        is AggregateInstruction.ArrayGetS -> context.arrayGet(i.addressSlot, i.type, slot(i.fieldSlot), i.destinationSlot, signed = null)
        is AggregateInstruction.ArrayGetSignedI -> context.arrayGet(i.addressSlot, i.type, immediate(i.field.toLong()), i.destinationSlot, signed = true)
        is AggregateInstruction.ArrayGetSignedS -> context.arrayGet(i.addressSlot, i.type, slot(i.fieldSlot), i.destinationSlot, signed = true)
        is AggregateInstruction.ArrayGetUnsignedI -> context.arrayGet(i.addressSlot, i.type, immediate(i.field.toLong()), i.destinationSlot, signed = false)
        is AggregateInstruction.ArrayGetUnsignedS -> context.arrayGet(i.addressSlot, i.type, slot(i.fieldSlot), i.destinationSlot, signed = false)
        is AggregateInstruction.ArraySetIi -> context.arraySet(i.addressSlot, i.type, immediate(i.field.toLong()), immediate(i.value))
        is AggregateInstruction.ArraySetIs -> context.arraySet(i.addressSlot, i.type, slot(i.fieldSlot), immediate(i.value))
        is AggregateInstruction.ArraySetSi -> context.arraySet(i.addressSlot, i.type, immediate(i.field.toLong()), slot(i.valueSlot))
        is AggregateInstruction.ArraySetSs -> context.arraySet(i.addressSlot, i.type, slot(i.fieldSlot), slot(i.valueSlot))
        is AggregateInstruction.ArrayLenS -> context.arrayLen(i.addressSlot, i.type, i.destinationSlot)
        is AggregateInstruction.RefI31I -> context.refI31(immediate(i.value.toLong()), i.destinationSlot)
        is AggregateInstruction.RefI31S -> context.refI31(slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.I31GetSignedS -> context.i31Get(i.valueSlot, i.destinationSlot, signed = true)
        is AggregateInstruction.I31GetUnsignedS -> context.i31Get(i.valueSlot, i.destinationSlot, signed = false)
        is AggregateInstruction.AnyConvertExternS -> context.copyToSlot(i.destinationSlot, slot(i.valueSlot))
        is AggregateInstruction.ExternConvertAnyS -> context.copyToSlot(i.destinationSlot, slot(i.valueSlot))
        is AggregateInstruction.ArrayCopyIii -> context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset.toLong()), i.sourceAddressSlot, immediate(i.sourceOffset.toLong()), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayCopyIis -> context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, immediate(i.sourceOffset.toLong()), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayCopyIsi -> context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset.toLong()), i.sourceAddressSlot, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayCopyIss -> context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayCopySii -> context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset.toLong()), i.sourceAddressSlot, immediate(i.sourceOffset.toLong()), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayCopySis -> context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, immediate(i.sourceOffset.toLong()), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayCopySsi -> context.arrayCopy(i.destinationAddressSlot, immediate(i.destinationOffset.toLong()), i.sourceAddressSlot, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayCopySss -> context.arrayCopy(i.destinationAddressSlot, slot(i.destinationOffsetSlot), i.sourceAddressSlot, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayFillIii -> context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset.toLong()), immediate(i.fillValue), immediate(i.elementsToFill.toLong()))
        is AggregateInstruction.ArrayFillIis -> context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), immediate(i.fillValue), immediate(i.elementsToFill.toLong()))
        is AggregateInstruction.ArrayFillIsi -> context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset.toLong()), slot(i.fillValueSlot), immediate(i.elementsToFill.toLong()))
        is AggregateInstruction.ArrayFillIss -> context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), slot(i.fillValueSlot), immediate(i.elementsToFill.toLong()))
        is AggregateInstruction.ArrayFillSii -> context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset.toLong()), immediate(i.fillValue), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayFillSis -> context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), immediate(i.fillValue), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayFillSsi -> context.arrayFill(i.addressSlot, i.type, immediate(i.arrayElementOffset.toLong()), slot(i.fillValueSlot), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayFillSss -> context.arrayFill(i.addressSlot, i.type, slot(i.arrayElementOffsetSlot), slot(i.fillValueSlot), slot(i.elementsToFillSlot))
        is AggregateInstruction.ArrayNewDataIi -> context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, immediate(i.sourceOffset.toLong()), immediate(i.arrayLength.toLong()), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementIi -> context.arrayNewElement(i.type, i.element.index, immediate(i.sourceOffset.toLong()), immediate(i.arrayLength.toLong()), i.destinationSlot)
        is AggregateInstruction.ArrayNewDataIs -> context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, immediate(i.sourceOffset.toLong()), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementIs -> context.arrayNewElement(i.type, i.element.index, immediate(i.sourceOffset.toLong()), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewDataSi -> context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, slot(i.sourceOffsetSlot), immediate(i.arrayLength.toLong()), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementSi -> context.arrayNewElement(i.type, i.element.index, slot(i.sourceOffsetSlot), immediate(i.arrayLength.toLong()), i.destinationSlot)
        is AggregateInstruction.ArrayNewDataSs -> context.arrayNewData(i.type, i.data.index, i.fieldWidthInBytes, slot(i.sourceOffsetSlot), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewElementSs -> context.arrayNewElement(i.type, i.element.index, slot(i.sourceOffsetSlot), slot(i.arrayLengthSlot), i.destinationSlot)
        is AggregateInstruction.ArrayInitDataIii -> context.arrayInitData(i.addressSlot, immediate(i.destinationOffset.toLong()), i.data.index, immediate(i.sourceOffset.toLong()), immediate(i.elementsToCopy.toLong()), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementIii -> context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset.toLong()), i.element.index, immediate(i.sourceOffset.toLong()), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayInitDataIis -> context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, immediate(i.sourceOffset.toLong()), immediate(i.elementsToCopy.toLong()), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementIis -> context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, immediate(i.sourceOffset.toLong()), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayInitDataIsi -> context.arrayInitData(i.addressSlot, immediate(i.destinationOffset.toLong()), i.data.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy.toLong()), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementIsi -> context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset.toLong()), i.element.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayInitDataIss -> context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy.toLong()), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementIss -> context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy.toLong()))
        is AggregateInstruction.ArrayInitDataSii -> context.arrayInitData(i.addressSlot, immediate(i.destinationOffset.toLong()), i.data.index, immediate(i.sourceOffset.toLong()), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementSii -> context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset.toLong()), i.element.index, immediate(i.sourceOffset.toLong()), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayInitDataSis -> context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, immediate(i.sourceOffset.toLong()), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementSis -> context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, immediate(i.sourceOffset.toLong()), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayInitDataSsi -> context.arrayInitData(i.addressSlot, immediate(i.destinationOffset.toLong()), i.data.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementSsi -> context.arrayInitElement(i.addressSlot, immediate(i.destinationOffset.toLong()), i.element.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        is AggregateInstruction.ArrayInitDataSss -> context.arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
        is AggregateInstruction.ArrayInitElementSss -> context.arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element.index, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
        else -> throw Unsupported("${i::class.simpleName} is not lowered for managed references yet")
    }
}

private fun LowerContext.structNew(
    type: TypeIndex,
    firstFieldSlot: Int?,
    destination: Int,
) {

    val storage = module.storagePlans.struct(type.index)
    chargeAllocation(module.structSizes.bytes(storage))
    allocateStruct(storage, type.index)

    if (firstFieldSlot != null) {
        storage.fields.forEachIndexed { index, field -> putFieldValue(Registers.OBJECT, storage, field, slot(firstFieldSlot + index)) }
    }

    storeReference(destination, Registers.OBJECT)
}

private fun LowerContext.structGet(
    addressSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
    destination: Int,
    signed: Boolean?,
) {

    val storage = module.storagePlans.struct(type.index)

    loadReference(Registers.OBJECT, addressSlot)
    getFieldValue(Registers.OBJECT, storage, storage.fields[fieldIndex], destination, signed)
}

private fun LowerContext.structSet(
    addressSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
    value: Operand,
) {

    val storage = module.storagePlans.struct(type.index)

    loadReference(Registers.OBJECT, addressSlot)
    receiver(Registers.OBJECT, storage.className)
    putFieldValue(Registers.OBJECT, storage, storage.fields[fieldIndex], value)
}

private fun LowerContext.refCastStructGet(
    referenceSlot: Int,
    typeTest: ReferenceType,
    type: TypeIndex,
    fieldIndex: Int,
    destination: Int,
) {

    loadReference(Registers.OBJECT, referenceSlot)
    val matches = emit.freshLabel()
    branchOnType(Registers.OBJECT, typeTest, matches)
    trap(CAST_FAILED)
    emit.place(matches)

    val storage = module.storagePlans.struct(type.index)
    getFieldValue(Registers.OBJECT, storage, storage.fields[fieldIndex], destination, signed = null)
}

private fun LowerContext.structGetStructGet(
    instruction: AggregateInstruction.StructGetStructGetS,
) {

    val first = module.storagePlans.struct(instruction.firstType.index)
    val second = module.storagePlans.struct(instruction.secondType.index)

    loadReference(Registers.OBJECT, instruction.addressSlot)
    receiver(Registers.OBJECT, first.className)
    getLane(Registers.OBJECT, Registers.OBJECT, first, first.fields[instruction.firstFieldIndex].lanes.single())

    getFieldValue(Registers.OBJECT, second, second.fields[instruction.secondFieldIndex], instruction.destinationSlot, signed = null)
}

private fun LowerContext.arrayNew(
    type: TypeIndex,
    size: Operand,
    value: Operand?,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    loadInt(Registers.I1, Registers.W0, size)
    chargeArray(storage.elementShift, Registers.I1)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, element.arrayDescriptor)

    if (value != null && !(value is Operand.Immediate && value.bits == 0L)) {
        fillArray(element.descriptor, value)
    }

    wrapArray(type, storage, element, destination)
}

private fun LowerContext.fillArray(
    element: String,
    value: Operand,
) {

    val fill = MethodRef(ARRAYS, "fill", listOf(fillArrayDescriptor(element), fillValueDescriptor(element)), "V")

    when (element) {
        "J" -> {
            loadWide(Registers.W1, value)
            emit.invoke(fill, Registers.SECOND_OBJECT, Registers.W1, Registers.W1 + 1)
        }
        "D" -> {
            loadDouble(Registers.W1, value)
            emit.invoke(fill, Registers.SECOND_OBJECT, Registers.W1, Registers.W1 + 1)
        }
        "F" -> {
            loadFloat(Registers.I0, Registers.W0, value)
            emit.invoke(fill, Registers.SECOND_OBJECT, Registers.I0)
        }
        "I", "B", "C" -> {
            loadInt(Registers.I0, Registers.W0, value)
            narrowStorage(element, Registers.I0)
            emit.invoke(fill, Registers.SECOND_OBJECT, Registers.I0)
        }
        else -> {
            loadReference(Registers.OBJECT, value)
            emit.invoke(fill, Registers.SECOND_OBJECT, Registers.OBJECT)
        }
    }
}

private fun LowerContext.arrayNewFixed(
    type: TypeIndex,
    firstElementSlot: Int,
    size: Int,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    chargeAllocation((size shl storage.elementShift) + ARRAY_OVERHEAD)
    emit.const(Registers.I1, size)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, element.arrayDescriptor)

    for (index in 0 until size) {
        emit.const(Registers.I3, index)
        val value = loadElement(element.descriptor, slot(firstElementSlot + index))
        emit.arrayPut(arrayKind(element.descriptor), value, Registers.SECOND_OBJECT, Registers.I3)
    }

    wrapArray(type, storage, element, destination)
}

private fun LowerContext.chargeArray(
    shift: Int,
    length: Int,
) {

    val exhausted = trapLabel(GUEST_HEAP_EXHAUSTED)
    emit.branchZero(Cond.Lt, length, exhausted)
    emit.const(Registers.W0 + 1, maximumArrayLength(shift))
    emit.branchCompare(Cond.Gt, length, Registers.W0 + 1, exhausted)
    emit.literal(LiteralOp.ShlInt, Registers.W0 + 1, length, shift)
    emit.literal(LiteralOp.AddInt, Registers.W0 + 1, Registers.W0 + 1, ARRAY_OVERHEAD)
    chargeRegister(Registers.W0 + 1)
}

private fun LowerContext.chargeAllocation(
    bytes: Int,
) {
    emit.const(Registers.W0 + 1, bytes)
    chargeRegister(Registers.W0 + 1)
}

private fun LowerContext.chargeRegister(
    bytesRegister: Int,
) {

    val charged = emit.freshLabel()
    emit.getField(Registers.W1, Registers.CONTEXT, CONTEXT_TYPE, ALLOCATION_FIELD, ALLOCATION_METER)
    emit.getField(Registers.W0, Registers.W1, ALLOCATION_METER, ALLOCATION_REMAINING_FIELD, "I")
    emit.binary(BinaryOp.SubInt, Registers.W0, Registers.W0, bytesRegister)
    emit.putField(Registers.W0, Registers.W1, ALLOCATION_METER, ALLOCATION_REMAINING_FIELD, "I")
    emit.branchZero(Cond.Ge, Registers.W0, charged)
    emit.invoke(REFILL_ALLOCATION, Registers.CONTEXT, bytesRegister)
    emit.place(charged)
}

private const val I31_BYTES = 16

private fun LowerContext.wrapArray(
    type: TypeIndex,
    storage: ArrayStorage,
    element: ArrayElementKind,
    destination: Int,
) {
    loadObject(Registers.THIRD_OBJECT, Resource.Rtt(type.index), RTT_TYPE)
    emit.newInstance(Registers.OBJECT, storage.className)
    emit.invokeConstructor(
        storage.className,
        RTT_TYPE,
        element.arrayDescriptor,
        Registers.OBJECT,
        Registers.THIRD_OBJECT,
        Registers.SECOND_OBJECT,
    )
    storeReference(destination, Registers.OBJECT)
}

private fun LowerContext.arrayElement(type: TypeIndex): ArrayElementKind {
    return arrayElementKind(declarations.arrayElement(type))
}

private fun LowerContext.arrayGet(
    addressSlot: Int,
    type: TypeIndex,
    index: Operand,
    destination: Int,
    signed: Boolean?,
) {

    val element = arrayElement(type).descriptor
    loadArrayData(addressSlot, type, index)

    when (element) {
        "J", "D" -> {
            emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I1)
            if (element == "J") store(destination, Registers.W0) else storeDouble(destination, Registers.W0)
        }
        "I", "F", "B", "C" -> {
            emit.arrayGet(arrayKind(element), Registers.I0, Registers.SECOND_OBJECT, Registers.I1)
            widenStorage(element, Registers.I0, signed)
            if (element == "F") storeFloat(destination, Registers.I0) else storeInt(destination, Registers.I0)
        }
        else -> {
            emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I1)
            storeReference(destination, Registers.OBJECT)
        }
    }
}

private fun LowerContext.arraySet(
    addressSlot: Int,
    type: TypeIndex,
    index: Operand,
    value: Operand,
) {

    val element = arrayElement(type).descriptor
    loadArrayData(addressSlot, type, index)

    emit.arrayPut(arrayKind(element), loadElement(element, value), Registers.SECOND_OBJECT, Registers.I1)
}

private fun LowerContext.loadArrayData(
    addressSlot: Int,
    type: TypeIndex,
    index: Operand,
) {
    loadReference(Registers.OBJECT, addressSlot)
    loadStorageData(type)
    loadInt(Registers.I1, Registers.W0, index)
    emit.arrayLength(Registers.I0, Registers.SECOND_OBJECT)
    emit.branchZero(Cond.Lt, Registers.I1, trapLabel(ARRAY_OUT_OF_BOUNDS))
    emit.branchCompare(Cond.Ge, Registers.I1, Registers.I0, trapLabel(ARRAY_OUT_OF_BOUNDS))
}

private fun LowerContext.arrayCopy(
    destinationSlot: Int,
    destinationOffset: Operand,
    sourceSlot: Int,
    sourceOffset: Operand,
    count: Operand,
) {
    loadReference(Registers.OBJECT, destinationSlot)
    loadInt(Registers.I0, Registers.W0, destinationOffset)
    loadReference(Registers.SECOND_OBJECT, sourceSlot)
    loadInt(Registers.I1, Registers.W1, sourceOffset)
    loadInt(Registers.I3, Registers.W0, count)
    emit.invoke(COPY_ARRAY, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I1, Registers.I3)
}

private fun LowerContext.arrayFill(
    addressSlot: Int,
    type: TypeIndex,
    offset: Operand,
    value: Operand,
    count: Operand,
) {

    val element = arrayElement(type).descriptor
    loadReference(Registers.OBJECT, addressSlot)
    loadInt(Registers.I0, Registers.W0, offset)

    when (element) {
        "J", "D" -> {
            if (element == "J") loadWide(Registers.W1, value) else loadDouble(Registers.W1, value)
            loadInt(Registers.I3, Registers.W0, count)
            emit.invoke(if (element == "J") FILL_LONGS else FILL_DOUBLES, Registers.OBJECT, Registers.I0, Registers.W1, Registers.W1 + 1, Registers.I3)
        }
        "I", "F", "B", "C" -> {
            if (element == "F") loadFloat(Registers.I1, Registers.W1, value) else loadInt(Registers.I1, Registers.W1, value)
            loadInt(Registers.I3, Registers.W0, count)
            val fill = when (element) {
                "I" -> FILL_INTS
                "F" -> FILL_FLOATS
                "B" -> FILL_BYTES
                else -> FILL_CHARS
            }
            emit.invoke(fill, Registers.OBJECT, Registers.I0, Registers.I1, Registers.I3)
        }
        else -> {
            loadReference(Registers.SECOND_OBJECT, value)
            loadInt(Registers.I3, Registers.W0, count)
            emit.invoke(FILL_OBJECTS, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I3)
        }
    }
}

private fun LowerContext.arrayNewData(
    type: TypeIndex,
    data: Int,
    width: Int,
    offset: Operand,
    length: Operand,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val element = arrayElement(type)
    loadObject(Registers.OBJECT, Resource.Data(data), DATA_INSTANCE)
    loadInt(Registers.I0, Registers.W0, offset)
    loadInt(Registers.I1, Registers.W1, length)
    emit.const(Registers.I3, width)
    emit.invoke(CHECK_DATA_RANGE, Registers.OBJECT, Registers.I0, Registers.I1, Registers.I3)
    chargeArray(storage.elementShift, Registers.I1)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, element.arrayDescriptor)
    emit.const(Registers.I3, 0)
    emit.invoke(COPY_DATA, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I3, Registers.I1)

    wrapArray(type, storage, element, destination)
}

private fun LowerContext.arrayNewElement(
    type: TypeIndex,
    element: Int,
    offset: Operand,
    length: Operand,
    destination: Int,
) {

    val storage = module.storagePlans.array(type.index)
    val kind = arrayElement(type)
    loadObject(Registers.OBJECT, Resource.ReferenceSegment(element), REFERENCE_SEGMENT)
    loadInt(Registers.I0, Registers.W0, offset)
    loadInt(Registers.I1, Registers.W1, length)
    emit.invoke(CHECK_ELEMENT_RANGE, Registers.OBJECT, Registers.I0, Registers.I1)
    chargeArray(storage.elementShift, Registers.I1)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, kind.arrayDescriptor)
    emit.const(Registers.I3, 0)
    emit.invoke(COPY_ELEMENTS, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I3, Registers.I1)

    wrapArray(type, storage, kind, destination)
}

private fun LowerContext.arrayInitData(
    addressSlot: Int,
    destinationOffset: Operand,
    data: Int,
    sourceOffset: Operand,
    count: Operand,
    width: Int,
) {

    loadObject(Registers.SECOND_OBJECT, Resource.Data(data), DATA_INSTANCE)
    loadReference(Registers.OBJECT, addressSlot)
    loadInt(Registers.I0, Registers.W0, destinationOffset)
    loadInt(Registers.I1, Registers.W1, sourceOffset)
    loadInt(Registers.I3, Registers.W0, count)

    val window = registers.window
    emit.moveObject(window, Registers.OBJECT)
    emit.move(window + 1, Registers.I0)
    emit.moveObject(window + 2, Registers.SECOND_OBJECT)
    emit.move(window + 3, Registers.I1)
    emit.move(window + 4, Registers.I3)
    emit.const(Registers.I0, width)
    emit.move(window + 5, Registers.I0)
    emit.invokeRange(INIT_ARRAY_DATA, window, INIT_ARRAY_DATA.words)
    restoreWindow(binding = true)
}

private fun LowerContext.arrayInitElement(
    addressSlot: Int,
    destinationOffset: Operand,
    element: Int,
    sourceOffset: Operand,
    count: Operand,
) {

    loadObject(Registers.SECOND_OBJECT, Resource.ReferenceSegment(element), REFERENCE_SEGMENT)
    loadReference(Registers.OBJECT, addressSlot)
    loadInt(Registers.I0, Registers.W0, destinationOffset)
    loadInt(Registers.I1, Registers.W1, sourceOffset)
    loadInt(Registers.I3, Registers.W0, count)
    emit.invoke(INIT_ARRAY_ELEMENTS, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I1, Registers.I3)
}

private fun LowerContext.arrayLen(
    addressSlot: Int,
    type: TypeIndex?,
    destination: Int,
) {

    loadReference(Registers.OBJECT, addressSlot)
    if (type == null) {
        receiver(Registers.OBJECT, MANAGED_ARRAY)
        emit.invoke(ARRAY_LENGTH, Registers.OBJECT)
        emit.moveResult(Registers.I0)
        return storeInt(destination, Registers.I0)
    }

    loadStorageData(type)
    emit.arrayLength(Registers.I0, Registers.SECOND_OBJECT)
    storeInt(destination, Registers.I0)
}

private fun LowerContext.loadStorageData(type: TypeIndex) {

    val className = module.storagePlans.array(type.index).className

    receiver(Registers.OBJECT, className)
    emit.getField(Registers.SECOND_OBJECT, Registers.OBJECT, className, STORAGE_ARRAY_DATA_FIELD, arrayElement(type).arrayDescriptor)
}

private fun LowerContext.refI31(
    value: Operand,
    destination: Int,
) {
    chargeAllocation(I31_BYTES)
    loadInt(Registers.I0, Registers.W0, value)
    emit.literal(LiteralOp.ShlInt, Registers.I0, Registers.I0, 1)
    emit.literal(LiteralOp.UshrInt, Registers.I0, Registers.I0, 1)
    emit.newInstance(Registers.OBJECT, I31_REFERENCE)
    emit.invokeConstructor(I31_REFERENCE, "I", Registers.OBJECT, Registers.I0)
    storeReference(destination, Registers.OBJECT)
}

private fun LowerContext.i31Get(
    valueSlot: Int,
    destination: Int,
    signed: Boolean,
) {

    loadReference(Registers.OBJECT, valueSlot)
    receiver(Registers.OBJECT, I31_REFERENCE)
    emit.getField(Registers.I0, Registers.OBJECT, I31_REFERENCE, I31_VALUE, "I")

    if (signed) {
        emit.literal(LiteralOp.ShlInt, Registers.I0, Registers.I0, 1)
        emit.literal(LiteralOp.ShrInt, Registers.I0, Registers.I0, 1)
    }

    storeInt(destination, Registers.I0)
}

private fun LowerContext.receiver(
    register: Int,
    owner: String,
) {
    emit.branchZero(Cond.Eq, register, trapLabel(NULL_REFERENCE))
    emit.checkCast(register, owner)
}

private fun LowerContext.getFieldValue(
    receiverRegister: Int,
    storage: StructStorage,
    field: FieldStorage,
    destination: Int,
    signed: Boolean?,
) {

    receiver(receiverRegister, storage.className)

    val lanes = field.lanes
    if (lanes.size == 2) {
        getLane(Registers.I0, receiverRegister, storage, lanes[0])
        getLane(Registers.I1, receiverRegister, storage, lanes[1])
        combineLanes(Registers.W0, Registers.I0, Registers.I1)
        store(destination, Registers.W0)
        return
    }

    val lane = lanes.single()
    if (lane.kind == LaneKind.REFERENCE) {
        getLane(Registers.SECOND_OBJECT, receiverRegister, storage, lane)
        storeReference(destination, Registers.SECOND_OBJECT)
        return
    }

    getLane(Registers.I0, receiverRegister, storage, lane)
    extendPacked(field.type, Registers.I0, signed)
    storeInt(destination, Registers.I0)
}

private fun LowerContext.putFieldValue(
    receiverRegister: Int,
    storage: StructStorage,
    field: FieldStorage,
    value: Operand,
) {

    val lanes = field.lanes
    if (lanes.size == 2) {
        loadWide(Registers.W1, value)
        emit.convert(Conversion.LongToInt, Registers.I0, Registers.W1)
        putLane(Registers.I0, receiverRegister, storage, lanes[0])
        emit.const(Registers.NARROW, HALF_BITS)
        emit.binary(BinaryOp.UshrLong, Registers.W1, Registers.W1, Registers.NARROW)
        emit.convert(Conversion.LongToInt, Registers.I0, Registers.W1)
        putLane(Registers.I0, receiverRegister, storage, lanes[1])
        return
    }

    val lane = lanes.single()
    if (lane.kind == LaneKind.REFERENCE) {
        loadReference(Registers.SECOND_OBJECT, value)
        putLane(Registers.SECOND_OBJECT, receiverRegister, storage, lane)
        return
    }

    loadInt(Registers.I0, Registers.W0, value)
    maskPacked(field.type, Registers.I0)
    putLane(Registers.I0, receiverRegister, storage, lane)
}

private fun LowerContext.loadElement(
    element: String,
    value: Operand,
): Int {

    if (element.length == 1) {
        return loadStorage(element, value, Registers.OBJECT)
    }

    loadReference(Registers.OBJECT, value)
    return Registers.OBJECT
}

private fun LowerContext.loadStorage(
    descriptor: String,
    value: Operand,
    referenceRegister: Int,
): Int {
    return when (descriptor) {
        "J" -> {
            loadWide(Registers.W1, value)
            Registers.W1
        }
        "D" -> {
            loadDouble(Registers.W1, value)
            Registers.W1
        }
        "F" -> {
            loadFloat(Registers.I0, Registers.W0, value)
            Registers.I0
        }
        "I", "B", "C" -> {
            loadInt(Registers.I0, Registers.W0, value)
            narrowStorage(descriptor, Registers.I0)
            Registers.I0
        }
        else -> {
            loadReference(referenceRegister, value)
            emit.checkCast(referenceRegister, descriptor)
            referenceRegister
        }
    }
}

private fun LowerContext.narrowStorage(
    descriptor: String,
    register: Int,
) {
    when (descriptor) {
        "B" -> emit.convert(Conversion.IntToByte, register, register)
        "C" -> emit.convert(Conversion.IntToChar, register, register)
    }
}

private fun LowerContext.widenStorage(
    descriptor: String,
    register: Int,
    signed: Boolean?,
) {
    when {
        descriptor == "B" && signed == false -> emit.literal(LiteralOp.AndInt, register, register, 0xFF)
        descriptor == "C" && signed == true -> emit.convert(Conversion.IntToShort, register, register)
    }
}

private fun arrayKind(element: String): ArrayKind {
    return when (element) {
        "I", "F" -> ArrayKind.Int
        "J", "D" -> ArrayKind.Wide
        "B" -> ArrayKind.Byte
        "C" -> ArrayKind.Char
        else -> ArrayKind.Object
    }
}

private fun fillArrayDescriptor(element: String): String {
    return if (element.length == 1) "[$element" else "[$OBJECT_TYPE"
}

private fun fillValueDescriptor(element: String): String {
    return if (element.length == 1) element else OBJECT_TYPE
}

private const val ARRAYS = "Ljava/util/Arrays;"
private const val I31_VALUE = "value"
