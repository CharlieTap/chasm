package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction

fun expression(
    instructions: List<Instruction> = emptyList(),
) = Expression(
    instructions = instructions,
)
