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
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqIs,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqSi,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32EqDispatcher(
    instruction: NumericSuperInstruction.I32EqSs,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeIi,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeIs,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeSi,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32NeDispatcher(
    instruction: NumericSuperInstruction.I32NeSs,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSIi,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSIs,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSSi,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtSDispatcher(
    instruction: NumericSuperInstruction.I32LtSSs,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUIi,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUIs,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUSi,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32LtUDispatcher(
    instruction: NumericSuperInstruction.I32LtUSs,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSIi,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSIs,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSSi,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtSDispatcher(
    instruction: NumericSuperInstruction.I32GtSSs,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUIi,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUIs,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUSi,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32GtUDispatcher(
    instruction: NumericSuperInstruction.I32GtUSs,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSIi,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSIs,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSSi,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeSDispatcher(
    instruction: NumericSuperInstruction.I32LeSSs,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUIi,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUIs,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUSi,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32LeUDispatcher(
    instruction: NumericSuperInstruction.I32LeUSs,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSIi,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSIs,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSSi,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeSDispatcher(
    instruction: NumericSuperInstruction.I32GeSSs,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUIi,
) = dispatchInstruction(instruction, ::I32GeUExecutor)

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUIs,
) = dispatchInstruction(instruction, ::I32GeUExecutor)

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUSi,
) = dispatchInstruction(instruction, ::I32GeUExecutor)

fun I32GeUDispatcher(
    instruction: NumericSuperInstruction.I32GeUSs,
) = dispatchInstruction(instruction, ::I32GeUExecutor)
