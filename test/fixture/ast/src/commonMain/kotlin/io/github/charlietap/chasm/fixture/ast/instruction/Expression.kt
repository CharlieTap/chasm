package io.github.charlietap.chasm.fixture.ast.instruction

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction

fun expression(
    instructions: List<Instruction> = emptyList(),
) = Expression(
    instructions = instructions,
)
