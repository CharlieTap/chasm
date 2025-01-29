package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.fixture.ast.type.referenceType

fun referenceRuntimeInstruction() = refEqRuntimeInstruction()

fun refEqRuntimeInstruction() = ReferenceInstruction.RefEq

fun refTestRuntimeInstruction(
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefTest(
    referenceType = referenceType,
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
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefCast(
    referenceType = referenceType,
)
