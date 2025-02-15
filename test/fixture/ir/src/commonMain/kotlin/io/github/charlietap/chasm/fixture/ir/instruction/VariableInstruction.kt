package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.module.Index.GlobalIndex
import io.github.charlietap.chasm.ir.module.Index.LocalIndex

fun variableInstruction(): VariableInstruction = localGetInstruction()

fun localGetInstruction(
    localIdx: LocalIndex = localIndex(),
) = VariableInstruction.LocalGet(
    localIdx = localIdx,
)

fun localSetInstruction(
    localIdx: LocalIndex = localIndex(),
) = VariableInstruction.LocalSet(
    localIdx = localIdx,
)

fun localTeeInstruction(
    localIdx: LocalIndex = localIndex(),
) = VariableInstruction.LocalTee(
    localIdx = localIdx,
)

fun globalGetInstruction(
    globalIdx: GlobalIndex = globalIndex(),
) = VariableInstruction.GlobalGet(
    globalIdx = globalIdx,
)

fun globalSetInstruction(
    globalIdx: GlobalIndex = globalIndex(),
) = VariableInstruction.GlobalSet(
    globalIdx = globalIdx,
)
