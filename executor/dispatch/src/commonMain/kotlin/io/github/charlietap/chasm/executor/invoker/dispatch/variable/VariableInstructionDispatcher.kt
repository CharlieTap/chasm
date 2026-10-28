package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun VariableInstructionDispatcher(
    instruction: VariableInstruction,
): DispatchableInstruction = when (instruction) {
    is VariableInstruction.GlobalGetS -> GlobalGetDispatcher(instruction)
    is VariableInstruction.GlobalSetI -> GlobalSetDispatcher(instruction)
    is VariableInstruction.GlobalSetS -> GlobalSetDispatcher(instruction)
}
