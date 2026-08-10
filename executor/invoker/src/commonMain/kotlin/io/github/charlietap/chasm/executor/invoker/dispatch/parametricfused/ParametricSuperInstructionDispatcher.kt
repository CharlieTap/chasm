package io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ParametricSuperInstruction

fun ParametricSuperInstructionDispatcher(
    instruction: ParametricSuperInstruction,
): DispatchableInstruction = when (instruction) {
    is ParametricSuperInstruction.SelectIii -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectIis -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectIsi -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectIss -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectSii -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectSis -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectSsi -> SelectDispatcher(instruction)
    is ParametricSuperInstruction.SelectSss -> SelectDispatcher(instruction)
}
