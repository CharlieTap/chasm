package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.OperandTransfer
import kotlin.jvm.JvmInline

sealed interface Operand {

    @JvmInline
    value class Slot(
        val slot: Int,
    ) : Operand

    @JvmInline
    value class Immediate(
        val bits: Long,
    ) : Operand
}

fun slot(
    slot: Int,
): Operand = Operand.Slot(slot)

fun immediate(
    value: Int,
): Operand = Operand.Immediate(value.toLong())

fun immediate(
    value: Long,
): Operand = Operand.Immediate(value)

fun immediate(
    value: Float,
): Operand = Operand.Immediate(value.toRawBits().toLong())

fun immediate(
    value: Double,
): Operand = Operand.Immediate(value.toRawBits())

fun OperandTransfer.operands(): List<Operand> = List(size) { index ->
    if (immediates[index]) Operand.Immediate(values[index]) else Operand.Slot(values[index].toInt())
}

fun FusedOperand.operand(): Operand = when (this) {
    is FusedOperand.FrameSlot -> Operand.Slot(offset)
    is FusedOperand.I32Const -> immediate(const)
    is FusedOperand.I64Const -> immediate(const)
    is FusedOperand.F32Const -> immediate(const)
    is FusedOperand.F64Const -> immediate(const)
}
