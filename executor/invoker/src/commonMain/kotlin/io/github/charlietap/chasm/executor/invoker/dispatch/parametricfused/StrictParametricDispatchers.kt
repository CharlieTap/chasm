package io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.parametricfused.SelectExecutor
import io.github.charlietap.chasm.runtime.instruction.ParametricSuperInstruction

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIii) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIis) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIsi) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectIss) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSii) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSis) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSsi) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }

fun SelectDispatcher(instruction: ParametricSuperInstruction.SelectSss) = dispatchInstruction { vstack, context -> SelectExecutor(vstack, context, instruction) }
