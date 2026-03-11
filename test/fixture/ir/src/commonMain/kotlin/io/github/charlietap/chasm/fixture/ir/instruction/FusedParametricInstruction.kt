package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.ParametricSuperInstruction

fun fusedParametricInstruction(): ParametricSuperInstruction = fusedSelect()

fun fusedSelect(
    const: FusedOperand = fusedOperand(),
    val1: FusedOperand = fusedOperand(),
    val2: FusedOperand = fusedOperand(),
    destination: FusedDestination = fusedDestination(),
) = ParametricSuperInstruction.Select(
    const = const,
    val1 = val1,
    val2 = val2,
    destination = destination,
)
