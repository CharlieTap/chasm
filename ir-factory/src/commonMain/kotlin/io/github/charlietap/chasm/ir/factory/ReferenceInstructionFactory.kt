package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction as IRReferenceInstruction
import io.github.charlietap.chasm.ir.module.Index.FunctionIndex as IRFunctionIndex

internal fun ReferenceInstructionFactory(
    instruction: ReferenceInstruction,
): IRReferenceInstruction = ReferenceInstructionFactory(
    instruction = instruction,
    functionIndexFactory = ::FunctionIndexFactory,
)

internal inline fun ReferenceInstructionFactory(
    instruction: ReferenceInstruction,
    functionIndexFactory: IRFactory<Index.FunctionIndex, IRFunctionIndex>,
): IRReferenceInstruction {
    return when (instruction) {
        is ReferenceInstruction.RefNull -> IRReferenceInstruction.RefNull(
            type = instruction.type,
        )

        ReferenceInstruction.RefIsNull -> IRReferenceInstruction.RefIsNull

        ReferenceInstruction.RefAsNonNull -> IRReferenceInstruction.RefAsNonNull

        is ReferenceInstruction.RefFunc -> IRReferenceInstruction.RefFunc(
            funcIdx = functionIndexFactory(instruction.funcIdx),
        )

        ReferenceInstruction.RefEq -> IRReferenceInstruction.RefEq

        is ReferenceInstruction.RefTest -> IRReferenceInstruction.RefTest(
            referenceType = instruction.referenceType,
        )

        is ReferenceInstruction.RefCast -> IRReferenceInstruction.RefCast(
            referenceType = instruction.referenceType,
        )
    }
}
