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
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64EqDispatcher(instruction: FusedNumericInstruction.I64EqIi) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64EqDispatcher(instruction: FusedNumericInstruction.I64EqIs) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64EqDispatcher(instruction: FusedNumericInstruction.I64EqSi) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64EqDispatcher(instruction: FusedNumericInstruction.I64EqSs) = dispatchInstruction(instruction, ::I64EqExecutor)

fun I64NeDispatcher(instruction: FusedNumericInstruction.I64NeIi) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64NeDispatcher(instruction: FusedNumericInstruction.I64NeIs) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64NeDispatcher(instruction: FusedNumericInstruction.I64NeSi) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64NeDispatcher(instruction: FusedNumericInstruction.I64NeSs) = dispatchInstruction(instruction, ::I64NeExecutor)

fun I64LtSDispatcher(instruction: FusedNumericInstruction.I64LtSIi) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtSDispatcher(instruction: FusedNumericInstruction.I64LtSIs) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtSDispatcher(instruction: FusedNumericInstruction.I64LtSSi) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtSDispatcher(instruction: FusedNumericInstruction.I64LtSSs) = dispatchInstruction(instruction, ::I64LtSExecutor)

fun I64LtUDispatcher(instruction: FusedNumericInstruction.I64LtUIi) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64LtUDispatcher(instruction: FusedNumericInstruction.I64LtUIs) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64LtUDispatcher(instruction: FusedNumericInstruction.I64LtUSi) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64LtUDispatcher(instruction: FusedNumericInstruction.I64LtUSs) = dispatchInstruction(instruction, ::I64LtUExecutor)

fun I64GtSDispatcher(instruction: FusedNumericInstruction.I64GtSIi) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtSDispatcher(instruction: FusedNumericInstruction.I64GtSIs) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtSDispatcher(instruction: FusedNumericInstruction.I64GtSSi) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtSDispatcher(instruction: FusedNumericInstruction.I64GtSSs) = dispatchInstruction(instruction, ::I64GtSExecutor)

fun I64GtUDispatcher(instruction: FusedNumericInstruction.I64GtUIi) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64GtUDispatcher(instruction: FusedNumericInstruction.I64GtUIs) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64GtUDispatcher(instruction: FusedNumericInstruction.I64GtUSi) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64GtUDispatcher(instruction: FusedNumericInstruction.I64GtUSs) = dispatchInstruction(instruction, ::I64GtUExecutor)

fun I64LeSDispatcher(instruction: FusedNumericInstruction.I64LeSIi) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeSDispatcher(instruction: FusedNumericInstruction.I64LeSIs) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeSDispatcher(instruction: FusedNumericInstruction.I64LeSSi) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeSDispatcher(instruction: FusedNumericInstruction.I64LeSSs) = dispatchInstruction(instruction, ::I64LeSExecutor)

fun I64LeUDispatcher(instruction: FusedNumericInstruction.I64LeUIi) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64LeUDispatcher(instruction: FusedNumericInstruction.I64LeUIs) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64LeUDispatcher(instruction: FusedNumericInstruction.I64LeUSi) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64LeUDispatcher(instruction: FusedNumericInstruction.I64LeUSs) = dispatchInstruction(instruction, ::I64LeUExecutor)

fun I64GeSDispatcher(instruction: FusedNumericInstruction.I64GeSIi) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeSDispatcher(instruction: FusedNumericInstruction.I64GeSIs) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeSDispatcher(instruction: FusedNumericInstruction.I64GeSSi) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeSDispatcher(instruction: FusedNumericInstruction.I64GeSSs) = dispatchInstruction(instruction, ::I64GeSExecutor)

fun I64GeUDispatcher(instruction: FusedNumericInstruction.I64GeUIi) = dispatchInstruction(instruction, ::I64GeUExecutor)

fun I64GeUDispatcher(instruction: FusedNumericInstruction.I64GeUIs) = dispatchInstruction(instruction, ::I64GeUExecutor)

fun I64GeUDispatcher(instruction: FusedNumericInstruction.I64GeUSi) = dispatchInstruction(instruction, ::I64GeUExecutor)

fun I64GeUDispatcher(instruction: FusedNumericInstruction.I64GeUSs) = dispatchInstruction(instruction, ::I64GeUExecutor)
