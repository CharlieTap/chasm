package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

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
    global: GlobalInstance = globalInstance(),
) = VariableInstruction.GlobalGet(
    global = global,
)

fun globalSetRuntimeInstruction(
    global: GlobalInstance = globalInstance(),
) = VariableInstruction.GlobalSet(
    global = global,
)
