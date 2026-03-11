package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I64PopcntExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I64ClzDispatcher(instruction: NumericSuperInstruction.I64ClzI) = dispatchInstruction(instruction, ::I64ClzExecutor)

fun I64ClzDispatcher(instruction: NumericSuperInstruction.I64ClzS) = dispatchInstruction(instruction, ::I64ClzExecutor)

fun I64CtzDispatcher(instruction: NumericSuperInstruction.I64CtzI) = dispatchInstruction(instruction, ::I64CtzExecutor)

fun I64CtzDispatcher(instruction: NumericSuperInstruction.I64CtzS) = dispatchInstruction(instruction, ::I64CtzExecutor)

fun I64PopcntDispatcher(instruction: NumericSuperInstruction.I64PopcntI) = dispatchInstruction(instruction, ::I64PopcntExecutor)

fun I64PopcntDispatcher(instruction: NumericSuperInstruction.I64PopcntS) = dispatchInstruction(instruction, ::I64PopcntExecutor)

fun I64Extend8SDispatcher(instruction: NumericSuperInstruction.I64Extend8SI) = dispatchInstruction(instruction, ::I64Extend8SExecutor)

fun I64Extend8SDispatcher(instruction: NumericSuperInstruction.I64Extend8SS) = dispatchInstruction(instruction, ::I64Extend8SExecutor)

fun I64Extend16SDispatcher(instruction: NumericSuperInstruction.I64Extend16SI) = dispatchInstruction(instruction, ::I64Extend16SExecutor)

fun I64Extend16SDispatcher(instruction: NumericSuperInstruction.I64Extend16SS) = dispatchInstruction(instruction, ::I64Extend16SExecutor)

fun I64Extend32SDispatcher(instruction: NumericSuperInstruction.I64Extend32SI) = dispatchInstruction(instruction, ::I64Extend32SExecutor)

fun I64Extend32SDispatcher(instruction: NumericSuperInstruction.I64Extend32SS) = dispatchInstruction(instruction, ::I64Extend32SExecutor)
