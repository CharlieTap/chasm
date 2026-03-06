package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalSetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.LocalSetExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction

fun GlobalGetDispatcher(instruction: FusedVariableInstruction.GlobalGetS) = dispatchInstruction(instruction, ::GlobalGetExecutor)

fun GlobalSetDispatcher(instruction: FusedVariableInstruction.GlobalSetI) = dispatchInstruction(instruction, ::GlobalSetExecutor)

fun GlobalSetDispatcher(instruction: FusedVariableInstruction.GlobalSetS) = dispatchInstruction(instruction, ::GlobalSetExecutor)

fun LocalSetDispatcher(instruction: FusedVariableInstruction.LocalSetI) = dispatchInstruction(instruction, ::LocalSetExecutor)

fun LocalSetDispatcher(instruction: FusedVariableInstruction.LocalSetS) = dispatchInstruction(instruction, ::LocalSetExecutor)
