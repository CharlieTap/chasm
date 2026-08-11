package io.github.charlietap.chasm.runtime.instruction

import kotlin.jvm.JvmInline

class OperandCopyPlan(
    val operands: Array<CopyOperand>,
    val order: OperandCopyOrder,
) {

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is OperandCopyPlan && operands.contentEquals(other.operands) && order == other.order

    override fun hashCode(): Int = 31 * operands.contentHashCode() + order.hashCode()

    override fun toString(): String = "OperandCopyPlan(operands=${operands.contentToString()}, order=$order)"
}

enum class OperandCopyOrder {
    None,
    Forward,
    Reverse,
    Staged,
}

sealed interface CopyOperand {

    @JvmInline
    value class Immediate(val value: Long) : CopyOperand

    @JvmInline
    value class Slot(val slot: Int) : CopyOperand
}
