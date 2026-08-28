package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.AggregateInstructionDispatcher
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StructType

internal fun FunctionCompilationContext.emitStructNew(
    firstFieldSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
) {
    emitAggregate(
        AggregateInstruction.StructNewS(
            firstFieldSlot = firstFieldSlot,
            destinationSlot = destinationSlot,
            rtt = rtt,
        ),
    )
}

internal fun FunctionCompilationContext.emitStructNewDefault(
    destinationSlot: Int,
    rtt: RTT,
    type: StructType,
) = emitAggregate(
    AggregateInstruction.StructNewDefaultS(
        destinationSlot,
        rtt,
        LongArray(type.fields.size) { index -> type.fields[index].default() },
    ),
)

internal fun FunctionCompilationContext.emitStructGet(
    signed: Boolean?,
    packedType: PackedType?,
    addressSlot: Int,
    destinationSlot: Int,
    fieldIndex: Int,
) = emitAggregate(
    when (signed) {
        null -> AggregateInstruction.StructGetS(addressSlot, destinationSlot, fieldIndex)
        true -> AggregateInstruction.StructGetSignedS(
            addressSlot,
            destinationSlot,
            fieldIndex,
            checkNotNull(packedType),
        )
        false -> AggregateInstruction.StructGetUnsignedS(
            addressSlot,
            destinationSlot,
            fieldIndex,
            checkNotNull(packedType),
        )
    },
)

internal fun FunctionCompilationContext.emitRefCastStructGet(
    referenceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
    fieldIndex: Int,
) = emitAggregate(
    AggregateInstruction.RefCastStructGetS(
        referenceSlot = referenceSlot,
        destinationSlot = destinationSlot,
        typeTest = typeTest,
        fieldIndex = fieldIndex,
    ),
)

internal fun FunctionCompilationContext.emitStructGetStructGet(
    addressSlot: Int,
    destinationSlot: Int,
    firstFieldIndex: Int,
    secondFieldIndex: Int,
) = emitAggregate(
    AggregateInstruction.StructGetStructGetS(
        addressSlot = addressSlot,
        destinationSlot = destinationSlot,
        firstFieldIndex = firstFieldIndex,
        secondFieldIndex = secondFieldIndex,
    ),
)

internal fun FunctionCompilationContext.emitLocalSetStructGet(
    sourceSlot: Int,
    localSlot: Int,
    destinationSlot: Int,
    fieldIndex: Int,
) = emitAggregate(
    AggregateInstruction.LocalSetStructGetS(
        sourceSlot = sourceSlot,
        localSlot = localSlot,
        destinationSlot = destinationSlot,
        fieldIndex = fieldIndex,
    ),
)

internal fun FunctionCompilationContext.emitStructSet(
    value: OperandSource,
    addressSlot: Int,
    fieldIndex: Int,
) = emitAggregate(
    if (value.isImmediate) {
        AggregateInstruction.StructSetI(value.sourceBits, addressSlot, fieldIndex)
    } else {
        AggregateInstruction.StructSetS(value.sourceSlot, addressSlot, fieldIndex)
    },
)

internal fun FunctionCompilationContext.emitArrayNew(
    size: OperandSource,
    value: OperandSource,
    destinationSlot: Int,
    rtt: RTT,
) {
    val sizeImmediate = size.sourceKind == OperandSourceKind.I32Immediate
    val valueImmediate = value.isImmediate
    emitAggregate(
        when {
            sizeImmediate && valueImmediate -> AggregateInstruction.ArrayNewIi(size.i32Immediate, value.sourceBits, destinationSlot, rtt)
            sizeImmediate -> AggregateInstruction.ArrayNewIs(size.i32Immediate, value.sourceSlot, destinationSlot, rtt)
            valueImmediate -> AggregateInstruction.ArrayNewSi(size.sourceSlot, value.sourceBits, destinationSlot, rtt)
            else -> AggregateInstruction.ArrayNewSs(size.sourceSlot, value.sourceSlot, destinationSlot, rtt)
        },
    )
}

internal fun FunctionCompilationContext.emitArrayNewDefault(
    size: OperandSource,
    destinationSlot: Int,
    rtt: RTT,
    field: Long,
) = emitAggregate(
    if (size.sourceKind == OperandSourceKind.I32Immediate) {
        AggregateInstruction.ArrayNewDefaultI(size.i32Immediate, destinationSlot, rtt, field)
    } else {
        AggregateInstruction.ArrayNewDefaultS(size.sourceSlot, destinationSlot, rtt, field)
    },
)

internal fun FunctionCompilationContext.emitArrayNewData(
    sourceOffset: OperandSource,
    length: OperandSource,
    destinationSlot: Int,
    rtt: RTT,
    data: DataInstance,
    fieldWidthInBytes: Int,
) {
    val sourceImmediate = sourceOffset.sourceKind == OperandSourceKind.I32Immediate
    val lengthImmediate = length.sourceKind == OperandSourceKind.I32Immediate
    emitAggregate(
        when {
            sourceImmediate && lengthImmediate -> AggregateInstruction.ArrayNewDataIi(
                sourceOffset.i32Immediate,
                length.i32Immediate,
                destinationSlot,
                rtt,
                data,
                fieldWidthInBytes,
            )
            sourceImmediate -> AggregateInstruction.ArrayNewDataIs(
                sourceOffset.i32Immediate,
                length.sourceSlot,
                destinationSlot,
                rtt,
                data,
                fieldWidthInBytes,
            )
            lengthImmediate -> AggregateInstruction.ArrayNewDataSi(
                sourceOffset.sourceSlot,
                length.i32Immediate,
                destinationSlot,
                rtt,
                data,
                fieldWidthInBytes,
            )
            else -> AggregateInstruction.ArrayNewDataSs(
                sourceOffset.sourceSlot,
                length.sourceSlot,
                destinationSlot,
                rtt,
                data,
                fieldWidthInBytes,
            )
        },
    )
}

internal fun FunctionCompilationContext.emitArrayNewElement(
    sourceOffset: OperandSource,
    length: OperandSource,
    destinationSlot: Int,
    rtt: RTT,
    element: ElementInstance,
) {
    val sourceImmediate = sourceOffset.sourceKind == OperandSourceKind.I32Immediate
    val lengthImmediate = length.sourceKind == OperandSourceKind.I32Immediate
    emitAggregate(
        when {
            sourceImmediate && lengthImmediate -> AggregateInstruction.ArrayNewElementIi(
                sourceOffset.i32Immediate,
                length.i32Immediate,
                destinationSlot,
                rtt,
                element,
            )
            sourceImmediate -> AggregateInstruction.ArrayNewElementIs(
                sourceOffset.i32Immediate,
                length.sourceSlot,
                destinationSlot,
                rtt,
                element,
            )
            lengthImmediate -> AggregateInstruction.ArrayNewElementSi(
                sourceOffset.sourceSlot,
                length.i32Immediate,
                destinationSlot,
                rtt,
                element,
            )
            else -> AggregateInstruction.ArrayNewElementSs(
                sourceOffset.sourceSlot,
                length.sourceSlot,
                destinationSlot,
                rtt,
                element,
            )
        },
    )
}

internal fun FunctionCompilationContext.emitArrayNewFixed(
    firstElementSlot: Int,
    length: Int,
    destinationSlot: Int,
    rtt: RTT,
) = emitAggregate(AggregateInstruction.ArrayNewFixedS(firstElementSlot, destinationSlot, rtt, length))

internal fun FunctionCompilationContext.emitArrayGet(
    signed: Boolean?,
    packedType: PackedType?,
    addressSlot: Int,
    field: OperandSource,
    destinationSlot: Int,
) {
    val immediate = field.sourceKind == OperandSourceKind.I32Immediate
    emitAggregate(
        when (signed) {
            null -> if (immediate) {
                AggregateInstruction.ArrayGetI(addressSlot, field.i32Immediate, destinationSlot)
            } else {
                AggregateInstruction.ArrayGetS(addressSlot, field.sourceSlot, destinationSlot)
            }
            true -> if (immediate) {
                AggregateInstruction.ArrayGetSignedI(
                    addressSlot,
                    field.i32Immediate,
                    destinationSlot,
                    checkNotNull(packedType),
                )
            } else {
                AggregateInstruction.ArrayGetSignedS(
                    addressSlot,
                    field.sourceSlot,
                    destinationSlot,
                    checkNotNull(packedType),
                )
            }
            false -> if (immediate) {
                AggregateInstruction.ArrayGetUnsignedI(
                    addressSlot,
                    field.i32Immediate,
                    destinationSlot,
                    checkNotNull(packedType),
                )
            } else {
                AggregateInstruction.ArrayGetUnsignedS(
                    addressSlot,
                    field.sourceSlot,
                    destinationSlot,
                    checkNotNull(packedType),
                )
            }
        },
    )
}

internal fun FunctionCompilationContext.emitArraySet(
    value: OperandSource,
    field: OperandSource,
    addressSlot: Int,
) {
    val valueImmediate = value.isImmediate
    val fieldImmediate = field.sourceKind == OperandSourceKind.I32Immediate
    emitAggregate(
        when {
            valueImmediate && fieldImmediate -> AggregateInstruction.ArraySetIi(value.sourceBits, field.i32Immediate, addressSlot)
            valueImmediate -> AggregateInstruction.ArraySetIs(value.sourceBits, field.sourceSlot, addressSlot)
            fieldImmediate -> AggregateInstruction.ArraySetSi(value.sourceSlot, field.i32Immediate, addressSlot)
            else -> AggregateInstruction.ArraySetSs(value.sourceSlot, field.sourceSlot, addressSlot)
        },
    )
}

internal fun FunctionCompilationContext.emitArrayLen(addressSlot: Int, destinationSlot: Int) =
    emitAggregate(AggregateInstruction.ArrayLenS(addressSlot, destinationSlot))

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
    emitAggregate(
        when {
            ai && bi && ci -> AggregateInstruction.ArrayFillIii(a, b, c, addressSlot)
            ai && bi -> AggregateInstruction.ArrayFillIis(a, b, c, addressSlot)
            ai && ci -> AggregateInstruction.ArrayFillIsi(a, value.sourceSlot, c, addressSlot)
            ai -> AggregateInstruction.ArrayFillIss(a, value.sourceSlot, c, addressSlot)
            bi && ci -> AggregateInstruction.ArrayFillSii(a, b, c, addressSlot)
            bi -> AggregateInstruction.ArrayFillSis(a, b, c, addressSlot)
            ci -> AggregateInstruction.ArrayFillSsi(a, value.sourceSlot, c, addressSlot)
            else -> AggregateInstruction.ArrayFillSss(a, value.sourceSlot, c, addressSlot)
        },
    )
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
    { a, b, c -> AggregateInstruction.ArrayCopyIii(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopyIis(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopyIsi(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopyIss(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopySii(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopySis(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopySsi(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateInstruction.ArrayCopySss(a, b, c, sourceAddressSlot, destinationAddressSlot) },
)

internal fun FunctionCompilationContext.emitArrayInitData(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    addressSlot: Int,
    data: DataInstance,
    elementByteWidth: Int,
) {
    emitAggregateTernary(
        elements,
        sourceOffset,
        destinationOffset,
        { a, b, c -> AggregateInstruction.ArrayInitDataIii(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataIis(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataIsi(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataIss(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataSii(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataSis(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataSsi(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateInstruction.ArrayInitDataSss(a, b, c, addressSlot, data, elementByteWidth) },
    )
}

internal fun FunctionCompilationContext.emitArrayInitElement(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    addressSlot: Int,
    element: ElementInstance,
) = emitAggregateTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> AggregateInstruction.ArrayInitElementIii(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementIis(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementIsi(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementIss(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementSii(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementSis(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementSsi(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateInstruction.ArrayInitElementSss(a, b, c, addressSlot, element) },
)

internal fun FunctionCompilationContext.emitRefI31(value: OperandSource, destinationSlot: Int) = emitAggregate(
    if (value.sourceKind == OperandSourceKind.I32Immediate) {
        AggregateInstruction.RefI31I(value.i32Immediate, destinationSlot)
    } else {
        AggregateInstruction.RefI31S(value.sourceSlot, destinationSlot)
    },
)

internal fun FunctionCompilationContext.emitI31Get(signed: Boolean, valueSlot: Int, destinationSlot: Int) =
    emitAggregate(
        if (signed) {
            AggregateInstruction.I31GetSignedS(valueSlot, destinationSlot)
        } else {
            AggregateInstruction.I31GetUnsignedS(valueSlot, destinationSlot)
        },
    )

internal fun FunctionCompilationContext.emitAnyConvertExtern(valueSlot: Int, destinationSlot: Int) =
    emitAggregate(AggregateInstruction.AnyConvertExternS(valueSlot, destinationSlot))

internal fun FunctionCompilationContext.emitExternConvertAny(valueSlot: Int, destinationSlot: Int) =
    emitAggregate(AggregateInstruction.ExternConvertAnyS(valueSlot, destinationSlot))

private inline fun FunctionCompilationContext.emitAggregateTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> AggregateInstruction,
    iis: (Int, Int, Int) -> AggregateInstruction,
    isi: (Int, Int, Int) -> AggregateInstruction,
    iss: (Int, Int, Int) -> AggregateInstruction,
    sii: (Int, Int, Int) -> AggregateInstruction,
    sis: (Int, Int, Int) -> AggregateInstruction,
    ssi: (Int, Int, Int) -> AggregateInstruction,
    sss: (Int, Int, Int) -> AggregateInstruction,
) {
    val ai = first.sourceKind == OperandSourceKind.I32Immediate
    val bi = second.sourceKind == OperandSourceKind.I32Immediate
    val ci = third.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) first.i32Immediate else first.sourceSlot
    val b = if (bi) second.i32Immediate else second.sourceSlot
    val c = if (ci) third.i32Immediate else third.sourceSlot
    emitAggregate(
        when {
            ai && bi && ci -> iii(a, b, c)
            ai && bi -> iis(a, b, c)
            ai && ci -> isi(a, b, c)
            ai -> iss(a, b, c)
            bi && ci -> sii(a, b, c)
            bi -> sis(a, b, c)
            ci -> ssi(a, b, c)
            else -> sss(a, b, c)
        },
    )
}

private fun FunctionCompilationContext.emitAggregate(instruction: AggregateInstruction) {
    emit(instruction, ::AggregateInstructionDispatcher)
}
