package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction as IRAggregateInstruction
import io.github.charlietap.chasm.ir.module.Index.DataIndex as IRDataIndex
import io.github.charlietap.chasm.ir.module.Index.ElementIndex as IRElementIndex
import io.github.charlietap.chasm.ir.module.Index.FieldIndex as IRFieldIndex
import io.github.charlietap.chasm.ir.module.Index.TypeIndex as IRTypeIndex

internal fun AggregateInstructionFactory(
    instruction: AggregateInstruction,
): IRAggregateInstruction = AggregateInstructionFactory(
    instruction = instruction,
    typeIndexFactory = ::TypeIndexFactory,
    fieldIndexFactory = ::FieldIndexFactory,
    dataIndexFactory = ::DataIndexFactory,
    elementIndexFactory = ::ElementIndexFactory,
)

internal inline fun AggregateInstructionFactory(
    instruction: AggregateInstruction,
    typeIndexFactory: IRFactory<Index.TypeIndex, IRTypeIndex>,
    fieldIndexFactory: IRFactory<Index.FieldIndex, IRFieldIndex>,
    dataIndexFactory: IRFactory<Index.DataIndex, IRDataIndex>,
    elementIndexFactory: IRFactory<Index.ElementIndex, IRElementIndex>,
): IRAggregateInstruction {
    return when (instruction) {
        AggregateInstruction.AnyConvertExtern -> IRAggregateInstruction.AnyConvertExtern
        is AggregateInstruction.ArrayCopy -> IRAggregateInstruction.ArrayCopy(
            sourceTypeIndex = typeIndexFactory(instruction.sourceTypeIndex),
            destinationTypeIndex = typeIndexFactory(instruction.destinationTypeIndex),
        )

        is AggregateInstruction.ArrayFill -> IRAggregateInstruction.ArrayFill(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.ArrayGet -> IRAggregateInstruction.ArrayGet(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.ArrayGetSigned -> IRAggregateInstruction.ArrayGetSigned(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.ArrayGetUnsigned -> IRAggregateInstruction.ArrayGetUnsigned(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.ArrayInitData -> IRAggregateInstruction.ArrayInitData(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            dataIndex = dataIndexFactory(instruction.dataIndex),
        )

        is AggregateInstruction.ArrayInitElement -> IRAggregateInstruction.ArrayInitElement(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            elementIndex = elementIndexFactory(instruction.elementIndex),
        )

        AggregateInstruction.ArrayLen -> IRAggregateInstruction.ArrayLen
        is AggregateInstruction.ArrayNew -> IRAggregateInstruction.ArrayNew(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.ArrayNewData -> IRAggregateInstruction.ArrayNewData(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            dataIndex = dataIndexFactory(instruction.dataIndex),
        )

        is AggregateInstruction.ArrayNewDefault -> IRAggregateInstruction.ArrayNewDefault(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.ArrayNewElement -> IRAggregateInstruction.ArrayNewElement(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            elementIndex = elementIndexFactory(instruction.elementIndex),
        )

        is AggregateInstruction.ArrayNewFixed -> IRAggregateInstruction.ArrayNewFixed(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            size = instruction.size,
        )

        is AggregateInstruction.ArraySet -> IRAggregateInstruction.ArraySet(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        AggregateInstruction.ExternConvertAny -> IRAggregateInstruction.ExternConvertAny
        AggregateInstruction.I31GetSigned -> IRAggregateInstruction.I31GetSigned
        AggregateInstruction.I31GetUnsigned -> IRAggregateInstruction.I31GetUnsigned
        AggregateInstruction.RefI31 -> IRAggregateInstruction.RefI31
        is AggregateInstruction.StructGet -> IRAggregateInstruction.StructGet(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            fieldIndex = fieldIndexFactory(instruction.fieldIndex),
        )

        is AggregateInstruction.StructGetSigned -> IRAggregateInstruction.StructGetSigned(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            fieldIndex = fieldIndexFactory(instruction.fieldIndex),
        )

        is AggregateInstruction.StructGetUnsigned -> IRAggregateInstruction.StructGetUnsigned(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            fieldIndex = fieldIndexFactory(instruction.fieldIndex),
        )

        is AggregateInstruction.StructNew -> IRAggregateInstruction.StructNew(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.StructNewDefault -> IRAggregateInstruction.StructNewDefault(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is AggregateInstruction.StructSet -> IRAggregateInstruction.StructSet(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            fieldIndex = fieldIndexFactory(instruction.fieldIndex),
        )
    }
}
