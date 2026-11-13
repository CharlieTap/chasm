package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.CAST_MATCHES
import io.github.charlietap.chasm.compiler.backend.abi.FUEL_AND_INTERRUPT_CHECK
import io.github.charlietap.chasm.compiler.backend.abi.FUEL_CHECK
import io.github.charlietap.chasm.compiler.backend.abi.INTERRUPT_CHECK
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.operands
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion

internal fun lowerAdminInstruction(
    instruction: AdminInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is AdminInstruction.EndFunction -> context.ret()
        AdminInstruction.Checkpoint -> context.checkpoint()
        is AdminInstruction.CopySlot -> context.copyToSlot(instruction.destinationSlot, slot(instruction.sourceSlot))
        is AdminInstruction.CopySlots ->
            context.parallelCopy(instruction.sourceSlots.map(::slot), instruction.destinationSlots.asList())
        is AdminInstruction.CopySlotSequence -> instruction.sourceSlots.indices.forEach { index ->
            context.copyToSlot(instruction.destinationSlots[index], slot(instruction.sourceSlots[index]))
        }
        is AdminInstruction.Jump -> context.jump(instruction.target)
        is AdminInstruction.JumpCopies -> {
            context.transfer(instruction.operands.operands(), instruction.operands.destinationSlotBase)
            context.jump(instruction.target)
        }
        is AdminInstruction.JumpIfI -> if (instruction.operand != 0L) context.jump(instruction.target)
        is AdminInstruction.JumpIfZeroI -> if (instruction.operand == 0L) context.jump(instruction.target)
        is AdminInstruction.JumpIfS -> context.branchOnSlot(instruction.operandSlot, Cond.Ne, context.label(instruction.target))
        is AdminInstruction.JumpIfZeroS -> context.branchOnSlot(instruction.operandSlot, Cond.Eq, context.label(instruction.target))
        is AdminInstruction.JumpIfCopyI -> if (instruction.operand != 0L) {
            context.copyToSlot(instruction.destinationSlot, slot(instruction.sourceSlot))
            context.jump(instruction.target)
        }
        is AdminInstruction.JumpIfCopyS -> {
            val skip = context.emit.freshLabel()
            context.branchOnSlot(instruction.operandSlot, Cond.Eq, skip)
            context.copyToSlot(instruction.destinationSlot, slot(instruction.sourceSlot))
            context.jump(instruction.target)
            context.emit.place(skip)
        }
        is AdminInstruction.JumpIfCondition -> context.lowerCondition(instruction.condition, holds = true, context.label(instruction.target))
        is AdminInstruction.JumpIfConditionMismatch ->
            context.lowerCondition(instruction.condition, holds = false, context.label(instruction.target))
        is AdminInstruction.JumpTableS -> context.jumpTable(instruction.operandSlot, instruction.targets)
        is AdminInstruction.JumpOnNullI -> context.nullBranch(immediate(instruction.operand), isNull = true, instruction.target)
        is AdminInstruction.JumpOnNullS -> context.nullBranch(slot(instruction.operandSlot), isNull = true, instruction.target)
        is AdminInstruction.JumpOnNonNullI -> context.nullBranch(immediate(instruction.operand), isNull = false, instruction.target)
        is AdminInstruction.JumpOnNonNullS -> context.nullBranch(slot(instruction.operandSlot), isNull = false, instruction.target)
        is AdminInstruction.JumpOnCastI -> context.castBranch(immediate(instruction.operand), instruction.typeTest, matches = true, instruction.target)
        is AdminInstruction.JumpOnCastS -> context.castBranch(slot(instruction.operandSlot), instruction.typeTest, matches = true, instruction.target)
        is AdminInstruction.JumpOnCastFailI ->
            context.castBranch(immediate(instruction.operand), instruction.typeTest, matches = false, instruction.target)
        is AdminInstruction.JumpOnCastFailS ->
            context.castBranch(slot(instruction.operandSlot), instruction.typeTest, matches = false, instruction.target)
    }
}

private fun LowerContext.jump(
    target: InstructionIndex,
) = emit.gotoLabel(label(target))

private fun LowerContext.checkpoint() {
    val options = module.options
    when {
        options.meterFuel && options.interruptible -> emit.invoke(FUEL_AND_INTERRUPT_CHECK, Registers.CONTEXT)
        options.meterFuel -> emit.invoke(FUEL_CHECK, Registers.CONTEXT)
        options.interruptible -> emit.invoke(INTERRUPT_CHECK, Registers.CONTEXT)
    }
}

private fun LowerContext.jumpTable(
    operandSlot: Int,
    targets: IntArray,
) {
    loadInt(Registers.I0, Registers.W0, Operand.Slot(operandSlot))
    val chunk = chunk
    val cases = targets.copyOf(targets.size - 1)
    val dynamic = chunk != null && (cases.size > LARGE_JUMP_TABLE || cases.count { target -> target !in chunk } > CHUNK_TABLE_EXITS)
    if (!dynamic) {
        if (cases.isNotEmpty()) emit.packedSwitch(Registers.I0, 0, cases.map(::label))
        return emit.gotoLabel(label(targets.last()))
    }
    val elsewhere = emit.freshLabel()
    if (cases.size > LARGE_JUMP_TABLE) {
        emit.const(Registers.I2, Int.MIN_VALUE)
        emit.binary(BinaryOp.AddInt, Registers.I1, Registers.I0, Registers.I2)
        emit.const(Registers.I3, cases.size xor Int.MIN_VALUE)
        emit.branchCompare(Cond.Lt, Registers.I1, Registers.I3, elsewhere)
        emit.gotoLabel(label(targets.last()))
    } else {
        emit.packedSwitch(Registers.I0, 0, cases.map { target -> if (target in checkNotNull(chunk)) label(target) else elsewhere })
        emit.gotoLabel(label(targets.last()))
    }
    outOfLine += {
        emit.place(elsewhere)
        loadObject(Registers.OBJECT, Resource.Longs(LongArray(cases.size) { index -> cases[index].toLong() }), "[J")
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I0)
        emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
        checkNotNull(chunk).leavesAnywhere = true
        emit.gotoLabel(SPILL)
    }
}

private const val CHUNK_TABLE_EXITS = 16

private const val LARGE_JUMP_TABLE = 1_024

private fun LowerContext.nullBranch(
    reference: Operand,
    isNull: Boolean,
    target: InstructionIndex,
) {
    loadWide(Registers.W0, reference)
    branchOnNull(Registers.W0, isNull, label(target))
}

private fun LowerContext.castBranch(
    reference: Operand,
    type: ReferenceType,
    matches: Boolean,
    target: InstructionIndex,
) {
    loadWide(Registers.W0, reference)
    loadObject(Registers.OBJECT, Resource.TypeTest(type), OBJECT_TYPE)
    emit.invoke(CAST_MATCHES, Registers.CONTEXT, Registers.W0, Registers.W0 + 1, Registers.OBJECT)
    emit.moveResult(Registers.I0)
    emit.branchZero(if (matches) Cond.Ne else Cond.Eq, Registers.I0, label(target))
}
