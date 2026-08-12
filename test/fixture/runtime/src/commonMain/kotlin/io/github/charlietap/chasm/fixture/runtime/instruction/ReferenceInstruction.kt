package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.type.referenceTypeTest
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

fun referenceRuntimeInstruction() = refEqRuntimeInstruction()

fun refEqRuntimeInstruction() = ReferenceInstruction.RefEq

fun refTestRuntimeInstruction(
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = ReferenceInstruction.RefTest(
    typeTest = typeTest,
)

fun refNullRuntimeInstruction(
    reference: Long = 0L,
) = ReferenceInstruction.RefNull(
    reference = reference,
)

fun refIsNullRuntimeInstruction() = ReferenceInstruction.RefIsNull

fun refAsNonNullRuntimeInstruction() = ReferenceInstruction.RefAsNonNull

fun refFuncRuntimeInstruction(
    reference: Long = 0L,
) = ReferenceInstruction.RefFunc(
    reference = reference,
)

fun refCastRuntimeInstruction(
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = ReferenceInstruction.RefCast(
    typeTest = typeTest,
)
