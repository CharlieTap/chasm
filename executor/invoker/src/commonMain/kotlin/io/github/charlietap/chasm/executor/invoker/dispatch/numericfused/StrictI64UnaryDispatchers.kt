package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64PopcntExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64ClzDispatcher(instruction: FusedNumericInstruction.I64ClzI) = dispatchInstruction(instruction, ::I64ClzExecutor)

fun I64ClzDispatcher(instruction: FusedNumericInstruction.I64ClzS) = dispatchInstruction(instruction, ::I64ClzExecutor)

fun I64CtzDispatcher(instruction: FusedNumericInstruction.I64CtzI) = dispatchInstruction(instruction, ::I64CtzExecutor)

fun I64CtzDispatcher(instruction: FusedNumericInstruction.I64CtzS) = dispatchInstruction(instruction, ::I64CtzExecutor)

fun I64PopcntDispatcher(instruction: FusedNumericInstruction.I64PopcntI) = dispatchInstruction(instruction, ::I64PopcntExecutor)

fun I64PopcntDispatcher(instruction: FusedNumericInstruction.I64PopcntS) = dispatchInstruction(instruction, ::I64PopcntExecutor)

fun I64Extend8SDispatcher(instruction: FusedNumericInstruction.I64Extend8SI) = dispatchInstruction(instruction, ::I64Extend8SExecutor)

fun I64Extend8SDispatcher(instruction: FusedNumericInstruction.I64Extend8SS) = dispatchInstruction(instruction, ::I64Extend8SExecutor)

fun I64Extend16SDispatcher(instruction: FusedNumericInstruction.I64Extend16SI) = dispatchInstruction(instruction, ::I64Extend16SExecutor)

fun I64Extend16SDispatcher(instruction: FusedNumericInstruction.I64Extend16SS) = dispatchInstruction(instruction, ::I64Extend16SExecutor)

fun I64Extend32SDispatcher(instruction: FusedNumericInstruction.I64Extend32SI) = dispatchInstruction(instruction, ::I64Extend32SExecutor)

fun I64Extend32SDispatcher(instruction: FusedNumericInstruction.I64Extend32SS) = dispatchInstruction(instruction, ::I64Extend32SExecutor)
