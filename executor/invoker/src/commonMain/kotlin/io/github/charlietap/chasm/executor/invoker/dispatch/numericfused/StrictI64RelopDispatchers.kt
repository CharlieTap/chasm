package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64NeExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqIi) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqIs) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqSi) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqSs) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeIi) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeIs) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeSi) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeSs) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSIi) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSIs) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSSi) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSSs) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUIi) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUIs) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUSi) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUSs) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSIi) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSIs) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSSi) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSSs) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUIi) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUIs) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUSi) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUSs) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSIi) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSIs) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSSi) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSSs) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUIi) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUIs) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUSi) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUSs) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSIi) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSIs) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSSi) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSSs) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUIi) = dispatchInstruction(instruction, ::I64GeUExecutor)

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUIs) = dispatchInstruction(instruction, ::I64GeUExecutor)

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUSi) = dispatchInstruction(instruction, ::I64GeUExecutor)

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUSs) = dispatchInstruction(instruction, ::I64GeUExecutor)
