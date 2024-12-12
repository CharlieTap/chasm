package io.github.charlietap.chasm.fixture.ast.instruction

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.dataIndex
import io.github.charlietap.chasm.fixture.ast.module.elementIndex
import io.github.charlietap.chasm.fixture.ast.module.fieldIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex

fun aggregateInstruction(): AggregateInstruction = structNewInstruction()

fun structNewInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.StructNew(
    typeIndex = typeIndex,
)

fun structNewDefaultInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.StructNewDefault(
    typeIndex = typeIndex,
)

fun structGetInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    fieldIndex: Index.FieldIndex = fieldIndex(),
) = AggregateInstruction.StructGet(
    typeIndex = typeIndex,
    fieldIndex = fieldIndex,
)

fun structGetSignedInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    fieldIndex: Index.FieldIndex = fieldIndex(),
) = AggregateInstruction.StructGetSigned(
    typeIndex = typeIndex,
    fieldIndex = fieldIndex,
)

fun structGetUnsignedInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    fieldIndex: Index.FieldIndex = fieldIndex(),
) = AggregateInstruction.StructGetUnsigned(
    typeIndex = typeIndex,
    fieldIndex = fieldIndex,
)

fun structSetInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    fieldIndex: Index.FieldIndex = fieldIndex(),
) = AggregateInstruction.StructSet(
    typeIndex = typeIndex,
    fieldIndex = fieldIndex,
)

fun arrayNewInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayNew(
    typeIndex = typeIndex,
)

fun arrayNewFixedInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    size: UInt = 0u,
) = AggregateInstruction.ArrayNewFixed(
    typeIndex = typeIndex,
    size = size,
)

fun arrayNewDefaultInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayNewDefault(
    typeIndex = typeIndex,
)

fun arrayNewDataInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    dataIndex: Index.DataIndex = dataIndex(),
) = AggregateInstruction.ArrayNewData(
    typeIndex = typeIndex,
    dataIndex = dataIndex,
)

fun arrayNewElementInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    elementIndex: Index.ElementIndex = elementIndex(),
) = AggregateInstruction.ArrayNewElement(
    typeIndex = typeIndex,
    elementIndex = elementIndex,
)

fun arrayGetInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayGet(
    typeIndex = typeIndex,
)

fun arrayGetSignedInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayGetSigned(
    typeIndex = typeIndex,
)

fun arrayGetUnsignedInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayGetUnsigned(
    typeIndex = typeIndex,
)

fun arraySetInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArraySet(
    typeIndex = typeIndex,
)

fun arrayLenInstruction() = AggregateInstruction.ArrayLen

fun arrayFillInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayFill(
    typeIndex = typeIndex,
)

fun arrayCopyInstruction(
    sourceTypeIndex: Index.TypeIndex = typeIndex(),
    destinationTypeIndex: Index.TypeIndex = typeIndex(),
) = AggregateInstruction.ArrayCopy(
    sourceTypeIndex = sourceTypeIndex,
    destinationTypeIndex = destinationTypeIndex,
)

fun arrayInitDataInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    dataIndex: Index.DataIndex = dataIndex(),
) = AggregateInstruction.ArrayInitData(
    typeIndex = typeIndex,
    dataIndex = dataIndex,
)

fun arrayInitElementInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    elementIndex: Index.ElementIndex = elementIndex(),
) = AggregateInstruction.ArrayInitElement(
    typeIndex = typeIndex,
    elementIndex = elementIndex,
)

fun refI31Instruction() = AggregateInstruction.RefI31

fun i31GetSignedInstruction() = AggregateInstruction.I31GetSigned

fun i31GetUnsignedInstruction() = AggregateInstruction.I31GetUnsigned

fun anyConvertExternInstruction() = AggregateInstruction.AnyConvertExtern

fun externConvertAnyInstruction() = AggregateInstruction.ExternConvertAny
