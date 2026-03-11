package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32ClzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32CtzExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32Extend16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32Extend8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32PopcntExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I32ClzDispatcher(instruction: NumericSuperInstruction.I32ClzI) = dispatchInstruction(instruction, ::I32ClzExecutor)

fun I32ClzDispatcher(instruction: NumericSuperInstruction.I32ClzS) = dispatchInstruction(instruction, ::I32ClzExecutor)

fun I32CtzDispatcher(instruction: NumericSuperInstruction.I32CtzI) = dispatchInstruction(instruction, ::I32CtzExecutor)

fun I32CtzDispatcher(instruction: NumericSuperInstruction.I32CtzS) = dispatchInstruction(instruction, ::I32CtzExecutor)

fun I32PopcntDispatcher(instruction: NumericSuperInstruction.I32PopcntI) = dispatchInstruction(instruction, ::I32PopcntExecutor)

fun I32PopcntDispatcher(instruction: NumericSuperInstruction.I32PopcntS) = dispatchInstruction(instruction, ::I32PopcntExecutor)

fun I32Extend8SDispatcher(instruction: NumericSuperInstruction.I32Extend8SI) = dispatchInstruction(instruction, ::I32Extend8SExecutor)

fun I32Extend8SDispatcher(instruction: NumericSuperInstruction.I32Extend8SS) = dispatchInstruction(instruction, ::I32Extend8SExecutor)

fun I32Extend16SDispatcher(instruction: NumericSuperInstruction.I32Extend16SI) = dispatchInstruction(instruction, ::I32Extend16SExecutor)

fun I32Extend16SDispatcher(instruction: NumericSuperInstruction.I32Extend16SS) = dispatchInstruction(instruction, ::I32Extend16SExecutor)
