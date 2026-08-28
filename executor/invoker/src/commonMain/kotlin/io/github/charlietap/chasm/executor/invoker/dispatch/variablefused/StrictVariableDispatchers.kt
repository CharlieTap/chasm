package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalSetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.LocalSetExecutor
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction

fun GlobalGetDispatcher(instruction: VariableSuperInstruction.GlobalGetS) = dispatchInstruction { vstack, context -> GlobalGetExecutor(vstack, context, instruction) }

fun GlobalSetDispatcher(instruction: VariableSuperInstruction.GlobalSetI) = dispatchInstruction { vstack, context -> GlobalSetExecutor(vstack, context, instruction) }

fun GlobalSetDispatcher(instruction: VariableSuperInstruction.GlobalSetS) = dispatchInstruction { vstack, context -> GlobalSetExecutor(vstack, context, instruction) }

fun LocalSetDispatcher(instruction: VariableSuperInstruction.LocalSetI) = dispatchInstruction { vstack, context -> LocalSetExecutor(vstack, context, instruction) }

fun LocalSetDispatcher(instruction: VariableSuperInstruction.LocalSetS) = dispatchInstruction { vstack, context -> LocalSetExecutor(vstack, context, instruction) }
