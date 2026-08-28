package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32NeExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqIi,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqIs,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqSi,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqSs,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeIi,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeIs,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeSi,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeSs,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSIi,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSIs,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSSi,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSSs,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUIi,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUIs,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUSi,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUSs,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSIi,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSIs,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSSi,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSSs,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUIi,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUIs,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUSi,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUSs,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSIi,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSIs,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSSi,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSSs,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUIi,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUIs,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUSi,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUSs,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSIi,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSIs,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSSi,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSSs,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUIi,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUIs,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUSi,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUSs,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }
