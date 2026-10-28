package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun ParametricInstructionDispatcher(
    instruction: ParametricInstruction,
): DispatchableInstruction = when (instruction) {
    is ParametricInstruction.SelectIii -> SelectIiiDispatcher(instruction.condition, instruction.val1, instruction.val2, instruction.destinationSlot)
    is ParametricInstruction.SelectIis -> SelectIisDispatcher(instruction.condition, instruction.val1, instruction.val2Slot, instruction.destinationSlot)
    is ParametricInstruction.SelectIsi -> SelectIsiDispatcher(instruction.condition, instruction.val1Slot, instruction.val2, instruction.destinationSlot)
    is ParametricInstruction.SelectIss -> SelectIssDispatcher(instruction.condition, instruction.val1Slot, instruction.val2Slot, instruction.destinationSlot)
    is ParametricInstruction.SelectSii -> SelectSiiDispatcher(instruction.conditionSlot, instruction.val1, instruction.val2, instruction.destinationSlot)
    is ParametricInstruction.SelectSis -> SelectSisDispatcher(instruction.conditionSlot, instruction.val1, instruction.val2Slot, instruction.destinationSlot)
    is ParametricInstruction.SelectSsi -> SelectSsiDispatcher(instruction.conditionSlot, instruction.val1Slot, instruction.val2, instruction.destinationSlot)
    is ParametricInstruction.SelectSss -> SelectSssDispatcher(instruction.conditionSlot, instruction.val1Slot, instruction.val2Slot, instruction.destinationSlot)
}
