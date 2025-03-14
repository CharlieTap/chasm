package io.github.charlietap.chasm.fixture.ast.instruction

import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.type.ValueType

fun parametricInstruction(): ParametricInstruction = dropInstruction()

fun dropInstruction() = ParametricInstruction.Drop

fun selectInstruction() = ParametricInstruction.Select

fun selectWithTypeInstruction(
    types: List<ValueType> = listOf(),
) = ParametricInstruction.SelectWithType(
    types = types,
)
