package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction

fun VariableSuperInstructionDispatcher(
    instruction: VariableSuperInstruction,
): DispatchableInstruction = when (instruction) {
    is VariableSuperInstruction.GlobalGetS -> GlobalGetDispatcher(instruction)
    is VariableSuperInstruction.GlobalSetI -> GlobalSetDispatcher(instruction)
    is VariableSuperInstruction.GlobalSetS -> GlobalSetDispatcher(instruction)
    is VariableSuperInstruction.LocalSetI -> LocalSetDispatcher(instruction)
    is VariableSuperInstruction.LocalSetS -> LocalSetDispatcher(instruction)
}
