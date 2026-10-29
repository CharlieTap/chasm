package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.CAST_MATCHES
import io.github.charlietap.chasm.compiler.backend.abi.FUEL_AND_INTERRUPT_CHECK
import io.github.charlietap.chasm.compiler.backend.abi.FUEL_CHECK
import io.github.charlietap.chasm.compiler.backend.abi.INTERRUPT_CHECK
import io.github.charlietap.chasm.compiler.backend.abi.IS_NULL
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.immediate
import io.github.charlietap.chasm.compiler.backend.analysis.form.operands
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

internal fun lowerAdminInstruction(
    instruction: AdminInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is AdminInstruction.EndFunction -> context.ret()
        AdminInstruction.Checkpoint -> context.checkpoint()
        is AdminInstruction.CopySlot -> context.copyToSlot(instruction.destinationSlot, slot(instruction.sourceSlot))
        is AdminInstruction.CopySlots -> context.parallelCopy(instruction.sourceSlots.map(::slot), instruction.destinationSlots.asList())
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
        is AdminInstruction.JumpIfS -> context.branchOnInt(slot(instruction.operandSlot), zero = false, context.label(instruction.target))
        is AdminInstruction.JumpIfZeroS -> context.branchOnInt(slot(instruction.operandSlot), zero = true, context.label(instruction.target))
        is AdminInstruction.JumpIfCopyI -> if (instruction.operand != 0L) {
            context.copyToSlot(instruction.destinationSlot, slot(instruction.sourceSlot))
            context.jump(instruction.target)
        }
        is AdminInstruction.JumpIfCopyS -> {
            val skip = context.emit.label()
            context.branchOnInt(slot(instruction.operandSlot), zero = true, skip)
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
) = emit.jump(Opcodes.GOTO, label(target))

private fun LowerContext.checkpoint() {
    val options = module.options
    val check = when {
        options.meterFuel && options.interruptible -> FUEL_AND_INTERRUPT_CHECK
        options.meterFuel -> FUEL_CHECK
        options.interruptible -> INTERRUPT_CHECK
        else -> return
    }
    pushContext()
    emit.invokeStatic(check)
}

private fun LowerContext.jumpTable(
    operandSlot: Int,
    targets: IntArray,
) {
    val default = label(targets.last())
    if (targets.size == 1) return emit.jump(Opcodes.GOTO, default)
    val cases = targets.size - 1
    val chunk = chunk
    val outside = if (chunk == null) 0 else targets.count { it !in chunk }
    if (cases > LARGE_JUMP_TABLE || outside > CHUNK_TABLE_EXITS) return largeJumpTable(operandSlot, targets)
    pushInt(Operand.Slot(operandSlot))
    emit.tableSwitch(0, default, Array(cases) { index -> label(targets[index]) })
}

private fun LowerContext.largeJumpTable(
    operandSlot: Int,
    targets: IntArray,
) {
    val default = label(targets.last())
    val cases = targets.size - 1
    pushInt(Operand.Slot(operandSlot))
    emit.store(Opcodes.ISTORE, locals.scratchInt)
    flipped { emit.load(Opcodes.ILOAD, locals.scratchInt) }
    emit.int(cases xor Int.MIN_VALUE)
    emit.jump(Opcodes.IF_ICMPGE, default)
    pushResource(Resource.Longs(LongArray(cases) { index -> targets[index].toLong() }))
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    emit.insn(Opcodes.LALOAD)
    emit.insn(Opcodes.L2I)
    val positions = targets.copyOf(cases).distinct().sorted()
    val chunk = chunk
    if (chunk == null) {
        return emit.lookupSwitch(default, positions.toIntArray(), Array(positions.size) { index -> label(positions[index]) })
    }
    val held = positions.filter { it in chunk }.toIntArray()
    val elsewhere = emit.label()
    emit.insn(Opcodes.DUP)
    emit.store(Opcodes.ISTORE, locals.exitPosition)
    emit.lookupSwitch(elsewhere, held, Array(held.size) { index -> label(held[index]) })
    emit.place(elsewhere)
    emit.int(Chunk.DYNAMIC_EXIT_BASE)
    emit.load(Opcodes.ILOAD, locals.exitPosition)
    emit.insn(Opcodes.ISUB)
    emit.store(Opcodes.ISTORE, locals.exitPosition)
    emit.jump(Opcodes.GOTO, chunk.spill)
    chunk.leavesAnywhere = true
    positions.filterTo(chunk.dynamicExits) { it !in chunk }
}

private const val LARGE_JUMP_TABLE = 1_024

private const val CHUNK_TABLE_EXITS = 64

private fun LowerContext.nullBranch(
    reference: Operand,
    isNull: Boolean,
    target: InstructionIndex,
) {
    pushLong(reference)
    emit.invokeStatic(IS_NULL)
    emit.jump(if (isNull) Opcodes.IFNE else Opcodes.IFEQ, label(target))
}

private fun LowerContext.castBranch(
    reference: Operand,
    type: ReferenceType,
    matches: Boolean,
    target: InstructionIndex,
) {
    pushContext()
    pushLong(reference)
    pushResource(Resource.TypeTest(type))
    emit.invokeStatic(CAST_MATCHES)
    emit.jump(if (matches) Opcodes.IFNE else Opcodes.IFEQ, label(target))
}

internal fun LowerContext.branchTo(
    label: Label,
) = emit.jump(Opcodes.GOTO, label)
