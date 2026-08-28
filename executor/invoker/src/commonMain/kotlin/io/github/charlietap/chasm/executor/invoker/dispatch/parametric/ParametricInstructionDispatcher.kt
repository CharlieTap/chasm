package io.github.charlietap.chasm.executor.invoker.dispatch.parametric

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun ParametricInstructionDispatcher(
    instruction: ParametricInstruction,
): DispatchableInstruction = when (instruction) {
    is ParametricInstruction.SelectIii -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectIis -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectIsi -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectIss -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectSii -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectSis -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectSsi -> SelectDispatcher(instruction)
    is ParametricInstruction.SelectSss -> SelectDispatcher(instruction)
}
