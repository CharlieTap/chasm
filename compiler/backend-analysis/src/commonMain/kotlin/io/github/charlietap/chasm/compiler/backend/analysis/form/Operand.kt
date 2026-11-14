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
): Operand {
    return if (slot in 0 until SHARED_SLOTS) {
        sharedSlots[slot]
    } else {
        Operand.Slot(slot)
    }
}

fun immediate(
    value: Int,
): Operand {
    return immediate(value.toLong())
}

fun immediate(
    value: Long,
): Operand {
    return if (value in SHARED_IMMEDIATE_LOW..SHARED_IMMEDIATE_HIGH) {
        sharedImmediates[(value - SHARED_IMMEDIATE_LOW).toInt()]
    } else {
        Operand.Immediate(value)
    }
}

fun immediate(
    value: Float,
): Operand {
    return immediate(value.toRawBits().toLong())
}

fun immediate(
    value: Double,
): Operand {
    return immediate(value.toRawBits())
}

fun OperandTransfer.operands(): List<Operand> {
    return List(size) { index ->
        if (immediates[index]) immediate(values[index]) else slot(values[index].toInt())
    }
}

fun FusedOperand.operand(): Operand {
    return when (this) {
        is FusedOperand.FrameSlot -> slot(offset)
        is FusedOperand.I32Const -> immediate(const)
        is FusedOperand.I64Const -> immediate(const)
        is FusedOperand.F32Const -> immediate(const)
        is FusedOperand.F64Const -> immediate(const)
    }
}

private const val SHARED_SLOTS = 4_096

private const val SHARED_IMMEDIATE_LOW = -256L

private const val SHARED_IMMEDIATE_HIGH = 4_095L

private val sharedSlots: Array<Operand> = Array(SHARED_SLOTS) { slot -> Operand.Slot(slot) }

private val sharedImmediates: Array<Operand> = Array((SHARED_IMMEDIATE_HIGH - SHARED_IMMEDIATE_LOW + 1).toInt()) { index ->
    Operand.Immediate(SHARED_IMMEDIATE_LOW + index)
}
