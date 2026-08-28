package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun variableRuntimeInstruction(): VariableInstruction = globalGetSRuntimeInstruction()

fun globalGetSRuntimeInstruction(
    global: GlobalInstance = globalInstance(),
    destinationSlot: Int = 0,
) = VariableInstruction.GlobalGetS(
    global = global,
    destinationSlot = destinationSlot,
)

fun globalSetIRuntimeInstruction(
    value: Long = 0L,
    global: GlobalInstance = globalInstance(),
) = VariableInstruction.GlobalSetI(
    value = value,
    global = global,
)

fun globalSetSRuntimeInstruction(
    sourceSlot: Int = 0,
    global: GlobalInstance = globalInstance(),
) = VariableInstruction.GlobalSetS(
    sourceSlot = sourceSlot,
    global = global,
)
