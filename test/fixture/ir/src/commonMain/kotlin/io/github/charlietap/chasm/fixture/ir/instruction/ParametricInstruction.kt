package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.type.ValueType

fun parametricInstruction(): ParametricInstruction = dropInstruction()

fun dropInstruction() = ParametricInstruction.Drop

fun selectInstruction() = ParametricInstruction.Select

fun selectWithTypeInstruction(
    types: List<ValueType> = listOf(),
) = ParametricInstruction.SelectWithType(
    types = types,
)
