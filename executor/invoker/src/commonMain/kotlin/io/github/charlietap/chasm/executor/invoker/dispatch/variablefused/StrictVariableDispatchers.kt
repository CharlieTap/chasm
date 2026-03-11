package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalSetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.LocalSetExecutor
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction

fun GlobalGetDispatcher(instruction: VariableSuperInstruction.GlobalGetS) = dispatchInstruction(instruction, ::GlobalGetExecutor)

fun GlobalSetDispatcher(instruction: VariableSuperInstruction.GlobalSetI) = dispatchInstruction(instruction, ::GlobalSetExecutor)

fun GlobalSetDispatcher(instruction: VariableSuperInstruction.GlobalSetS) = dispatchInstruction(instruction, ::GlobalSetExecutor)

fun LocalSetDispatcher(instruction: VariableSuperInstruction.LocalSetI) = dispatchInstruction(instruction, ::LocalSetExecutor)

fun LocalSetDispatcher(instruction: VariableSuperInstruction.LocalSetS) = dispatchInstruction(instruction, ::LocalSetExecutor)
