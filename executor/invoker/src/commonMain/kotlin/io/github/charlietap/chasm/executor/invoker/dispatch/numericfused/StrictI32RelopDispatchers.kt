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
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32EqDispatcher(
    instruction: FusedNumericInstruction.I32EqIi,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32EqDispatcher(
    instruction: FusedNumericInstruction.I32EqIs,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32EqDispatcher(
    instruction: FusedNumericInstruction.I32EqSi,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32EqDispatcher(
    instruction: FusedNumericInstruction.I32EqSs,
) = dispatchInstruction(instruction, ::I32EqExecutor)

fun I32NeDispatcher(
    instruction: FusedNumericInstruction.I32NeIi,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32NeDispatcher(
    instruction: FusedNumericInstruction.I32NeIs,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32NeDispatcher(
    instruction: FusedNumericInstruction.I32NeSi,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32NeDispatcher(
    instruction: FusedNumericInstruction.I32NeSs,
) = dispatchInstruction(instruction, ::I32NeExecutor)

fun I32LtSDispatcher(
    instruction: FusedNumericInstruction.I32LtSIi,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtSDispatcher(
    instruction: FusedNumericInstruction.I32LtSIs,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtSDispatcher(
    instruction: FusedNumericInstruction.I32LtSSi,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtSDispatcher(
    instruction: FusedNumericInstruction.I32LtSSs,
) = dispatchInstruction(instruction, ::I32LtSExecutor)

fun I32LtUDispatcher(
    instruction: FusedNumericInstruction.I32LtUIi,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32LtUDispatcher(
    instruction: FusedNumericInstruction.I32LtUIs,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32LtUDispatcher(
    instruction: FusedNumericInstruction.I32LtUSi,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32LtUDispatcher(
    instruction: FusedNumericInstruction.I32LtUSs,
) = dispatchInstruction(instruction, ::I32LtUExecutor)

fun I32GtSDispatcher(
    instruction: FusedNumericInstruction.I32GtSIi,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtSDispatcher(
    instruction: FusedNumericInstruction.I32GtSIs,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtSDispatcher(
    instruction: FusedNumericInstruction.I32GtSSi,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtSDispatcher(
    instruction: FusedNumericInstruction.I32GtSSs,
) = dispatchInstruction(instruction, ::I32GtSExecutor)

fun I32GtUDispatcher(
    instruction: FusedNumericInstruction.I32GtUIi,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32GtUDispatcher(
    instruction: FusedNumericInstruction.I32GtUIs,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32GtUDispatcher(
    instruction: FusedNumericInstruction.I32GtUSi,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32GtUDispatcher(
    instruction: FusedNumericInstruction.I32GtUSs,
) = dispatchInstruction(instruction, ::I32GtUExecutor)

fun I32LeSDispatcher(
    instruction: FusedNumericInstruction.I32LeSIi,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeSDispatcher(
    instruction: FusedNumericInstruction.I32LeSIs,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeSDispatcher(
    instruction: FusedNumericInstruction.I32LeSSi,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeSDispatcher(
    instruction: FusedNumericInstruction.I32LeSSs,
) = dispatchInstruction(instruction, ::I32LeSExecutor)

fun I32LeUDispatcher(
    instruction: FusedNumericInstruction.I32LeUIi,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32LeUDispatcher(
    instruction: FusedNumericInstruction.I32LeUIs,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32LeUDispatcher(
    instruction: FusedNumericInstruction.I32LeUSi,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32LeUDispatcher(
    instruction: FusedNumericInstruction.I32LeUSs,
) = dispatchInstruction(instruction, ::I32LeUExecutor)

fun I32GeSDispatcher(
    instruction: FusedNumericInstruction.I32GeSIi,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeSDispatcher(
    instruction: FusedNumericInstruction.I32GeSIs,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeSDispatcher(
    instruction: FusedNumericInstruction.I32GeSSi,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeSDispatcher(
    instruction: FusedNumericInstruction.I32GeSSs,
) = dispatchInstruction(instruction, ::I32GeSExecutor)

fun I32GeUDispatcher(
    instruction: FusedNumericInstruction.I32GeUIi,
) = dispatchInstruction(instruction, ::I32GeUExecutor)

fun I32GeUDispatcher(
    instruction: FusedNumericInstruction.I32GeUIs,
) = dispatchInstruction(instruction, ::I32GeUExecutor)

fun I32GeUDispatcher(
    instruction: FusedNumericInstruction.I32GeUSi,
) = dispatchInstruction(instruction, ::I32GeUExecutor)

fun I32GeUDispatcher(
    instruction: FusedNumericInstruction.I32GeUSs,
) = dispatchInstruction(instruction, ::I32GeUExecutor)
