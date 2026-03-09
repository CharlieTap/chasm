package io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.parametricfused.SelectExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedParametricInstruction

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectIii) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectIis) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectIsi) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectIss) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectSii) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectSis) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectSsi) = dispatchInstruction(instruction, ::SelectExecutor)

fun SelectDispatcher(instruction: FusedParametricInstruction.SelectSss) = dispatchInstruction(instruction, ::SelectExecutor)
