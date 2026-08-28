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

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqIi) = dispatchInstruction { vstack, context -> I64EqExecutor(vstack, context, instruction) }

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqIs) = dispatchInstruction { vstack, context -> I64EqExecutor(vstack, context, instruction) }

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqSi) = dispatchInstruction { vstack, context -> I64EqExecutor(vstack, context, instruction) }

fun I64EqDispatcher(instruction: NumericSuperInstruction.I64EqSs) = dispatchInstruction { vstack, context -> I64EqExecutor(vstack, context, instruction) }

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeIi) = dispatchInstruction { vstack, context -> I64NeExecutor(vstack, context, instruction) }

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeIs) = dispatchInstruction { vstack, context -> I64NeExecutor(vstack, context, instruction) }

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeSi) = dispatchInstruction { vstack, context -> I64NeExecutor(vstack, context, instruction) }

fun I64NeDispatcher(instruction: NumericSuperInstruction.I64NeSs) = dispatchInstruction { vstack, context -> I64NeExecutor(vstack, context, instruction) }

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSIi) = dispatchInstruction { vstack, context -> I64LtSExecutor(vstack, context, instruction) }

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSIs) = dispatchInstruction { vstack, context -> I64LtSExecutor(vstack, context, instruction) }

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSSi) = dispatchInstruction { vstack, context -> I64LtSExecutor(vstack, context, instruction) }

fun I64LtSDispatcher(instruction: NumericSuperInstruction.I64LtSSs) = dispatchInstruction { vstack, context -> I64LtSExecutor(vstack, context, instruction) }

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUIi) = dispatchInstruction { vstack, context -> I64LtUExecutor(vstack, context, instruction) }

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUIs) = dispatchInstruction { vstack, context -> I64LtUExecutor(vstack, context, instruction) }

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUSi) = dispatchInstruction { vstack, context -> I64LtUExecutor(vstack, context, instruction) }

fun I64LtUDispatcher(instruction: NumericSuperInstruction.I64LtUSs) = dispatchInstruction { vstack, context -> I64LtUExecutor(vstack, context, instruction) }

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSIi) = dispatchInstruction { vstack, context -> I64GtSExecutor(vstack, context, instruction) }

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSIs) = dispatchInstruction { vstack, context -> I64GtSExecutor(vstack, context, instruction) }

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSSi) = dispatchInstruction { vstack, context -> I64GtSExecutor(vstack, context, instruction) }

fun I64GtSDispatcher(instruction: NumericSuperInstruction.I64GtSSs) = dispatchInstruction { vstack, context -> I64GtSExecutor(vstack, context, instruction) }

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUIi) = dispatchInstruction { vstack, context -> I64GtUExecutor(vstack, context, instruction) }

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUIs) = dispatchInstruction { vstack, context -> I64GtUExecutor(vstack, context, instruction) }

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUSi) = dispatchInstruction { vstack, context -> I64GtUExecutor(vstack, context, instruction) }

fun I64GtUDispatcher(instruction: NumericSuperInstruction.I64GtUSs) = dispatchInstruction { vstack, context -> I64GtUExecutor(vstack, context, instruction) }

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSIi) = dispatchInstruction { vstack, context -> I64LeSExecutor(vstack, context, instruction) }

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSIs) = dispatchInstruction { vstack, context -> I64LeSExecutor(vstack, context, instruction) }

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSSi) = dispatchInstruction { vstack, context -> I64LeSExecutor(vstack, context, instruction) }

fun I64LeSDispatcher(instruction: NumericSuperInstruction.I64LeSSs) = dispatchInstruction { vstack, context -> I64LeSExecutor(vstack, context, instruction) }

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUIi) = dispatchInstruction { vstack, context -> I64LeUExecutor(vstack, context, instruction) }

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUIs) = dispatchInstruction { vstack, context -> I64LeUExecutor(vstack, context, instruction) }

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUSi) = dispatchInstruction { vstack, context -> I64LeUExecutor(vstack, context, instruction) }

fun I64LeUDispatcher(instruction: NumericSuperInstruction.I64LeUSs) = dispatchInstruction { vstack, context -> I64LeUExecutor(vstack, context, instruction) }

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSIi) = dispatchInstruction { vstack, context -> I64GeSExecutor(vstack, context, instruction) }

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSIs) = dispatchInstruction { vstack, context -> I64GeSExecutor(vstack, context, instruction) }

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSSi) = dispatchInstruction { vstack, context -> I64GeSExecutor(vstack, context, instruction) }

fun I64GeSDispatcher(instruction: NumericSuperInstruction.I64GeSSs) = dispatchInstruction { vstack, context -> I64GeSExecutor(vstack, context, instruction) }

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUIi) = dispatchInstruction { vstack, context -> I64GeUExecutor(vstack, context, instruction) }

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUIs) = dispatchInstruction { vstack, context -> I64GeUExecutor(vstack, context, instruction) }

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUSi) = dispatchInstruction { vstack, context -> I64GeUExecutor(vstack, context, instruction) }

fun I64GeUDispatcher(instruction: NumericSuperInstruction.I64GeUSs) = dispatchInstruction { vstack, context -> I64GeUExecutor(vstack, context, instruction) }
