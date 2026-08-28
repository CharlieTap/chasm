package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32EqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtSExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtUExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32NeExecutor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32EqDispatcher(
    instruction: NumericInstruction.I32EqIi,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32EqDispatcher(
    instruction: NumericInstruction.I32EqIs,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32EqDispatcher(
    instruction: NumericInstruction.I32EqSi,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32EqDispatcher(
    instruction: NumericInstruction.I32EqSs,
) = dispatchInstruction { vstack, context -> I32EqExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericInstruction.I32NeIi,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericInstruction.I32NeIs,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericInstruction.I32NeSi,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32NeDispatcher(
    instruction: NumericInstruction.I32NeSs,
) = dispatchInstruction { vstack, context -> I32NeExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtSIi,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtSIs,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtSSi,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtSDispatcher(
    instruction: NumericInstruction.I32LtSSs,
) = dispatchInstruction { vstack, context -> I32LtSExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtUIi,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtUIs,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtUSi,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtUSs,
) = dispatchInstruction { vstack, context -> I32LtUExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtSIi,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtSIs,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtSSi,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtSSs,
) = dispatchInstruction { vstack, context -> I32GtSExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtUIi,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtUIs,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtUSi,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtUSs,
) = dispatchInstruction { vstack, context -> I32GtUExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeSIi,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeSIs,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeSSi,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeSSs,
) = dispatchInstruction { vstack, context -> I32LeSExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericInstruction.I32LeUIi,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericInstruction.I32LeUIs,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericInstruction.I32LeUSi,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32LeUDispatcher(
    instruction: NumericInstruction.I32LeUSs,
) = dispatchInstruction { vstack, context -> I32LeUExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeSIi,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeSIs,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeSSi,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeSSs,
) = dispatchInstruction { vstack, context -> I32GeSExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeUIi,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeUIs,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeUSi,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }

fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeUSs,
) = dispatchInstruction { vstack, context -> I32GeUExecutor(vstack, context, instruction) }
