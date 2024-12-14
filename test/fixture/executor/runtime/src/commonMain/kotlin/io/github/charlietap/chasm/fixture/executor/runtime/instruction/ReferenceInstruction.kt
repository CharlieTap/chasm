package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.ast.type.referenceType

fun referenceRuntimeInstruction() = refEqRuntimeInstruction()

fun refEqRuntimeInstruction() = ReferenceInstruction.RefEq

fun refTestRuntimeInstruction(
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefTest(
    referenceType = referenceType,
)

fun refNullRuntimeInstruction(
    type: HeapType = heapType(),
) = ReferenceInstruction.RefNull(
    type = type,
)

fun refIsNullRuntimeInstruction() = ReferenceInstruction.RefIsNull

fun refAsNonNullRuntimeInstruction() = ReferenceInstruction.RefAsNonNull

fun refFuncRuntimeInstruction(
    funcIdx: Index.FunctionIndex = functionIndex(),
) = ReferenceInstruction.RefFunc(
    funcIdx = funcIdx,
)

fun refCastRuntimeInstruction(
    referenceType: ReferenceType = referenceType(),
) = ReferenceInstruction.RefCast(
    referenceType = referenceType,
)
