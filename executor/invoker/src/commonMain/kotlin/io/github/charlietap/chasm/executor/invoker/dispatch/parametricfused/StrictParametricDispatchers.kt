package io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.parametricfused.SelectExecutor
import io.github.charlietap.chasm.runtime.instruction.ParametricSuperInstruction

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIii) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIis) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIsi) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIss) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSii) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSis) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSsi) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSss) = dispatchInstruction(instruction, ::SelectExecutor)
