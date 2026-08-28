package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.type.referenceTypeTest
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

fun referenceRuntimeInstruction(): ReferenceInstruction = refCastSRuntimeInstruction()

fun refCastSRuntimeInstruction(
    referenceSlot: Int = 0,
    destinationSlot: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = ReferenceInstruction.RefCastS(
    referenceSlot = referenceSlot,
    destinationSlot = destinationSlot,
    typeTest = typeTest,
)

fun refEqSsRuntimeInstruction(
    reference1Slot: Int = 0,
    reference2Slot: Int = 0,
    destinationSlot: Int = 0,
) = ReferenceInstruction.RefEqSs(
    reference1Slot = reference1Slot,
    reference2Slot = reference2Slot,
    destinationSlot = destinationSlot,
)

fun refIsNullSRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = ReferenceInstruction.RefIsNullS(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun refAsNonNullSRuntimeInstruction(
    valueSlot: Int = 0,
    destinationSlot: Int = 0,
) = ReferenceInstruction.RefAsNonNullS(
    valueSlot = valueSlot,
    destinationSlot = destinationSlot,
)

fun refNullSRuntimeInstruction(
    reference: Long = 0L,
    destinationSlot: Int = 0,
) = ReferenceInstruction.RefNullS(
    reference = reference,
    destinationSlot = destinationSlot,
)

fun refFuncSRuntimeInstruction(
    reference: Long = 0L,
    destinationSlot: Int = 0,
) = ReferenceInstruction.RefFuncS(
    reference = reference,
    destinationSlot = destinationSlot,
)

fun refTestSRuntimeInstruction(
    referenceSlot: Int = 0,
    destinationSlot: Int = 0,
    typeTest: ReferenceTypeTest = referenceTypeTest(),
) = ReferenceInstruction.RefTestS(
    referenceSlot = referenceSlot,
    destinationSlot = destinationSlot,
    typeTest = typeTest,
)
