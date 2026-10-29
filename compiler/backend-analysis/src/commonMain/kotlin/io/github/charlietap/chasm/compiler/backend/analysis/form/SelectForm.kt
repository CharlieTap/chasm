package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.ParametricInstruction

class SelectForm(
    val condition: Operand,
    val first: Operand,
    val second: Operand,
    val destination: Int,
)

fun ParametricInstruction.select(): SelectForm = when (val i = this) {
    is ParametricInstruction.SelectIii -> SelectForm(immediate(i.condition), immediate(i.val1), immediate(i.val2), i.destinationSlot)
    is ParametricInstruction.SelectIis -> SelectForm(immediate(i.condition), immediate(i.val1), slot(i.val2Slot), i.destinationSlot)
    is ParametricInstruction.SelectIsi -> SelectForm(immediate(i.condition), slot(i.val1Slot), immediate(i.val2), i.destinationSlot)
    is ParametricInstruction.SelectIss -> SelectForm(immediate(i.condition), slot(i.val1Slot), slot(i.val2Slot), i.destinationSlot)
    is ParametricInstruction.SelectSii -> SelectForm(slot(i.conditionSlot), immediate(i.val1), immediate(i.val2), i.destinationSlot)
    is ParametricInstruction.SelectSis -> SelectForm(slot(i.conditionSlot), immediate(i.val1), slot(i.val2Slot), i.destinationSlot)
    is ParametricInstruction.SelectSsi -> SelectForm(slot(i.conditionSlot), slot(i.val1Slot), immediate(i.val2), i.destinationSlot)
    is ParametricInstruction.SelectSss -> SelectForm(slot(i.conditionSlot), slot(i.val1Slot), slot(i.val2Slot), i.destinationSlot)
}
