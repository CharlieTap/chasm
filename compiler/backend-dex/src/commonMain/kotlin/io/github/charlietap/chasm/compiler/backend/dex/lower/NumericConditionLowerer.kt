package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.compiler.backend.analysis.form.ConditionForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.form
import io.github.charlietap.chasm.compiler.backend.analysis.form.negatable
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond

internal fun LowerContext.lowerCondition(
    condition: NumericCondition,
    holds: Boolean,
    target: String,
) {
    val form = condition.form()
    if (holds || form.negatable) return branchOnCondition(form, target, negated = !holds)
    val taken = emit.freshLabel()
    branchOnCondition(form, taken, negated = false)
    emit.gotoLabel(target)
    emit.place(taken)
}

private fun LowerContext.branchOnCondition(
    condition: ConditionForm,
    target: String,
    negated: Boolean,
) {
    when (condition) {
        is ConditionForm.Compare ->
            branchCompare(condition.type, condition.comparison, condition.left, condition.right, target, negated)
        is ConditionForm.And -> {
            loadInt(Registers.I0, Registers.W0, condition.left)
            loadInt(Registers.I1, Registers.W1, condition.right)
            emit.binary(BinaryOp.AndInt, Registers.I0, Registers.I0, Registers.I1)
            emit.branchZero(if (negated) Cond.Eq else Cond.Ne, Registers.I0, target)
        }
    }
}
