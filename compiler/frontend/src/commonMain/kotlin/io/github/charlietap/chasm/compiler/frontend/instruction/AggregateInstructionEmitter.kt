package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.DataIndex
import io.github.charlietap.chasm.cir.ElementIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.ext.toCir
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.i32Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.isImmediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.cir.ReferenceType as CirReferenceType

internal fun FunctionCompilationContext.emitStructNew(
    firstFieldSlot: Int,
    destinationSlot: Int,
    type: TypeIndex,
) {
    emit(
        AggregateInstruction.StructNewS(
            firstFieldSlot = firstFieldSlot,
            destinationSlot = destinationSlot,
            type = type,
        ),
    )
}

internal fun FunctionCompilationContext.emitStructNewDefault(
    destinationSlot: Int,
    type: TypeIndex,
) = emit(AggregateInstruction.StructNewDefaultS(destinationSlot, type))

internal fun FunctionCompilationContext.emitStructGet(
    signed: Boolean?,
    packedType: PackedType?,
    addressSlot: Int,
    destinationSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
) = when (signed) {
    null -> emit(AggregateInstruction.StructGetS(addressSlot, destinationSlot, type, fieldIndex))
    true -> emit(
        AggregateInstruction.StructGetSignedS(
            addressSlot,
            destinationSlot,
            type,
            fieldIndex,
            checkNotNull(packedType).toCir(),
        ),
    )
    false -> emit(
        AggregateInstruction.StructGetUnsignedS(
            addressSlot,
            destinationSlot,
            type,
            fieldIndex,
            checkNotNull(packedType).toCir(),
        ),
    )
}

internal fun FunctionCompilationContext.emitRefCastStructGet(
    referenceSlot: Int,
    destinationSlot: Int,
    typeTest: CirReferenceType,
    type: TypeIndex,
    fieldIndex: Int,
) = emit(
    AggregateInstruction.RefCastStructGetS(
        referenceSlot = referenceSlot,
        destinationSlot = destinationSlot,
        typeTest = typeTest,
        type = type,
        fieldIndex = fieldIndex,
    ),
)

internal fun FunctionCompilationContext.emitStructGetStructGet(
    addressSlot: Int,
    destinationSlot: Int,
    firstType: TypeIndex,
    firstFieldIndex: Int,
    secondType: TypeIndex,
    secondFieldIndex: Int,
) = emit(
    AggregateInstruction.StructGetStructGetS(
        addressSlot = addressSlot,
        destinationSlot = destinationSlot,
        firstType = firstType,
        firstFieldIndex = firstFieldIndex,
        secondType = secondType,
        secondFieldIndex = secondFieldIndex,
    ),
)

internal fun FunctionCompilationContext.emitLocalSetStructGet(
    sourceSlot: Int,
    localSlot: Int,
    destinationSlot: Int,
    type: TypeIndex,
    fieldIndex: Int,
) = emit(
    AggregateInstruction.LocalSetStructGetS(
        sourceSlot = sourceSlot,
        localSlot = localSlot,
        destinationSlot = destinationSlot,
        type = type,
        fieldIndex = fieldIndex,
    ),
)

internal fun FunctionCompilationContext.emitStructSet(
    value: OperandSource,
    addressSlot: Int,
    fieldIndex: Int,
) = if (value.isImmediate) {
    emit(AggregateInstruction.StructSetI(value.sourceBits, addressSlot, fieldIndex))
} else {
    emit(AggregateInstruction.StructSetS(value.sourceSlot, addressSlot, fieldIndex))
}

internal fun FunctionCompilationContext.emitArrayNew(
    size: OperandSource,
    value: OperandSource,
    destinationSlot: Int,
    type: TypeIndex,
) {
    val sizeImmediate = size.sourceKind == OperandSourceKind.I32Immediate
    val valueImmediate = value.isImmediate
    when {
        sizeImmediate && valueImmediate -> emit(AggregateInstruction.ArrayNewIi(size.i32Immediate, value.sourceBits, destinationSlot, type))
        sizeImmediate -> emit(AggregateInstruction.ArrayNewIs(size.i32Immediate, value.sourceSlot, destinationSlot, type))
        valueImmediate -> emit(AggregateInstruction.ArrayNewSi(size.sourceSlot, value.sourceBits, destinationSlot, type))
        else -> emit(AggregateInstruction.ArrayNewSs(size.sourceSlot, value.sourceSlot, destinationSlot, type))
    }
}

internal fun FunctionCompilationContext.emitArrayNewDefault(
    size: OperandSource,
    destinationSlot: Int,
    type: TypeIndex,
) = if (size.sourceKind == OperandSourceKind.I32Immediate) {
    emit(AggregateInstruction.ArrayNewDefaultI(size.i32Immediate, destinationSlot, type))
} else {
    emit(AggregateInstruction.ArrayNewDefaultS(size.sourceSlot, destinationSlot, type))
}

internal fun FunctionCompilationContext.emitArrayNewData(
    sourceOffset: OperandSource,
    length: OperandSource,
    destinationSlot: Int,
    type: TypeIndex,
    data: DataIndex,
    fieldWidthInBytes: Int,
) {
    val sourceImmediate = sourceOffset.sourceKind == OperandSourceKind.I32Immediate
    val lengthImmediate = length.sourceKind == OperandSourceKind.I32Immediate
    when {
        sourceImmediate && lengthImmediate -> emit(
            AggregateInstruction.ArrayNewDataIi(
                sourceOffset.i32Immediate,
                length.i32Immediate,
                destinationSlot,
                type,
                data,
                fieldWidthInBytes,
            ),
        )
        sourceImmediate -> emit(
            AggregateInstruction.ArrayNewDataIs(
                sourceOffset.i32Immediate,
                length.sourceSlot,
                destinationSlot,
                type,
                data,
                fieldWidthInBytes,
            ),
        )
        lengthImmediate -> emit(
            AggregateInstruction.ArrayNewDataSi(
                sourceOffset.sourceSlot,
                length.i32Immediate,
                destinationSlot,
                type,
                data,
                fieldWidthInBytes,
            ),
        )
        else -> emit(
            AggregateInstruction.ArrayNewDataSs(
                sourceOffset.sourceSlot,
                length.sourceSlot,
                destinationSlot,
                type,
                data,
                fieldWidthInBytes,
            ),
        )
    }
}

internal fun FunctionCompilationContext.emitArrayNewElement(
    sourceOffset: OperandSource,
    length: OperandSource,
    destinationSlot: Int,
    type: TypeIndex,
    element: ElementIndex,
) {
    val sourceImmediate = sourceOffset.sourceKind == OperandSourceKind.I32Immediate
    val lengthImmediate = length.sourceKind == OperandSourceKind.I32Immediate
    when {
        sourceImmediate && lengthImmediate -> emit(
            AggregateInstruction.ArrayNewElementIi(
                sourceOffset.i32Immediate,
                length.i32Immediate,
                destinationSlot,
                type,
                element,
            ),
        )
        sourceImmediate -> emit(
            AggregateInstruction.ArrayNewElementIs(
                sourceOffset.i32Immediate,
                length.sourceSlot,
                destinationSlot,
                type,
                element,
            ),
        )
        lengthImmediate -> emit(
            AggregateInstruction.ArrayNewElementSi(
                sourceOffset.sourceSlot,
                length.i32Immediate,
                destinationSlot,
                type,
                element,
            ),
        )
        else -> emit(
            AggregateInstruction.ArrayNewElementSs(
                sourceOffset.sourceSlot,
                length.sourceSlot,
                destinationSlot,
                type,
                element,
            ),
        )
    }
}

internal fun FunctionCompilationContext.emitArrayNewFixed(
    firstElementSlot: Int,
    length: Int,
    destinationSlot: Int,
    type: TypeIndex,
) = emit(AggregateInstruction.ArrayNewFixedS(firstElementSlot, destinationSlot, type, length))

internal fun FunctionCompilationContext.emitArrayGet(
    signed: Boolean?,
    packedType: PackedType?,
    addressSlot: Int,
    field: OperandSource,
    destinationSlot: Int,
    type: TypeIndex,
) {
    val immediate = field.sourceKind == OperandSourceKind.I32Immediate
    when (signed) {
        null -> if (immediate) {
            emit(AggregateInstruction.ArrayGetI(addressSlot, field.i32Immediate, destinationSlot, type))
        } else {
            emit(AggregateInstruction.ArrayGetS(addressSlot, field.sourceSlot, destinationSlot, type))
        }
        true -> if (immediate) {
            emit(
                AggregateInstruction.ArrayGetSignedI(
                    addressSlot,
                    field.i32Immediate,
                    destinationSlot,
                    type,
                    checkNotNull(packedType).toCir(),
                ),
            )
        } else {
            emit(
                AggregateInstruction.ArrayGetSignedS(
                    addressSlot,
                    field.sourceSlot,
                    destinationSlot,
                    type,
                    checkNotNull(packedType).toCir(),
                ),
            )
        }
        false -> if (immediate) {
            emit(
                AggregateInstruction.ArrayGetUnsignedI(
                    addressSlot,
                    field.i32Immediate,
                    destinationSlot,
                    type,
                    checkNotNull(packedType).toCir(),
                ),
            )
        } else {
            emit(
                AggregateInstruction.ArrayGetUnsignedS(
                    addressSlot,
                    field.sourceSlot,
                    destinationSlot,
                    type,
                    checkNotNull(packedType).toCir(),
                ),
            )
        }
    }
}

internal fun FunctionCompilationContext.emitArraySet(
    value: OperandSource,
    field: OperandSource,
    addressSlot: Int,
) {
    val valueImmediate = value.isImmediate
    val fieldImmediate = field.sourceKind == OperandSourceKind.I32Immediate
    when {
        valueImmediate && fieldImmediate -> emit(AggregateInstruction.ArraySetIi(value.sourceBits, field.i32Immediate, addressSlot))
        valueImmediate -> emit(AggregateInstruction.ArraySetIs(value.sourceBits, field.sourceSlot, addressSlot))
        fieldImmediate -> emit(AggregateInstruction.ArraySetSi(value.sourceSlot, field.i32Immediate, addressSlot))
        else -> emit(AggregateInstruction.ArraySetSs(value.sourceSlot, field.sourceSlot, addressSlot))
    }
}

internal fun FunctionCompilationContext.emitArrayLen(
    addressSlot: Int,
    destinationSlot: Int,
) =
    emit(AggregateInstruction.ArrayLenS(addressSlot, destinationSlot))

internal fun FunctionCompilationContext.emitArrayFill(
    elements: OperandSource,
    value: OperandSource,
    offset: OperandSource,
    addressSlot: Int,
) {
    val ai = elements.sourceKind == OperandSourceKind.I32Immediate
    val bi = value.isImmediate
    val ci = offset.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) elements.i32Immediate else elements.sourceSlot
    val b = value.sourceBits
    val c = if (ci) offset.i32Immediate else offset.sourceSlot
    when {
        ai && bi && ci -> emit(AggregateInstruction.ArrayFillIii(a, b, c, addressSlot))
        ai && bi -> emit(AggregateInstruction.ArrayFillIis(a, b, c, addressSlot))
        ai && ci -> emit(AggregateInstruction.ArrayFillIsi(a, value.sourceSlot, c, addressSlot))
        ai -> emit(AggregateInstruction.ArrayFillIss(a, value.sourceSlot, c, addressSlot))
        bi && ci -> emit(AggregateInstruction.ArrayFillSii(a, b, c, addressSlot))
        bi -> emit(AggregateInstruction.ArrayFillSis(a, b, c, addressSlot))
        ci -> emit(AggregateInstruction.ArrayFillSsi(a, value.sourceSlot, c, addressSlot))
        else -> emit(AggregateInstruction.ArrayFillSss(a, value.sourceSlot, c, addressSlot))
    }
}

internal fun FunctionCompilationContext.emitArrayCopy(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = emitAggregateTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> emit(AggregateInstruction.ArrayCopyIii(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopyIis(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopyIsi(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopyIss(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopySii(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopySis(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopySsi(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
    { a, b, c -> emit(AggregateInstruction.ArrayCopySss(a, b, c, sourceAddressSlot, destinationAddressSlot)) },
)

internal fun FunctionCompilationContext.emitArrayInitData(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    addressSlot: Int,
    data: DataIndex,
    elementByteWidth: Int,
) {
    emitAggregateTernary(
        elements,
        sourceOffset,
        destinationOffset,
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataIii(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataIis(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataIsi(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataIss(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataSii(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataSis(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataSsi(a, b, c, addressSlot, data, elementByteWidth)) },
        { a, b, c -> emit(AggregateInstruction.ArrayInitDataSss(a, b, c, addressSlot, data, elementByteWidth)) },
    )
}

internal fun FunctionCompilationContext.emitArrayInitElement(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    addressSlot: Int,
    element: ElementIndex,
) = emitAggregateTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementIii(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementIis(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementIsi(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementIss(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementSii(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementSis(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementSsi(a, b, c, addressSlot, element)) },
    { a, b, c -> emit(AggregateInstruction.ArrayInitElementSss(a, b, c, addressSlot, element)) },
)

internal fun FunctionCompilationContext.emitRefI31(
    value: OperandSource,
    destinationSlot: Int,
) = if (value.sourceKind == OperandSourceKind.I32Immediate) {
    emit(AggregateInstruction.RefI31I(value.i32Immediate, destinationSlot))
} else {
    emit(AggregateInstruction.RefI31S(value.sourceSlot, destinationSlot))
}

internal fun FunctionCompilationContext.emitI31Get(
    signed: Boolean,
    valueSlot: Int,
    destinationSlot: Int,
) =
    if (signed) {
        emit(AggregateInstruction.I31GetSignedS(valueSlot, destinationSlot))
    } else {
        emit(AggregateInstruction.I31GetUnsignedS(valueSlot, destinationSlot))
    }

internal fun FunctionCompilationContext.emitAnyConvertExtern(
    valueSlot: Int,
    destinationSlot: Int,
) =
    emit(AggregateInstruction.AnyConvertExternS(valueSlot, destinationSlot))

internal fun FunctionCompilationContext.emitExternConvertAny(
    valueSlot: Int,
    destinationSlot: Int,
) =
    emit(AggregateInstruction.ExternConvertAnyS(valueSlot, destinationSlot))

private inline fun FunctionCompilationContext.emitAggregateTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> Unit,
    iis: (Int, Int, Int) -> Unit,
    isi: (Int, Int, Int) -> Unit,
    iss: (Int, Int, Int) -> Unit,
    sii: (Int, Int, Int) -> Unit,
    sis: (Int, Int, Int) -> Unit,
    ssi: (Int, Int, Int) -> Unit,
    sss: (Int, Int, Int) -> Unit,
) {
    val ai = first.sourceKind == OperandSourceKind.I32Immediate
    val bi = second.sourceKind == OperandSourceKind.I32Immediate
    val ci = third.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) first.i32Immediate else first.sourceSlot
    val b = if (bi) second.i32Immediate else second.sourceSlot
    val c = if (ci) third.i32Immediate else third.sourceSlot
    when {
        ai && bi && ci -> iii(a, b, c)
        ai && bi -> iis(a, b, c)
        ai && ci -> isi(a, b, c)
        ai -> iss(a, b, c)
        bi && ci -> sii(a, b, c)
        bi -> sis(a, b, c)
        ci -> ssi(a, b, c)
        else -> sss(a, b, c)
    }
}
