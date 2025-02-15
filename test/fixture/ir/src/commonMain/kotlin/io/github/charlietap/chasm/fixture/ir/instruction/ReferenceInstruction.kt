package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.type.heapType
import io.github.charlietap.chasm.fixture.ir.type.referenceType
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.ReferenceType

fun referenceInstruction() = refEqInstruction()

fun refEqInstruction() = ReferenceInstruction.RefEq

fun refTestInstruction(
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefTest(
    referenceType = referenceType,
)

fun refNullInstruction(
    type: HeapType = heapType(),
) = ReferenceInstruction.RefNull(
    type = type,
)

fun refIsNullInstruction() = ReferenceInstruction.RefIsNull

fun refAsNonNullInstruction() = ReferenceInstruction.RefAsNonNull

fun refFuncInstruction(
    funcIdx: Index.FunctionIndex = functionIndex(),
) = ReferenceInstruction.RefFunc(
    funcIdx = funcIdx,
)

fun refCastInstruction(
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefCast(
    referenceType = referenceType,
)

fun prefixedReferenceInstruction() = refTestInstruction()
