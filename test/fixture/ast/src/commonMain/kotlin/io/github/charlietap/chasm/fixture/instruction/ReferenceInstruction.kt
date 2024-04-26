package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.fixture.module.functionIndex
import io.github.charlietap.chasm.fixture.type.heapType
import io.github.charlietap.chasm.fixture.type.referenceType

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
