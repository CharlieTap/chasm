package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand

fun fusedNumericInstruction(): FusedNumericInstruction = fusedI32Add()

fun fusedI32Const(
    value: Int = 0,
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.I32Const(
    value = value,
    destination = destination,
)

fun fusedI64Const(
    value: Long = 0L,
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.I64Const(
    value = value,
    destination = destination,
)

fun fusedF32Const(
    value: Float = 0f,
    bits: Int = value.toRawBits(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.F32Const(
    bits = bits,
    destination = destination,
)

fun fusedF64Const(
    value: Double = 0.0,
    bits: Long = value.toRawBits(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.F64Const(
    bits = bits,
    destination = destination,
)

fun fusedI32Add(
    left: FusedOperand = fusedOperand(),
    right: FusedOperand = fusedOperand(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.I32Add(
    left = left,
    right = right,
    destination = destination,
)

fun fusedI32Eqz(
    operand: FusedOperand = fusedOperand(),
    destination: FusedDestination = fusedDestination(),
): FusedNumericInstruction = FusedNumericInstruction.I32Eqz(
    operand = operand,
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
