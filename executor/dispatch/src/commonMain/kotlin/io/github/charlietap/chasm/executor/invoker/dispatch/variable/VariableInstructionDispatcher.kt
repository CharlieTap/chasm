package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun VariableInstructionDispatcher(
    instruction: VariableInstruction,
): DispatchableInstruction = when (instruction) {
    is VariableInstruction.GlobalGetS -> GlobalGetSDispatcher(instruction.global, instruction.destinationSlot)
    is VariableInstruction.GlobalSetI -> GlobalSetIDispatcher(instruction.value, instruction.global)
    is VariableInstruction.GlobalSetS -> GlobalSetSDispatcher(instruction.sourceSlot, instruction.global)
}
