package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.compiler.backend.analysis.form.Comparison
import io.github.charlietap.chasm.compiler.backend.analysis.form.ConditionForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.Ordering
import io.github.charlietap.chasm.compiler.backend.analysis.form.form
import io.github.charlietap.chasm.compiler.backend.analysis.form.negatable
import io.github.charlietap.chasm.type.NumberType
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

internal inline fun LowerContext.flipped(
    push: () -> Unit,
) {
    push()
    emit.int(Int.MIN_VALUE)
    emit.insn(Opcodes.IADD)
}

internal inline fun LowerContext.flippedLong(
    push: () -> Unit,
) {
    push()
    emit.long(Long.MIN_VALUE)
    emit.insn(Opcodes.LADD)
}

internal fun LowerContext.branchCompare(
    type: NumberType,
    comparison: Comparison,
    left: Operand,
    right: Operand,
    target: Label,
    negated: Boolean,
) {
    val ordering = if (negated) comparison.ordering.opposite() else comparison.ordering
    when (type) {
        NumberType.I32 -> when {
            comparison.unsigned -> {
                flipped { pushInt(left) }
                flipped { pushInt(right) }
                emit.jump(intBranch(ordering), target)
            }
            right == ZERO -> {
                pushInt(left)
                emit.jump(zeroBranch(ordering), target)
            }
            else -> {
                pushInt(left)
                pushInt(right)
                emit.jump(intBranch(ordering), target)
            }
        }
        NumberType.I64 -> {
            if (comparison.unsigned) {
                flippedLong { pushLong(left) }
                flippedLong { pushLong(right) }
            } else {
                pushLong(left)
                pushLong(right)
            }
            emit.insn(Opcodes.LCMP)
            emit.jump(zeroBranch(ordering), target)
        }
        NumberType.F32, NumberType.F64 -> {
            check(!negated) { "a float comparison cannot be negated" }
            push(type, left)
            push(type, right)
            val greaterOnNaN = ordering == Ordering.Less || ordering == Ordering.LessOrEqual
            emit.insn(
                when {
                    type == NumberType.F32 -> if (greaterOnNaN) Opcodes.FCMPG else Opcodes.FCMPL
                    else -> if (greaterOnNaN) Opcodes.DCMPG else Opcodes.DCMPL
                },
            )
            emit.jump(zeroBranch(ordering), target)
        }
        else -> throw Unsupported("$type comparisons")
    }
}

internal fun LowerContext.lowerCondition(
    condition: NumericCondition,
    holds: Boolean,
    target: Label,
) {
    val form = condition.form()
    if (holds || form.negatable) return branchOnCondition(form, target, negated = !holds)
    val taken = emit.label()
    branchOnCondition(form, taken, negated = false)
    emit.jump(Opcodes.GOTO, target)
    emit.place(taken)
}

private fun LowerContext.branchOnCondition(
    condition: ConditionForm,
    target: Label,
    negated: Boolean,
) {
    when (condition) {
        is ConditionForm.Compare -> branchCompare(condition.type, condition.comparison, condition.left, condition.right, target, negated)
        is ConditionForm.And -> {
            pushInt(condition.left)
            pushInt(condition.right)
            emit.insn(Opcodes.IAND)
            emit.jump(if (negated) Opcodes.IFEQ else Opcodes.IFNE, target)
        }
    }
}

internal fun LowerContext.branchOnInt(
    operand: Operand,
    zero: Boolean,
    target: Label,
) {
    pushInt(operand)
    emit.jump(if (zero) Opcodes.IFEQ else Opcodes.IFNE, target)
}

private val ZERO: Operand = Operand.Immediate(0)

private fun Ordering.opposite(): Ordering = when (this) {
    Ordering.Equal -> Ordering.NotEqual
    Ordering.NotEqual -> Ordering.Equal
    Ordering.Less -> Ordering.GreaterOrEqual
    Ordering.GreaterOrEqual -> Ordering.Less
    Ordering.Greater -> Ordering.LessOrEqual
    Ordering.LessOrEqual -> Ordering.Greater
}

private fun intBranch(
    ordering: Ordering,
): Int = when (ordering) {
    Ordering.Equal -> Opcodes.IF_ICMPEQ
    Ordering.NotEqual -> Opcodes.IF_ICMPNE
    Ordering.Less -> Opcodes.IF_ICMPLT
    Ordering.Greater -> Opcodes.IF_ICMPGT
    Ordering.LessOrEqual -> Opcodes.IF_ICMPLE
    Ordering.GreaterOrEqual -> Opcodes.IF_ICMPGE
}

private fun zeroBranch(
    ordering: Ordering,
): Int = when (ordering) {
    Ordering.Equal -> Opcodes.IFEQ
    Ordering.NotEqual -> Opcodes.IFNE
    Ordering.Less -> Opcodes.IFLT
    Ordering.Greater -> Opcodes.IFGT
    Ordering.LessOrEqual -> Opcodes.IFLE
    Ordering.GreaterOrEqual -> Opcodes.IFGE
}
