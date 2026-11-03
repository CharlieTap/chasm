package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.Ordering
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.CompareOp
import io.github.charlietap.dexter.Cond

internal fun cond(
    ordering: Ordering,
): Cond = when (ordering) {
    Ordering.Equal -> Cond.Eq
    Ordering.NotEqual -> Cond.Ne
    Ordering.Less -> Cond.Lt
    Ordering.Greater -> Cond.Gt
    Ordering.LessOrEqual -> Cond.Le
    Ordering.GreaterOrEqual -> Cond.Ge
}

internal inline fun LowerContext.booleanResult(
    destination: Int,
    branch: (String) -> Unit,
) {
    val taken = emit.freshLabel()
    val end = emit.freshLabel()
    branch(taken)
    emit.const(Registers.I3, 0)
    emit.gotoLabel(end)
    emit.place(taken)
    emit.const(Registers.I3, 1)
    emit.place(end)
    storeInt(destination, Registers.I3)
}

internal fun LowerContext.branchCompare(
    type: NumberType,
    comparison: Comparison,
    left: Operand,
    right: Operand,
    target: String,
    negated: Boolean = false,
) {
    val condition = cond(comparison.ordering).let { if (negated) opposite(it) else it }
    when (type) {
        NumberType.I32 -> branchI32(condition, comparison.unsigned, left, right, target)
        NumberType.I64 -> {
            if (comparison.unsigned) {
                loadWide(Registers.W0, left)
                loadWide(Registers.W1, right)
                flipSignLongs()
                emit.compare(CompareOp.CmpLong, Registers.I0, Registers.W0, Registers.W1)
            } else {
                emit.compare(CompareOp.CmpLong, Registers.I0, wideSource(left, Registers.W0), wideSource(right, Registers.W1))
            }
            emit.branchZero(condition, Registers.I0, target)
        }
        NumberType.F32 -> {
            check(!negated) { "a float comparison cannot be negated" }
            val l = floatSource(left, Registers.I0)
            val r = floatSource(right, Registers.I1)
            emit.compare(floatCompare(condition, CompareOp.CmplFloat, CompareOp.CmpgFloat), Registers.I2, l, r)
            emit.branchZero(condition, Registers.I2, target)
        }
        NumberType.F64 -> {
            check(!negated) { "a float comparison cannot be negated" }
            val l = doubleSource(left, Registers.W0)
            val r = doubleSource(right, Registers.W1)
            emit.compare(floatCompare(condition, CompareOp.CmplDouble, CompareOp.CmpgDouble), Registers.I2, l, r)
            emit.branchZero(condition, Registers.I2, target)
        }
    }
}

private fun LowerContext.branchI32(
    compare: Cond,
    unsigned: Boolean,
    left: Operand,
    right: Operand,
    target: String,
) {
    val zeroRight = right is Operand.Immediate && right.bits.toInt() == 0
    val zeroLeft = left is Operand.Immediate && left.bits.toInt() == 0
    if (zeroRight || zeroLeft) {
        val against = if (zeroRight) compare else swapped(compare)
        val value = narrowSource(if (zeroRight) left else right, Registers.I0)
        if (!unsigned) return emit.branchZero(against, value, target)
        return when (against) {
            Cond.Lt -> Unit
            Cond.Ge -> emit.gotoLabel(target)
            Cond.Gt, Cond.Ne -> emit.branchZero(Cond.Ne, value, target)
            else -> emit.branchZero(Cond.Eq, value, target)
        }
    }
    if (unsigned) {
        emit.const(Registers.I2, Int.MIN_VALUE)
        flipSign(left, Registers.I0)
        flipSign(right, Registers.I1)
    } else {
        loadInt(Registers.I0, Registers.W0, left)
        loadInt(Registers.I1, Registers.W1, right)
    }
    emit.branchCompare(compare, Registers.I0, Registers.I1, target)
}

private fun LowerContext.flipSign(
    operand: Operand,
    register: Int,
) {
    if (operand is Operand.Immediate) return emit.const(register, operand.bits.toInt() xor Int.MIN_VALUE)
    emit.binary(BinaryOp.AddInt, register, narrowSource(operand, register), Registers.I2)
}

internal fun LowerContext.flipSignLongs() {
    emit.constWide(Registers.W2, Long.MIN_VALUE)
    emit.binary(BinaryOp.AddLong, Registers.W0, Registers.W0, Registers.W2)
    emit.binary(BinaryOp.AddLong, Registers.W1, Registers.W1, Registers.W2)
}

internal fun LowerContext.branchOnSlot(
    slot: Int,
    cond: Cond,
    target: String,
) {
    if (isInt(slot) && registers.home(slot) < 256) return emit.branchZero(cond, registers.home(slot), target)
    val value = wideSource(Operand.Slot(slot), Registers.W0)
    emit.constWide(Registers.W1, 0L)
    emit.compare(CompareOp.CmpLong, Registers.I0, value, Registers.W1)
    emit.branchZero(cond, Registers.I0, target)
}

private fun swapped(
    compare: Cond,
): Cond = when (compare) {
    Cond.Lt -> Cond.Gt
    Cond.Gt -> Cond.Lt
    Cond.Le -> Cond.Ge
    Cond.Ge -> Cond.Le
    else -> compare
}

private fun opposite(
    cond: Cond,
): Cond = when (cond) {
    Cond.Eq -> Cond.Ne
    Cond.Ne -> Cond.Eq
    Cond.Lt -> Cond.Ge
    Cond.Ge -> Cond.Lt
    Cond.Gt -> Cond.Le
    Cond.Le -> Cond.Gt
    else -> error("no condition $cond")
}

private fun floatCompare(
    compare: Cond,
    less: CompareOp,
    greater: CompareOp,
): CompareOp = when (compare) {
    Cond.Lt, Cond.Le -> greater
    else -> less
}
