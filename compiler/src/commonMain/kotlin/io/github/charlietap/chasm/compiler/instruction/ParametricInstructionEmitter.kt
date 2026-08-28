package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.parametric.ParametricInstructionDispatcher
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

internal fun FunctionCompilationContext.emitSelect(
    condition: OperandSource,
    first: OperandSource,
    second: OperandSource,
    destinationSlot: Int,
) {
    val conditionImmediate = condition.isImmediate
    val firstImmediate = first.isImmediate
    val secondImmediate = second.isImmediate

    val instruction = when {
        conditionImmediate && firstImmediate && secondImmediate -> {
            ParametricInstruction.SelectIii(
                condition.sourceBits,
                first.sourceBits,
                second.sourceBits,
                destinationSlot,
            )
        }
        conditionImmediate && firstImmediate -> {
            ParametricInstruction.SelectIis(
                condition.sourceBits,
                first.sourceBits,
                second.sourceSlot,
                destinationSlot,
            )
        }
        conditionImmediate && secondImmediate -> {
            ParametricInstruction.SelectIsi(
                condition.sourceBits,
                first.sourceSlot,
                second.sourceBits,
                destinationSlot,
            )
        }
        conditionImmediate -> {
            ParametricInstruction.SelectIss(
                condition.sourceBits,
                first.sourceSlot,
                second.sourceSlot,
                destinationSlot,
            )
        }
        firstImmediate && secondImmediate -> {
            ParametricInstruction.SelectSii(
                condition.sourceSlot,
                first.sourceBits,
                second.sourceBits,
                destinationSlot,
            )
        }
        firstImmediate -> {
            ParametricInstruction.SelectSis(
                condition.sourceSlot,
                first.sourceBits,
                second.sourceSlot,
                destinationSlot,
            )
        }
        secondImmediate -> {
            ParametricInstruction.SelectSsi(
                condition.sourceSlot,
                first.sourceSlot,
                second.sourceBits,
                destinationSlot,
            )
        }
        else -> {
            ParametricInstruction.SelectSss(
                condition.sourceSlot,
                first.sourceSlot,
                second.sourceSlot,
                destinationSlot,
            )
        }
    }
    emit(instruction, ::ParametricInstructionDispatcher)
}
