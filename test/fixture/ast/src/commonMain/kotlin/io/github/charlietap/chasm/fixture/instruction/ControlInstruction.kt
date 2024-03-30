package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.fixture.module.labelIndex
import io.github.charlietap.chasm.fixture.type.referenceType

fun controlInstruction() = unreachableInstruction()

fun unreachableInstruction() = ControlInstruction.Unreachable

fun prefixedControlInstruction() = brOnCastInstruction()

fun brOnCastInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = ControlInstruction.BrOnCast(
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)
