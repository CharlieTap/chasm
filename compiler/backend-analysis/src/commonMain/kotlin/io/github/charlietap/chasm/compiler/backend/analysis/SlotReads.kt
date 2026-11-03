package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.BulkMemory
import io.github.charlietap.chasm.compiler.backend.analysis.form.ConditionForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperArgument
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperCall
import io.github.charlietap.chasm.compiler.backend.analysis.form.NumericForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.compiler.backend.analysis.form.bulk
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.form
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.compiler.backend.analysis.form.operands
import io.github.charlietap.chasm.compiler.backend.analysis.form.select
import io.github.charlietap.chasm.compiler.backend.analysis.form.tailCallArguments

interface SlotReadVisitor {

    fun read(
        slot: Int,
    )
}

fun visitSlotReads(
    instruction: Instruction,
    declarations: ModuleIndexSpaces,
    visitor: SlotReadVisitor,
): Boolean = when (instruction) {
    is NumericInstruction -> {
        visitNumericReads(instruction.form(), visitor)
        true
    }
    is MemoryInstruction -> visitMemoryReads(instruction, visitor)
    is ParametricInstruction -> {
        val select = instruction.select()
        visitor.operand(select.condition)
        visitor.operand(select.first)
        visitor.operand(select.second)
        true
    }
    is AdminInstruction -> {
        visitAdminReads(instruction, visitor)
        true
    }
    is VariableInstruction -> {
        if (instruction is VariableInstruction.GlobalSetS) visitor.read(instruction.sourceSlot)
        true
    }
    is ControlInstruction -> {
        visitControlReads(instruction, declarations, visitor)
        true
    }
    is TableInstruction -> instruction.helperCall()?.let { call -> visitor.helper(call) } ?: false
    is ReferenceInstruction,
    is AggregateInstruction,
    -> false
}

private fun SlotReadVisitor.operand(
    operand: Operand,
) {
    if (operand is Operand.Slot) read(operand.slot)
}

private fun SlotReadVisitor.helper(
    call: HelperCall,
): Boolean {
    for (argument in call.arguments) {
        when (argument) {
            is HelperArgument.I32 -> operand(argument.operand)
            is HelperArgument.Encoded -> operand(argument.operand)
            else -> Unit
        }
    }
    return true
}

private fun visitNumericReads(
    form: NumericForm,
    visitor: SlotReadVisitor,
) {
    when (form) {
        is NumericForm.Constant -> Unit
        is NumericForm.BitFieldExtract -> visitor.operand(form.operand)
        is NumericForm.Unary -> visitor.operand(form.operand)
        is NumericForm.Binary -> {
            visitor.operand(form.left)
            visitor.operand(form.right)
        }
        is NumericForm.Compare -> {
            visitor.operand(form.left)
            visitor.operand(form.right)
        }
        is NumericForm.Convert -> visitor.operand(form.operand)
        is NumericForm.Wide -> form.operands.forEach(visitor::operand)
    }
}

private fun visitMemoryReads(
    instruction: MemoryInstruction,
    visitor: SlotReadVisitor,
): Boolean {
    val access = instruction.access()
    if (access != null) {
        visitor.operand(access.address)
        access.value?.let(visitor::operand)
        return true
    }
    when (val bulk = instruction.bulk()) {
        is BulkMemory.Copy -> {
            visitor.operand(bulk.count)
            visitor.operand(bulk.sourceOffset)
            visitor.operand(bulk.destinationOffset)
            return true
        }
        is BulkMemory.Fill -> {
            visitor.operand(bulk.count)
            visitor.operand(bulk.value)
            visitor.operand(bulk.offset)
            return true
        }
        null -> Unit
    }
    return instruction.helperCall()?.let { call -> visitor.helper(call) } ?: false
}

private fun visitAdminReads(
    instruction: AdminInstruction,
    visitor: SlotReadVisitor,
) {
    when (instruction) {
        is AdminInstruction.EndFunction -> for (slot in 0 until instruction.resultCount) visitor.read(slot)
        is AdminInstruction.CopySlot -> visitor.read(instruction.sourceSlot)
        is AdminInstruction.CopySlots -> instruction.sourceSlots.forEach(visitor::read)
        is AdminInstruction.CopySlotSequence -> instruction.sourceSlots.forEach(visitor::read)
        is AdminInstruction.Checkpoint -> Unit
        is AdminInstruction.Jump -> Unit
        is AdminInstruction.JumpCopies -> instruction.operands.operands().forEach(visitor::operand)
        is AdminInstruction.JumpIfI -> Unit
        is AdminInstruction.JumpIfS -> visitor.read(instruction.operandSlot)
        is AdminInstruction.JumpIfZeroI -> Unit
        is AdminInstruction.JumpIfZeroS -> visitor.read(instruction.operandSlot)
        is AdminInstruction.JumpIfCopyI -> visitor.read(instruction.sourceSlot)
        is AdminInstruction.JumpIfCopyS -> {
            visitor.read(instruction.operandSlot)
            visitor.read(instruction.sourceSlot)
        }
        is AdminInstruction.JumpIfCondition -> visitor.condition(instruction.condition.form())
        is AdminInstruction.JumpIfConditionMismatch -> visitor.condition(instruction.condition.form())
        is AdminInstruction.JumpTableS -> visitor.read(instruction.operandSlot)
        is AdminInstruction.JumpOnNullI -> Unit
        is AdminInstruction.JumpOnNullS -> visitor.read(instruction.operandSlot)
        is AdminInstruction.JumpOnNonNullI -> Unit
        is AdminInstruction.JumpOnNonNullS -> visitor.read(instruction.operandSlot)
        is AdminInstruction.JumpOnCastI -> Unit
        is AdminInstruction.JumpOnCastS -> visitor.read(instruction.operandSlot)
        is AdminInstruction.JumpOnCastFailI -> Unit
        is AdminInstruction.JumpOnCastFailS -> visitor.read(instruction.operandSlot)
    }
}

private fun SlotReadVisitor.condition(
    form: ConditionForm,
) {
    when (form) {
        is ConditionForm.Compare -> {
            operand(form.left)
            operand(form.right)
        }
        is ConditionForm.And -> {
            operand(form.left)
            operand(form.right)
        }
    }
}

private fun visitControlReads(
    instruction: ControlInstruction,
    declarations: ModuleIndexSpaces,
    visitor: SlotReadVisitor,
) {
    instruction.callSite(declarations)?.arguments?.forEach(visitor::operand)
    instruction.tailCallArguments()?.forEach(visitor::operand)
    when (instruction) {
        is ControlInstruction.CallIndirectS -> visitor.read(instruction.elementIndexSlot)
        is ControlInstruction.ReturnCallIndirectS -> visitor.read(instruction.elementIndexSlot)
        is ControlInstruction.CallRefS -> visitor.read(instruction.functionSlot)
        is ControlInstruction.ReturnCallRefS -> visitor.read(instruction.functionSlot)
        is ControlInstruction.FunctionReturn -> instruction.results.operands().forEach(visitor::operand)
        is ControlInstruction.Throw -> {
            val fields = declarations.tagFieldCount(instruction.exceptionTag)
            for (slot in instruction.firstPayloadSlot until instruction.firstPayloadSlot + fields) visitor.read(slot)
        }
        is ControlInstruction.ThrowRefS -> visitor.read(instruction.exceptionSlot)
        else -> Unit
    }
}
