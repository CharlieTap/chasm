package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.constant.F32ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.constant.F64ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.constant.I32ConstExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.constant.I64ConstExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ConstDispatcher(instruction: FusedNumericInstruction.I32ConstS) = dispatchInstruction(instruction, ::I32ConstExecutor)

fun I64ConstDispatcher(instruction: FusedNumericInstruction.I64ConstS) = dispatchInstruction(instruction, ::I64ConstExecutor)

fun F32ConstDispatcher(instruction: FusedNumericInstruction.F32ConstS) = dispatchInstruction(instruction, ::F32ConstExecutor)

fun F64ConstDispatcher(instruction: FusedNumericInstruction.F64ConstS) = dispatchInstruction(instruction, ::F64ConstExecutor)
