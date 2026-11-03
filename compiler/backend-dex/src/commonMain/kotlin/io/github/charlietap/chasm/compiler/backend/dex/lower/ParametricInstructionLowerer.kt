package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.select
import io.github.charlietap.dexter.Cond

internal fun lowerParametricInstruction(
    instruction: ParametricInstruction,
    context: LowerContext,
) {
    val select = instruction.select()
    val condition = select.condition
    if (condition is Operand.Immediate) {
        return context.copyToSlot(select.destination, if (condition.bits != 0L) select.first else select.second)
    }
    val chooseSecond = context.emit.freshLabel()
    val end = context.emit.freshLabel()
    context.branchOnSlot((condition as Operand.Slot).slot, Cond.Eq, chooseSecond)
    context.copyToSlot(select.destination, select.first)
    context.emit.gotoLabel(end)
    context.emit.place(chooseSecond)
    context.copyToSlot(select.destination, select.second)
    context.emit.place(end)
}
