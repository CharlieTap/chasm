package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

fun parametricRuntimeInstruction(): ParametricInstruction = dropRuntimeInstruction()

fun dropRuntimeInstruction() = ParametricInstruction.Drop

fun selectRuntimeInstruction() = ParametricInstruction.Select

fun selectWithTypeRuntimeInstruction(
    types: List<ValueType> = listOf(),
) = ParametricInstruction.SelectWithType(
    types = types,
)
