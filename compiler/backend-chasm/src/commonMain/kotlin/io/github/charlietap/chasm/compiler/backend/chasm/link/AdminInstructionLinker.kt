package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.compiler.backend.chasm.ext.toRuntime
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotSequenceDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpConditionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpCopiesDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfCopyIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfCopySDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfZeroIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfZeroSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastFailIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastFailSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNonNullIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNonNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNullIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpTableSDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal fun linkAdminInstruction(
    instruction: AdminInstruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    686 -> {
        instruction as AdminInstruction.CopySlot
        CopySlotDispatcher(instruction.sourceSlot, instruction.destinationSlot)
    }
    692 -> {
        instruction as AdminInstruction.JumpIfS
        JumpIfSDispatcher(instruction.operandSlot, context.ip(instruction.target))
    }
    689 -> {
        instruction as AdminInstruction.Jump
        JumpDispatcher(context.ip(instruction.target))
    }
    697 -> {
        instruction as AdminInstruction.JumpIfCondition
        JumpConditionDispatcher(instruction.condition.toRuntime(), context.ip(instruction.target), branchOnMatch = true)
    }
    698 -> {
        instruction as AdminInstruction.JumpIfConditionMismatch
        JumpConditionDispatcher(instruction.condition.toRuntime(), context.ip(instruction.target), branchOnMatch = false)
    }
    688 -> {
        instruction as AdminInstruction.CopySlotSequence
        CopySlotSequenceDispatcher(instruction.sourceSlots, instruction.destinationSlots)
    }
    684 -> {
        instruction as AdminInstruction.EndFunction
        EndFunctionDispatcher(instruction.resultCount, instruction.activationHeaderSlot)
    }
    694 -> {
        instruction as AdminInstruction.JumpIfZeroS
        JumpIfZeroSDispatcher(instruction.operandSlot, context.ip(instruction.target))
    }
    696 -> {
        instruction as AdminInstruction.JumpIfCopyS
        JumpIfCopySDispatcher(
            instruction.operandSlot,
            instruction.sourceSlot,
            instruction.destinationSlot,
            context.ip(instruction.target),
        )
    }
    690 -> {
        instruction as AdminInstruction.JumpCopies
        JumpCopiesDispatcher(
            instruction.operands.toRuntime(),
            instruction.operands.destinationSlotBase,
            context.ip(instruction.target),
        )
    }
    687 -> {
        instruction as AdminInstruction.CopySlots
        CopySlotsDispatcher(instruction.sourceSlots, instruction.destinationSlots)
    }
    699 -> {
        instruction as AdminInstruction.JumpTableS
        val targets = instruction.targets
        JumpTableSDispatcher(instruction.operandSlot, IntArray(targets.size) { index -> context.ip(targets[index]) })
    }
    691 -> {
        instruction as AdminInstruction.JumpIfI
        JumpIfIDispatcher(instruction.operand, context.ip(instruction.target))
    }
    693 -> {
        instruction as AdminInstruction.JumpIfZeroI
        JumpIfZeroIDispatcher(instruction.operand, context.ip(instruction.target))
    }
    695 -> {
        instruction as AdminInstruction.JumpIfCopyI
        JumpIfCopyIDispatcher(
            instruction.operand,
            instruction.sourceSlot,
            instruction.destinationSlot,
            context.ip(instruction.target),
        )
    }
    701 -> {
        instruction as AdminInstruction.JumpOnNullS
        JumpOnNullSDispatcher(instruction.operandSlot, context.ip(instruction.target))
    }
    703 -> {
        instruction as AdminInstruction.JumpOnNonNullS
        JumpOnNonNullSDispatcher(instruction.operandSlot, context.ip(instruction.target))
    }
    705 -> {
        instruction as AdminInstruction.JumpOnCastS
        JumpOnCastSDispatcher(instruction.operandSlot, context.ip(instruction.target), context.typeTest(instruction.typeTest))
    }
    707 -> {
        instruction as AdminInstruction.JumpOnCastFailS
        JumpOnCastFailSDispatcher(instruction.operandSlot, context.ip(instruction.target), context.typeTest(instruction.typeTest))
    }
    700 -> {
        instruction as AdminInstruction.JumpOnNullI
        JumpOnNullIDispatcher(instruction.operand, context.ip(instruction.target))
    }
    702 -> {
        instruction as AdminInstruction.JumpOnNonNullI
        JumpOnNonNullIDispatcher(instruction.operand, context.ip(instruction.target))
    }
    704 -> {
        instruction as AdminInstruction.JumpOnCastI
        JumpOnCastIDispatcher(instruction.operand, context.ip(instruction.target), context.typeTest(instruction.typeTest))
    }
    706 -> {
        instruction as AdminInstruction.JumpOnCastFailI
        JumpOnCastFailIDispatcher(instruction.operand, context.ip(instruction.target), context.typeTest(instruction.typeTest))
    }
    685 -> {
        instruction as AdminInstruction.Checkpoint
        checkNotNull(CheckpointLinker(context)) {
            "checkpoint links to nothing in this store"
        }
    }
    else -> error("opcode $opcode is not an admin instruction")
}
