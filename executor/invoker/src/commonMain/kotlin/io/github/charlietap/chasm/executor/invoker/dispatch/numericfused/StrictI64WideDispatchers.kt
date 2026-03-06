package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64Add128Executor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64MulWideSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64MulWideUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64Sub128Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Iiii) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Iiis) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Iisi) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Iiss) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Isii) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Isis) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Issi) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Isss) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Siii) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Siis) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Sisi) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Siss) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Ssii) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Ssis) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Sssi) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Add128Dispatcher(instruction: FusedNumericInstruction.I64Add128Ssss) = dispatchInstruction(instruction, ::I64Add128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Iiii) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Iiis) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Iisi) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Iiss) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Isii) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Isis) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Issi) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Isss) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Siii) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Siis) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Sisi) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Siss) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Ssii) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Ssis) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Sssi) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64Sub128Dispatcher(instruction: FusedNumericInstruction.I64Sub128Ssss) = dispatchInstruction(instruction, ::I64Sub128Executor)

fun I64MulWideSDispatcher(instruction: FusedNumericInstruction.I64MulWideSIi) = dispatchInstruction(instruction, ::I64MulWideSExecutor)

fun I64MulWideSDispatcher(instruction: FusedNumericInstruction.I64MulWideSIs) = dispatchInstruction(instruction, ::I64MulWideSExecutor)

fun I64MulWideSDispatcher(instruction: FusedNumericInstruction.I64MulWideSSi) = dispatchInstruction(instruction, ::I64MulWideSExecutor)

fun I64MulWideSDispatcher(instruction: FusedNumericInstruction.I64MulWideSSs) = dispatchInstruction(instruction, ::I64MulWideSExecutor)

fun I64MulWideUDispatcher(instruction: FusedNumericInstruction.I64MulWideUIi) = dispatchInstruction(instruction, ::I64MulWideUExecutor)

fun I64MulWideUDispatcher(instruction: FusedNumericInstruction.I64MulWideUIs) = dispatchInstruction(instruction, ::I64MulWideUExecutor)

fun I64MulWideUDispatcher(instruction: FusedNumericInstruction.I64MulWideUSi) = dispatchInstruction(instruction, ::I64MulWideUExecutor)

fun I64MulWideUDispatcher(instruction: FusedNumericInstruction.I64MulWideUSs) = dispatchInstruction(instruction, ::I64MulWideUExecutor)
