package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand

fun fusedNumericInstruction(): FusedNumericInstruction = fusedI32Add()

fun fusedI32Add(
    left: FusedOperand = fusedOperand(),
    right: FusedOperand = fusedOperand(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.I32Add(
    left = left,
    right = right,
    destination = destination,
)

fun fusedI32And(
    left: FusedOperand = fusedOperand(),
    right: FusedOperand = fusedOperand(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.I32And(
    left = left,
    right = right,
    destination = destination,
)

fun fusedF32Abs(
    operand: FusedOperand = fusedOperand(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.F32Abs(
    operand = operand,
    destination = destination,
)
