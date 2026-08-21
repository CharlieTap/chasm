package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.AggregateSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
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
        AggregateSuperInstruction.StructNewS(
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
    AggregateSuperInstruction.StructNewDefaultS(
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
        null -> AggregateSuperInstruction.StructGetS(addressSlot, destinationSlot, fieldIndex)
        true -> AggregateSuperInstruction.StructGetSignedS(
            addressSlot,
            destinationSlot,
            fieldIndex,
            checkNotNull(packedType),
        )
        false -> AggregateSuperInstruction.StructGetUnsignedS(
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
    AggregateSuperInstruction.RefCastStructGetS(
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
    AggregateSuperInstruction.StructGetStructGetS(
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
    AggregateSuperInstruction.LocalSetStructGetS(
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
        AggregateSuperInstruction.StructSetI(value.sourceBits, addressSlot, fieldIndex)
    } else {
        AggregateSuperInstruction.StructSetS(value.sourceSlot, addressSlot, fieldIndex)
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
            sizeImmediate && valueImmediate -> AggregateSuperInstruction.ArrayNewIi(size.i32Immediate, value.sourceBits, destinationSlot, rtt)
            sizeImmediate -> AggregateSuperInstruction.ArrayNewIs(size.i32Immediate, value.sourceSlot, destinationSlot, rtt)
            valueImmediate -> AggregateSuperInstruction.ArrayNewSi(size.sourceSlot, value.sourceBits, destinationSlot, rtt)
            else -> AggregateSuperInstruction.ArrayNewSs(size.sourceSlot, value.sourceSlot, destinationSlot, rtt)
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
        AggregateSuperInstruction.ArrayNewDefaultI(size.i32Immediate, destinationSlot, rtt, field)
    } else {
        AggregateSuperInstruction.ArrayNewDefaultS(size.sourceSlot, destinationSlot, rtt, field)
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
            sourceImmediate && lengthImmediate -> AggregateSuperInstruction.ArrayNewDataIi(sourceOffset.i32Immediate, length.i32Immediate, destinationSlot, rtt, data, fieldWidthInBytes)
            sourceImmediate -> AggregateSuperInstruction.ArrayNewDataIs(sourceOffset.i32Immediate, length.sourceSlot, destinationSlot, rtt, data, fieldWidthInBytes)
            lengthImmediate -> AggregateSuperInstruction.ArrayNewDataSi(sourceOffset.sourceSlot, length.i32Immediate, destinationSlot, rtt, data, fieldWidthInBytes)
            else -> AggregateSuperInstruction.ArrayNewDataSs(sourceOffset.sourceSlot, length.sourceSlot, destinationSlot, rtt, data, fieldWidthInBytes)
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
            sourceImmediate && lengthImmediate -> AggregateSuperInstruction.ArrayNewElementIi(sourceOffset.i32Immediate, length.i32Immediate, destinationSlot, rtt, element)
            sourceImmediate -> AggregateSuperInstruction.ArrayNewElementIs(sourceOffset.i32Immediate, length.sourceSlot, destinationSlot, rtt, element)
            lengthImmediate -> AggregateSuperInstruction.ArrayNewElementSi(sourceOffset.sourceSlot, length.i32Immediate, destinationSlot, rtt, element)
            else -> AggregateSuperInstruction.ArrayNewElementSs(sourceOffset.sourceSlot, length.sourceSlot, destinationSlot, rtt, element)
        },
    )
}

internal fun FunctionCompilationContext.emitArrayNewFixed(
    firstElementSlot: Int,
    length: Int,
    destinationSlot: Int,
    rtt: RTT,
) = emitAggregate(AggregateSuperInstruction.ArrayNewFixedS(firstElementSlot, destinationSlot, rtt, length))

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
            null -> if (immediate) AggregateSuperInstruction.ArrayGetI(addressSlot, field.i32Immediate, destinationSlot) else AggregateSuperInstruction.ArrayGetS(addressSlot, field.sourceSlot, destinationSlot)
            true -> if (immediate) AggregateSuperInstruction.ArrayGetSignedI(addressSlot, field.i32Immediate, destinationSlot, checkNotNull(packedType)) else AggregateSuperInstruction.ArrayGetSignedS(addressSlot, field.sourceSlot, destinationSlot, checkNotNull(packedType))
            false -> if (immediate) AggregateSuperInstruction.ArrayGetUnsignedI(addressSlot, field.i32Immediate, destinationSlot, checkNotNull(packedType)) else AggregateSuperInstruction.ArrayGetUnsignedS(addressSlot, field.sourceSlot, destinationSlot, checkNotNull(packedType))
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
            valueImmediate && fieldImmediate -> AggregateSuperInstruction.ArraySetIi(value.sourceBits, field.i32Immediate, addressSlot)
            valueImmediate -> AggregateSuperInstruction.ArraySetIs(value.sourceBits, field.sourceSlot, addressSlot)
            fieldImmediate -> AggregateSuperInstruction.ArraySetSi(value.sourceSlot, field.i32Immediate, addressSlot)
            else -> AggregateSuperInstruction.ArraySetSs(value.sourceSlot, field.sourceSlot, addressSlot)
        },
    )
}

internal fun FunctionCompilationContext.emitArrayLen(addressSlot: Int, destinationSlot: Int) =
    emitAggregate(AggregateSuperInstruction.ArrayLenS(addressSlot, destinationSlot))

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
            ai && bi && ci -> AggregateSuperInstruction.ArrayFillIii(a, b, c, addressSlot)
            ai && bi -> AggregateSuperInstruction.ArrayFillIis(a, b, c, addressSlot)
            ai && ci -> AggregateSuperInstruction.ArrayFillIsi(a, value.sourceSlot, c, addressSlot)
            ai -> AggregateSuperInstruction.ArrayFillIss(a, value.sourceSlot, c, addressSlot)
            bi && ci -> AggregateSuperInstruction.ArrayFillSii(a, b, c, addressSlot)
            bi -> AggregateSuperInstruction.ArrayFillSis(a, b, c, addressSlot)
            ci -> AggregateSuperInstruction.ArrayFillSsi(a, value.sourceSlot, c, addressSlot)
            else -> AggregateSuperInstruction.ArrayFillSss(a, value.sourceSlot, c, addressSlot)
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
    { a, b, c -> AggregateSuperInstruction.ArrayCopyIii(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopyIis(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopyIsi(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopyIss(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopySii(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopySis(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopySsi(a, b, c, sourceAddressSlot, destinationAddressSlot) },
    { a, b, c -> AggregateSuperInstruction.ArrayCopySss(a, b, c, sourceAddressSlot, destinationAddressSlot) },
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
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataIii(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataIis(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataIsi(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataIss(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataSii(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataSis(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataSsi(a, b, c, addressSlot, data, elementByteWidth) },
        { a, b, c -> AggregateSuperInstruction.ArrayInitDataSss(a, b, c, addressSlot, data, elementByteWidth) },
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
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementIii(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementIis(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementIsi(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementIss(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementSii(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementSis(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementSsi(a, b, c, addressSlot, element) },
    { a, b, c -> AggregateSuperInstruction.ArrayInitElementSss(a, b, c, addressSlot, element) },
)

internal fun FunctionCompilationContext.emitRefI31(value: OperandSource, destinationSlot: Int) = emitAggregate(
    if (value.sourceKind == OperandSourceKind.I32Immediate) {
        AggregateSuperInstruction.RefI31I(value.i32Immediate, destinationSlot)
    } else {
        AggregateSuperInstruction.RefI31S(value.sourceSlot, destinationSlot)
    },
)

internal fun FunctionCompilationContext.emitI31Get(signed: Boolean, valueSlot: Int, destinationSlot: Int) =
    emitAggregate(
        if (signed) {
            AggregateSuperInstruction.I31GetSignedS(valueSlot, destinationSlot)
        } else {
            AggregateSuperInstruction.I31GetUnsignedS(valueSlot, destinationSlot)
        },
    )

internal fun FunctionCompilationContext.emitAnyConvertExtern(valueSlot: Int, destinationSlot: Int) =
    emitAggregate(AggregateSuperInstruction.AnyConvertExternS(valueSlot, destinationSlot))

internal fun FunctionCompilationContext.emitExternConvertAny(valueSlot: Int, destinationSlot: Int) =
    emitAggregate(AggregateSuperInstruction.ExternConvertAnyS(valueSlot, destinationSlot))

private inline fun FunctionCompilationContext.emitAggregateTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> AggregateSuperInstruction,
    iis: (Int, Int, Int) -> AggregateSuperInstruction,
    isi: (Int, Int, Int) -> AggregateSuperInstruction,
    iss: (Int, Int, Int) -> AggregateSuperInstruction,
    sii: (Int, Int, Int) -> AggregateSuperInstruction,
    sis: (Int, Int, Int) -> AggregateSuperInstruction,
    ssi: (Int, Int, Int) -> AggregateSuperInstruction,
    sss: (Int, Int, Int) -> AggregateSuperInstruction,
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

private fun FunctionCompilationContext.emitAggregate(instruction: AggregateSuperInstruction) {
    emit(instruction, ::AggregateSuperInstructionDispatcher)
}
