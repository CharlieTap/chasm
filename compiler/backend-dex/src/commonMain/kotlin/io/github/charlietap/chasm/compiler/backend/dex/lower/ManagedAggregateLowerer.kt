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
import io.github.charlietap.chasm.compiler.backend.abi.INIT_ARRAY_DATA
import io.github.charlietap.chasm.compiler.backend.abi.INIT_ARRAY_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.REFILL_ALLOCATION
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.WASM_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.WASM_I31
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.dex.gc.ArrayShape
import io.github.charlietap.chasm.compiler.backend.dex.gc.GcField
import io.github.charlietap.chasm.compiler.backend.dex.gc.StructShape
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

    val shape = module.gc.struct(type.index)
    chargeAllocation(structBytes(shape))
    emit.newInstance(Registers.OBJECT, shape.className)
    emit.invokeConstructor(shape.className, Registers.OBJECT)

    if (firstFieldSlot != null) {
        shape.fields.forEachIndexed { index, field -> putFieldValue(Registers.OBJECT, field, slot(firstFieldSlot + index)) }
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
    loadReference(Registers.OBJECT, addressSlot)
    getFieldValue(Registers.OBJECT, module.gc.struct(type.index).fields[fieldIndex], destination, signed)
}

private fun LowerContext.structSet(
    addressSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
    value: Operand,
) {

    val field = module.gc.struct(type.index).fields[fieldIndex]
    loadReference(Registers.OBJECT, addressSlot)
    receiver(Registers.OBJECT, field.owner)
    putFieldValue(Registers.OBJECT, field, value)
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

    getFieldValue(Registers.OBJECT, module.gc.struct(type.index).fields[fieldIndex], destination, signed = null)
}

private fun LowerContext.structGetStructGet(
    instruction: AggregateInstruction.StructGetStructGetS,
) {

    val first = module.gc.struct(instruction.firstType.index).fields[instruction.firstFieldIndex]
    loadReference(Registers.OBJECT, instruction.addressSlot)
    receiver(Registers.OBJECT, first.owner)
    emit.getField(Registers.OBJECT, Registers.OBJECT, first.owner, first.name, first.descriptor)

    getFieldValue(Registers.OBJECT, module.gc.struct(instruction.secondType.index).fields[instruction.secondFieldIndex], instruction.destinationSlot, signed = null)
}

private fun LowerContext.arrayNew(
    type: TypeIndex,
    size: Operand,
    value: Operand?,
    destination: Int,
) {

    val shape = module.gc.array(type.index)
    loadInt(Registers.I1, Registers.W0, size)
    chargeArray(shape, Registers.I1)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, shape.data.descriptor)

    if (value != null && !(value is Operand.Immediate && value.bits == 0L)) {
        fillArray(shape, value)
    }

    wrapArray(shape, destination)
}

private fun LowerContext.fillArray(
    shape: ArrayShape,
    value: Operand,
) {

    val element = shape.data.descriptor.substring(1)
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

    val shape = module.gc.array(type.index)
    val element = shape.data.descriptor.substring(1)
    chargeAllocation((size shl elementShift(element)) + ARRAY_OVERHEAD)
    emit.const(Registers.I1, size)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, shape.data.descriptor)

    for (index in 0 until size) {
        emit.const(Registers.I3, index)
        val value = loadElement(element, slot(firstElementSlot + index))
        emit.arrayPut(arrayKind(element), value, Registers.SECOND_OBJECT, Registers.I3)
    }

    wrapArray(shape, destination)
}

private fun LowerContext.chargeArray(
    shape: ArrayShape,
    length: Int,
) {

    val shift = elementShift(shape.data.descriptor.substring(1))
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

private fun structBytes(
    shape: StructShape,
): Int {
    val fields = shape.fields.sumOf { field -> descriptorBytes(field.descriptor) }
    return (OBJECT_HEADER_BYTES + fields + 7) and 7.inv()
}

private fun elementShift(
    element: String,
): Int {
    return when (descriptorBytes(element)) {
        1 -> 0
        2 -> 1
        8 -> 3
        else -> 2
    }
}

private fun descriptorBytes(
    descriptor: String,
): Int {
    return when (descriptor) {
        "B" -> 1
        "C" -> 2
        "J", "D" -> 8
        else -> 4
    }
}

private const val OBJECT_HEADER_BYTES = 8

private const val I31_BYTES = 16

private fun LowerContext.wrapArray(
    shape: ArrayShape,
    destination: Int,
) {
    emit.newInstance(Registers.OBJECT, shape.className)
    emit.invokeConstructor(shape.className, shape.data.descriptor, Registers.OBJECT, Registers.SECOND_OBJECT)
    storeReference(destination, Registers.OBJECT)
}

private fun LowerContext.arrayGet(
    addressSlot: Int,
    type: TypeIndex,
    index: Operand,
    destination: Int,
    signed: Boolean?,
) {

    val data = module.gc.array(type.index).data
    val element = data.descriptor.substring(1)
    loadArrayData(addressSlot, data, index)

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

    val data = module.gc.array(type.index).data
    val element = data.descriptor.substring(1)
    loadArrayData(addressSlot, data, index)

    emit.arrayPut(arrayKind(element), loadElement(element, value), Registers.SECOND_OBJECT, Registers.I1)
}

private fun LowerContext.loadArrayData(
    addressSlot: Int,
    data: GcField,
    index: Operand,
) {
    loadReference(Registers.OBJECT, addressSlot)
    receiver(Registers.OBJECT, data.owner)
    emit.getField(Registers.SECOND_OBJECT, Registers.OBJECT, data.owner, data.name, data.descriptor)
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

    val element = module.gc.array(type.index).data.descriptor.substring(1)
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

    val shape = module.gc.array(type.index)
    loadObject(Registers.OBJECT, Resource.Data(data), DATA_INSTANCE)
    loadInt(Registers.I0, Registers.W0, offset)
    loadInt(Registers.I1, Registers.W1, length)
    emit.const(Registers.I3, width)
    emit.invoke(CHECK_DATA_RANGE, Registers.OBJECT, Registers.I0, Registers.I1, Registers.I3)
    chargeArray(shape, Registers.I1)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, shape.data.descriptor)
    emit.const(Registers.I3, 0)
    emit.invoke(COPY_DATA, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I3, Registers.I1)

    wrapArray(shape, destination)
}

private fun LowerContext.arrayNewElement(
    type: TypeIndex,
    element: Int,
    offset: Operand,
    length: Operand,
    destination: Int,
) {

    val shape = module.gc.array(type.index)
    loadObject(Registers.OBJECT, Resource.ReferenceSegment(element), REFERENCE_SEGMENT)
    loadInt(Registers.I0, Registers.W0, offset)
    loadInt(Registers.I1, Registers.W1, length)
    emit.invoke(CHECK_ELEMENT_RANGE, Registers.OBJECT, Registers.I0, Registers.I1)
    chargeArray(shape, Registers.I1)
    emit.newArray(Registers.SECOND_OBJECT, Registers.I1, shape.data.descriptor)
    emit.const(Registers.I3, 0)
    emit.invoke(COPY_ELEMENTS, Registers.OBJECT, Registers.I0, Registers.SECOND_OBJECT, Registers.I3, Registers.I1)

    wrapArray(shape, destination)
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
        receiver(Registers.OBJECT, WASM_ARRAY)
        emit.invoke(ARRAY_LENGTH, Registers.OBJECT)
        emit.moveResult(Registers.I0)
        return storeInt(destination, Registers.I0)
    }

    val data = module.gc.array(type.index).data
    receiver(Registers.OBJECT, data.owner)
    emit.getField(Registers.SECOND_OBJECT, Registers.OBJECT, data.owner, data.name, data.descriptor)
    emit.arrayLength(Registers.I0, Registers.SECOND_OBJECT)
    storeInt(destination, Registers.I0)
}

private fun LowerContext.refI31(
    value: Operand,
    destination: Int,
) {
    chargeAllocation(I31_BYTES)
    loadInt(Registers.I0, Registers.W0, value)
    emit.literal(LiteralOp.ShlInt, Registers.I0, Registers.I0, 1)
    emit.literal(LiteralOp.UshrInt, Registers.I0, Registers.I0, 1)
    emit.newInstance(Registers.OBJECT, WASM_I31)
    emit.invokeConstructor(WASM_I31, "I", Registers.OBJECT, Registers.I0)
    storeReference(destination, Registers.OBJECT)
}

private fun LowerContext.i31Get(
    valueSlot: Int,
    destination: Int,
    signed: Boolean,
) {

    loadReference(Registers.OBJECT, valueSlot)
    receiver(Registers.OBJECT, WASM_I31)
    emit.getField(Registers.I0, Registers.OBJECT, WASM_I31, I31_VALUE, "I")

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
    field: GcField,
    destination: Int,
    signed: Boolean?,
) {

    receiver(receiverRegister, field.owner)

    when (val descriptor = field.descriptor) {
        "J" -> {
            emit.getField(Registers.W0, receiverRegister, field.owner, field.name, descriptor)
            store(destination, Registers.W0)
        }
        "D" -> {
            emit.getField(Registers.W0, receiverRegister, field.owner, field.name, descriptor)
            storeDouble(destination, Registers.W0)
        }
        "F" -> {
            emit.getField(Registers.I0, receiverRegister, field.owner, field.name, descriptor)
            storeFloat(destination, Registers.I0)
        }
        "I", "B", "C" -> {
            emit.getField(Registers.I0, receiverRegister, field.owner, field.name, descriptor)
            widenStorage(descriptor, Registers.I0, signed)
            storeInt(destination, Registers.I0)
        }
        else -> {
            emit.getField(Registers.SECOND_OBJECT, receiverRegister, field.owner, field.name, descriptor)
            storeReference(destination, Registers.SECOND_OBJECT)
        }
    }
}

private fun LowerContext.putFieldValue(
    receiverRegister: Int,
    field: GcField,
    value: Operand,
) {
    val register = loadStorage(field.descriptor, value, Registers.SECOND_OBJECT)
    emit.putField(register, receiverRegister, field.owner, field.name, field.descriptor)
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
