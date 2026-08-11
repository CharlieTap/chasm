package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused.ParametricSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instruction.ParametricSuperInstruction

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
            ParametricSuperInstruction.SelectIii(
                condition.sourceBits,
                first.sourceBits,
                second.sourceBits,
                destinationSlot,
            )
        }
        conditionImmediate && firstImmediate -> {
            ParametricSuperInstruction.SelectIis(
                condition.sourceBits,
                first.sourceBits,
                second.sourceSlot,
                destinationSlot,
            )
        }
        conditionImmediate && secondImmediate -> {
            ParametricSuperInstruction.SelectIsi(
                condition.sourceBits,
                first.sourceSlot,
                second.sourceBits,
                destinationSlot,
            )
        }
        conditionImmediate -> {
            ParametricSuperInstruction.SelectIss(
                condition.sourceBits,
                first.sourceSlot,
                second.sourceSlot,
                destinationSlot,
            )
        }
        firstImmediate && secondImmediate -> {
            ParametricSuperInstruction.SelectSii(
                condition.sourceSlot,
                first.sourceBits,
                second.sourceBits,
                destinationSlot,
            )
        }
        firstImmediate -> {
            ParametricSuperInstruction.SelectSis(
                condition.sourceSlot,
                first.sourceBits,
                second.sourceSlot,
                destinationSlot,
            )
        }
        secondImmediate -> {
            ParametricSuperInstruction.SelectSsi(
                condition.sourceSlot,
                first.sourceSlot,
                second.sourceBits,
                destinationSlot,
            )
        }
        else -> {
            ParametricSuperInstruction.SelectSss(
                condition.sourceSlot,
                first.sourceSlot,
                second.sourceSlot,
                destinationSlot,
            )
        }
    }
    emit(instruction, ::ParametricSuperInstructionDispatcher)
}
