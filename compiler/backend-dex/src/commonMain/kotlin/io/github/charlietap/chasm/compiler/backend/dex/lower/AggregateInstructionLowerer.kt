package io.github.charlietap.chasm.compiler.backend.dex.lower

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
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TYPE_TEST
import io.github.charlietap.chasm.compiler.backend.abi.REF_CAST_STRUCT_GET
import io.github.charlietap.chasm.compiler.backend.abi.REF_I31
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_GET
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_GET_STRUCT_GET
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_NEW
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_NEW_DEFAULT
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_SET
import io.github.charlietap.chasm.compiler.backend.analysis.form.ArrayCopy
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.arrayCopy
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal fun lowerAggregateInstruction(
    instruction: AggregateInstruction,
    context: LowerContext,
) {
    val copy = instruction.arrayCopy()
    if (copy != null) return context.arrayCopy(copy)
    when (val i = instruction) {
        is AggregateInstruction.StructGetS -> context.structGet(i.addressSlot, i.fieldIndex, i.destinationSlot)
        is AggregateInstruction.StructGetSignedS -> context.structGet(i.addressSlot, i.fieldIndex, i.destinationSlot, i.packedType, signed = true)
        is AggregateInstruction.StructGetUnsignedS -> context.structGet(i.addressSlot, i.fieldIndex, i.destinationSlot, i.packedType, signed = false)
        is AggregateInstruction.StructSetI -> context.structSet(i.addressSlot, i.fieldIndex, immediate(i.value))
        is AggregateInstruction.StructSetS -> context.structSet(i.addressSlot, i.fieldIndex, slot(i.valueSlot))
        is AggregateInstruction.StructGetStructGetS -> context.structGetStructGet(i)
        is AggregateInstruction.RefCastStructGetS -> {
            context.loadObject(Registers.OBJECT, Resource.TypeTest(i.typeTest), REFERENCE_TYPE_TEST)
            context.loadWide(Registers.W0, i.referenceSlot)
            context.emit.const(Registers.I0, i.fieldIndex)
            context.emit.invoke(REF_CAST_STRUCT_GET, Registers.CONTEXT, Registers.W0, Registers.W0 + 1, Registers.OBJECT, Registers.I0)
            context.emit.moveResultWide(Registers.W0)
            context.store(i.destinationSlot, Registers.W0)
        }
        is AggregateInstruction.LocalSetStructGetS -> {
            context.loadWide(Registers.W0, i.sourceSlot)
            context.store(i.localSlot, Registers.W0)
            context.structGet(i.sourceSlot, i.fieldIndex, i.destinationSlot)
        }
        is AggregateInstruction.StructNewS -> context.structNew(i.type, i.firstFieldSlot, i.destinationSlot)
        is AggregateInstruction.StructNewDefaultS -> {
            context.loadValueInt(Registers.I3, Resource.RuntimeType(i.type.index))
            context.loadObject(Registers.W1, context.declarations.structDefaults(i.type), "[J")
            context.emit.invoke(STRUCT_NEW_DEFAULT, Registers.CONTEXT, Registers.I3, Registers.W1)
            context.emit.moveResultWide(Registers.W0)
            context.store(i.destinationSlot, Registers.W0)
        }
        is AggregateInstruction.ArrayGetI -> context.arrayGet(i.addressSlot, immediate(i.field), i.destinationSlot)
        is AggregateInstruction.ArrayGetS -> context.arrayGet(i.addressSlot, slot(i.fieldSlot), i.destinationSlot)
        is AggregateInstruction.ArrayGetSignedI -> context.arrayGet(i.addressSlot, immediate(i.field), i.destinationSlot, i.packedType, signed = true)
        is AggregateInstruction.ArrayGetSignedS -> context.arrayGet(i.addressSlot, slot(i.fieldSlot), i.destinationSlot, i.packedType, signed = true)
        is AggregateInstruction.ArrayGetUnsignedI -> context.arrayGet(i.addressSlot, immediate(i.field), i.destinationSlot, i.packedType, signed = false)
        is AggregateInstruction.ArrayGetUnsignedS -> context.arrayGet(i.addressSlot, slot(i.fieldSlot), i.destinationSlot, i.packedType, signed = false)
        is AggregateInstruction.ArraySetIi -> context.arraySet(i.addressSlot, immediate(i.field), immediate(i.value))
        is AggregateInstruction.ArraySetIs -> context.arraySet(i.addressSlot, slot(i.fieldSlot), immediate(i.value))
        is AggregateInstruction.ArraySetSi -> context.arraySet(i.addressSlot, immediate(i.field), slot(i.valueSlot))
        is AggregateInstruction.ArraySetSs -> context.arraySet(i.addressSlot, slot(i.fieldSlot), slot(i.valueSlot))
        is AggregateInstruction.ArrayLenS -> context.arrayLen(i.addressSlot, i.destinationSlot)
        is AggregateInstruction.ArrayNewDefaultI -> context.arrayNewDefault(i.type, immediate(i.size), i.destinationSlot)
        is AggregateInstruction.ArrayNewDefaultS -> context.arrayNewDefault(i.type, slot(i.sizeSlot), i.destinationSlot)
        is AggregateInstruction.ArrayNewFixedS -> context.arrayNewFixed(i.type, i.firstElementSlot, i.size, i.destinationSlot)
        is AggregateInstruction.RefI31I -> context.refI31(immediate(i.value), i.destinationSlot)
        is AggregateInstruction.RefI31S -> context.refI31(slot(i.valueSlot), i.destinationSlot)
        is AggregateInstruction.I31GetSignedS -> context.slotHelper(I31_GET_SIGNED, i.valueSlot, i.destinationSlot)
        is AggregateInstruction.I31GetUnsignedS -> context.slotHelper(I31_GET_UNSIGNED, i.valueSlot, i.destinationSlot)
        else -> {
            val call = i.helperCall() ?: throw Unsupported(i::class.simpleName ?: "aggregate instruction")
            context.lowerHelperCall(call)
        }
    }
}

private fun LowerContext.structGet(
    addressSlot: Int,
    field: Int,
    destination: Int,
    packed: PackedType? = null,
    signed: Boolean = false,
) {
    loadWide(Registers.W0, addressSlot)
    emit.const(Registers.I0, field)
    emit.invoke(STRUCT_GET, Registers.CONTEXT, Registers.W0, Registers.W0 + 1, Registers.I0)
    emit.moveResultWide(Registers.W0)
    if (packed != null) unpack(packed, signed)
    store(destination, Registers.W0)
}

private fun LowerContext.structGetStructGet(
    instruction: AggregateInstruction.StructGetStructGetS,
) {
    loadWide(Registers.W0, instruction.addressSlot)
    emit.const(Registers.I0, instruction.firstFieldIndex)
    emit.const(Registers.I1, instruction.secondFieldIndex)
    emit.invoke(STRUCT_GET_STRUCT_GET, Registers.CONTEXT, Registers.W0, Registers.W0 + 1, Registers.I0, Registers.I1)
    emit.moveResultWide(Registers.W0)
    store(instruction.destinationSlot, Registers.W0)
}

private fun LowerContext.structSet(
    addressSlot: Int,
    field: Int,
    value: Operand,
) {
    heapStore(STRUCT_SET, addressSlot, immediate(field), value)
}

private fun LowerContext.structNew(
    type: TypeIndex,
    firstFieldSlot: Int,
    destination: Int,
) {
    val fields = declarations.structFields(type).size
    loadValueInt(Registers.I3, Resource.RuntimeType(type.index))
    loadCallBuffer()
    for (field in 0 until fields) {
        loadWide(Registers.W0, firstFieldSlot + field)
        emit.const(Registers.I2, field)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    emit.invoke(STRUCT_NEW, Registers.CONTEXT, Registers.I3, Registers.OBJECT)
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}

private fun LowerContext.arrayGet(
    addressSlot: Int,
    index: Operand,
    destination: Int,
    packed: PackedType? = null,
    signed: Boolean = false,
) {
    loadWide(Registers.W0, addressSlot)
    loadInt(Registers.I2, Registers.W1, index)
    emit.invoke(ARRAY_GET, Registers.CONTEXT, Registers.W0, Registers.W0 + 1, Registers.I2)
    emit.moveResultWide(Registers.W0)
    if (packed != null) unpack(packed, signed)
    store(destination, Registers.W0)
}

private fun LowerContext.arraySet(
    addressSlot: Int,
    index: Operand,
    value: Operand,
) {
    heapStore(ARRAY_SET, addressSlot, index, value)
}

private fun LowerContext.arrayLen(
    addressSlot: Int,
    destination: Int,
) {
    loadWide(Registers.W0, addressSlot)
    emit.invoke(ARRAY_LEN, Registers.CONTEXT, Registers.W0, Registers.W0 + 1)
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}

private fun LowerContext.arrayNewDefault(
    type: TypeIndex,
    size: Operand,
    destination: Int,
) {
    loadValueInt(Registers.I3, Resource.RuntimeType(type.index))
    loadInt(Registers.I0, Registers.W0, size)
    emit.constWide(Registers.W1, declarations.arrayDefault(type))
    emit.invoke(ARRAY_NEW_DEFAULT, Registers.CONTEXT, Registers.I3, Registers.I0, Registers.W1, Registers.W1 + 1)
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}

private fun LowerContext.arrayNewFixed(
    type: TypeIndex,
    firstElementSlot: Int,
    size: Int,
    destination: Int,
) {
    loadCallBuffer()
    for (element in 0 until size) {
        loadWide(Registers.W0, firstElementSlot + element)
        emit.const(Registers.I2, element)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    val window = registers.window + 1
    emit.moveObject(window + 2, Registers.OBJECT)
    emit.moveObject(window, Registers.CONTEXT)
    loadValueInt(Registers.I0, Resource.RuntimeType(type.index))
    emit.move(window + 1, Registers.I0)
    emit.const(Registers.I0, size)
    emit.move(window + 3, Registers.I0)
    emit.invokeRange(ARRAY_NEW_FIXED, window, ARRAY_NEW_FIXED.words)
    emit.moveResultWide(Registers.W0)
    restoreWindow()
    store(destination, Registers.W0)
}

private fun LowerContext.arrayCopy(
    copy: ArrayCopy,
) {
    val window = registers.window + 1
    emit.moveObject(window, Registers.CONTEXT)
    loadWide(Registers.W0, copy.sourceSlot)
    emit.moveWide(window + 1, Registers.W0)
    loadInt(Registers.I0, Registers.W1, copy.sourceOffset)
    emit.move(window + 3, Registers.I0)
    loadWide(Registers.W0, copy.destinationSlot)
    emit.moveWide(window + 4, Registers.W0)
    loadInt(Registers.I0, Registers.W1, copy.destinationOffset)
    emit.move(window + 6, Registers.I0)
    loadInt(Registers.I0, Registers.W1, copy.count)
    emit.move(window + 7, Registers.I0)
    emit.invokeRange(ARRAY_COPY, window, ARRAY_COPY.words)
    restoreWindow()
}

private fun LowerContext.heapStore(
    method: MethodRef,
    addressSlot: Int,
    index: Operand,
    value: Operand,
) {
    val window = registers.window + 1
    emit.moveObject(window, Registers.CONTEXT)
    loadWide(Registers.W0, addressSlot)
    emit.moveWide(window + 1, Registers.W0)
    loadInt(Registers.I0, Registers.W1, index)
    emit.move(window + 3, Registers.I0)
    loadWide(Registers.W0, value)
    emit.moveWide(window + 4, Registers.W0)
    emit.invokeRange(method, window, method.words)
    restoreWindow()
}

private fun LowerContext.refI31(
    value: Operand,
    destination: Int,
) {
    loadInt(Registers.I0, Registers.W0, value)
    emit.invoke(REF_I31, Registers.I0)
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}

private fun LowerContext.unpack(
    packed: PackedType,
    signed: Boolean,
) {
    emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
    when (packed) {
        PackedType.I8 -> if (signed) {
            emit.convert(Conversion.IntToByte, Registers.I0, Registers.I0)
        } else {
            emit.literal(LiteralOp.AndInt, Registers.I0, Registers.I0, 0xFF)
        }
        PackedType.I16 -> if (signed) {
            emit.convert(Conversion.IntToShort, Registers.I0, Registers.I0)
        } else {
            emit.convert(Conversion.IntToChar, Registers.I0, Registers.I0)
        }
    }
    emit.convert(Conversion.IntToLong, Registers.W0, Registers.I0)
}
