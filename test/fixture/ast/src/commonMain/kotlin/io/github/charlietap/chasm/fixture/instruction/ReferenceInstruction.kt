package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.fixture.type.referenceType

fun referenceInstruction() = refEqInstruction()

fun refEqInstruction() = ReferenceInstruction.RefEq

fun prefixedReferenceInstruction() = refTest()

fun refTest(
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefTest(
    referenceType = referenceType,
)
