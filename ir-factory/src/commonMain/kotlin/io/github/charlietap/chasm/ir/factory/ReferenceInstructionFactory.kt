package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction as IRReferenceInstruction
import io.github.charlietap.chasm.ir.module.Index.FunctionIndex as IRFunctionIndex
import io.github.charlietap.chasm.ir.type.HeapType as IRHeapType
import io.github.charlietap.chasm.ir.type.ReferenceType as IRReferenceType

internal fun ReferenceInstructionFactory(
    instruction: ReferenceInstruction,
): IRReferenceInstruction = ReferenceInstructionFactory(
    instruction = instruction,
    functionIndexFactory = ::FunctionIndexFactory,
    heapTypeFactory = ::HeapTypeFactory,
    referenceTypeFactory = ::ReferenceTypeFactory,
)

internal inline fun ReferenceInstructionFactory(
    instruction: ReferenceInstruction,
    functionIndexFactory: IRFactory<Index.FunctionIndex, IRFunctionIndex>,
    heapTypeFactory: IRFactory<HeapType, IRHeapType>,
    referenceTypeFactory: IRFactory<ReferenceType, IRReferenceType>,
): IRReferenceInstruction {
    return when (instruction) {
        is ReferenceInstruction.RefNull -> IRReferenceInstruction.RefNull(
            type = heapTypeFactory(instruction.type),
        )

        ReferenceInstruction.RefIsNull -> IRReferenceInstruction.RefIsNull

        ReferenceInstruction.RefAsNonNull -> IRReferenceInstruction.RefAsNonNull

        is ReferenceInstruction.RefFunc -> IRReferenceInstruction.RefFunc(
            funcIdx = functionIndexFactory(instruction.funcIdx),
        )

        ReferenceInstruction.RefEq -> IRReferenceInstruction.RefEq

        is ReferenceInstruction.RefTest -> IRReferenceInstruction.RefTest(
            referenceType = referenceTypeFactory(instruction.referenceType),
        )

        is ReferenceInstruction.RefCast -> IRReferenceInstruction.RefCast(
            referenceType = referenceTypeFactory(instruction.referenceType),
        )
    }
}
