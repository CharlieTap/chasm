package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction
import io.github.charlietap.chasm.fixture.ast.module.globalIndex

fun variableRuntimeInstruction(): VariableInstruction = localGetRuntimeInstruction()

fun localGetRuntimeInstruction(
    localIdx: Int = 0,
) = VariableInstruction.LocalGet(
    localIdx = localIdx,
)

fun localSetRuntimeInstruction(
    localIdx: Int = 0,
) = VariableInstruction.LocalSet(
    localIdx = localIdx,
)

fun localTeeRuntimeInstruction(
    localIdx: Int = 0,
) = VariableInstruction.LocalTee(
    localIdx = localIdx,
)

fun globalGetRuntimeInstruction(
    globalIdx: GlobalIndex = globalIndex(),
) = VariableInstruction.GlobalGet(
    globalIdx = globalIdx,
)

fun globalSetRuntimeInstruction(
    globalIdx: GlobalIndex = globalIndex(),
) = VariableInstruction.GlobalSet(
    globalIdx = globalIdx,
)
