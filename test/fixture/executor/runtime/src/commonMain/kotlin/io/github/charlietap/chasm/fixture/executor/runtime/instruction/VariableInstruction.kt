package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.ast.module.Index.LocalIndex
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction
import io.github.charlietap.chasm.fixture.ast.module.globalIndex
import io.github.charlietap.chasm.fixture.ast.module.localIndex

fun variableRuntimeInstruction(): VariableInstruction = localGetRuntimeInstruction()

fun localGetRuntimeInstruction(
    localIdx: LocalIndex = localIndex(),
) = VariableInstruction.LocalGet(
    localIdx = localIdx,
)

fun localSetRuntimeInstruction(
    localIdx: LocalIndex = localIndex(),
) = VariableInstruction.LocalSet(
    localIdx = localIdx,
)

fun localTeeRuntimeInstruction(
    localIdx: LocalIndex = localIndex(),
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
