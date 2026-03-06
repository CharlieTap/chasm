package io.github.charlietap.chasm.predecoder.instruction.parametricfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused.SelectDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.FusedParametricInstruction as RuntimeFusedParametricInstruction

internal fun FusedParametricInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedParametricInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedParametricInstruction.Select -> strictSelectInstruction(instruction)
    }
}

private fun strictSelectInstruction(
    instruction: FusedParametricInstruction.Select,
): DispatchableInstruction {
    val destinationSlot =
        strictParametricDestinationSlot(instruction.destination)
            ?: return unsupportedUnloweredParametricInstruction()

    val conditionImmediate = strictParametricImmediate(instruction.const)
    val conditionSlot = strictParametricOperandSlot(instruction.const)
    val val1Immediate = strictParametricImmediate(instruction.val1)
    val val1Slot = strictParametricOperandSlot(instruction.val1)
    val val2Immediate = strictParametricImmediate(instruction.val2)
    val val2Slot = strictParametricOperandSlot(instruction.val2)

    return when {
        conditionImmediate != null && val1Immediate != null && val2Immediate != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectIii(
                    condition = conditionImmediate,
                    val1 = val1Immediate,
                    val2 = val2Immediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionImmediate != null && val1Immediate != null && val2Slot != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectIis(
                    condition = conditionImmediate,
                    val1 = val1Immediate,
                    val2Slot = val2Slot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionImmediate != null && val1Slot != null && val2Immediate != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectIsi(
                    condition = conditionImmediate,
                    val1Slot = val1Slot,
                    val2 = val2Immediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionImmediate != null && val1Slot != null && val2Slot != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectIss(
                    condition = conditionImmediate,
                    val1Slot = val1Slot,
                    val2Slot = val2Slot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionSlot != null && val1Immediate != null && val2Immediate != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectSii(
                    conditionSlot = conditionSlot,
                    val1 = val1Immediate,
                    val2 = val2Immediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionSlot != null && val1Immediate != null && val2Slot != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectSis(
                    conditionSlot = conditionSlot,
                    val1 = val1Immediate,
                    val2Slot = val2Slot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionSlot != null && val1Slot != null && val2Immediate != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectSsi(
                    conditionSlot = conditionSlot,
                    val1Slot = val1Slot,
                    val2 = val2Immediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        conditionSlot != null && val1Slot != null && val2Slot != null -> {
            SelectDispatcher(
                RuntimeFusedParametricInstruction.SelectSss(
                    conditionSlot = conditionSlot,
                    val1Slot = val1Slot,
                    val2Slot = val2Slot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        else -> unsupportedUnloweredParametricInstruction()
    }
}

private fun strictParametricImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun strictParametricOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictParametricDestinationSlot(
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> destination.index.idx
    else -> null
}

private fun unsupportedUnloweredParametricInstruction(): DispatchableInstruction =
    error("parametric fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
