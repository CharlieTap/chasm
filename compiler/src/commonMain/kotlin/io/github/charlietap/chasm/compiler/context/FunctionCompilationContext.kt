package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.compiler.emptyIntArray
import io.github.charlietap.chasm.compiler.instruction.DeferredBranchPaths
import io.github.charlietap.chasm.compiler.instruction.emitCopies
import io.github.charlietap.chasm.compiler.instruction.emitCopy
import io.github.charlietap.chasm.compiler.instruction.emitF32Constant
import io.github.charlietap.chasm.compiler.instruction.emitF64Constant
import io.github.charlietap.chasm.compiler.instruction.emitI32Constant
import io.github.charlietap.chasm.compiler.instruction.emitI64Constant
import io.github.charlietap.chasm.compiler.operand.FrameAllocator
import io.github.charlietap.chasm.compiler.operand.FunctionFrameLayout
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.f32Immediate
import io.github.charlietap.chasm.compiler.operand.f64Immediate
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.compiler.program.ProgramBuilder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.type.ValueType

internal class FunctionCompilationContext(
    val compiler: CompilerContext,
    val layout: FunctionFrameLayout,
    val frame: FrameAllocator,
    val program: ProgramBuilder,
) {

    val operands = OperandStack(compiler.operandPool)
    private val poppedOperands = PoppedOperands(operands)
    internal var deferredBranchPaths: DeferredBranchPaths? = null
    val localAliases = arrayOfNulls<Operand>(layout.localCount)
    val controls = ControlStack(compiler.controlPool)
    var rootControl: BlockContext? = null
    var reachable = true
    var handlerDepth = 0

    inline fun <T : LinkedInstruction> emit(
        instruction: T,
        dispatcher: (T) -> DispatchableInstruction,
    ) {
        val dispatchableInstruction = dispatcher(instruction)
        compiler.instructionObserver?.onInstruction(dispatchableInstruction, instruction)
        program.append(dispatchableInstruction)
    }

    inline fun emit(
        dispatchableInstruction: DispatchableInstruction,
        instruction: () -> LinkedInstruction,
    ) {
        compiler.instructionObserver?.onInstruction(dispatchableInstruction, instruction())
        program.append(dispatchableInstruction)
    }

    inline fun <T : LinkedInstruction> dispatch(
        instruction: T,
        dispatcher: (T) -> DispatchableInstruction,
    ): DispatchableInstruction {
        val dispatchableInstruction = dispatcher(instruction)
        compiler.instructionObserver?.onInstruction(dispatchableInstruction, instruction)
        return dispatchableInstruction
    }

    inline fun dispatch(
        dispatchableInstruction: DispatchableInstruction,
        instruction: () -> LinkedInstruction,
    ): DispatchableInstruction {
        compiler.instructionObserver?.onInstruction(dispatchableInstruction, instruction())
        return dispatchableInstruction
    }

    fun pushFrame(type: ValueType?, reservedSlot: Int, sourceSlot: Int = reservedSlot) {
        push(type, reservedSlot, OperandSourceKind.Frame, sourceSlot.toLong())
    }

    fun pushLocal(
        type: ValueType?,
        reservedSlot: Int,
        localIndex: Int,
        sourceSlot: Int,
    ) {
        push(type, reservedSlot, OperandSourceKind.Local, sourceSlot.toLong(), localIndex)
    }

    fun pushI32(type: ValueType?, reservedSlot: Int, value: Int) {
        push(type, reservedSlot, OperandSourceKind.I32Immediate, value.toLong())
    }

    fun pushI64(type: ValueType?, reservedSlot: Int, value: Long) {
        push(type, reservedSlot, OperandSourceKind.I64Immediate, value)
    }

    fun pushF32(type: ValueType?, reservedSlot: Int, value: Float) {
        push(type, reservedSlot, OperandSourceKind.F32Immediate, value.toRawBits().toLong())
    }

    fun pushF64(type: ValueType?, reservedSlot: Int, value: Double) {
        push(type, reservedSlot, OperandSourceKind.F64Immediate, value.toRawBits())
    }

    private fun push(
        type: ValueType?,
        reservedSlot: Int,
        sourceKind: OperandSourceKind,
        sourceBits: Long,
        sourceLocalIndex: Int = Operand.NO_LOCAL_INDEX,
    ) {
        val operand = operands.push(type, reservedSlot, sourceKind, sourceBits, sourceLocalIndex)
        frame.reserve(operand.reservedSlot)
        trackLocalAlias(operand)
    }

    fun pop(): Operand {
        val operand = operands.pop()
        untrackLocalAlias(operand)
        frame.release(operand.reservedSlot)
        return operand
    }

    fun pop(count: Int): List<Operand> {
        check(count in 0..operands.size)
        val startIndex = operands.size - count
        repeat(count) {
            pop()
        }
        return poppedOperands.reset(startIndex, count)
    }

    fun preserveLocal(index: Int) {
        var alias = localAliases[index] ?: return
        var destinationSlots = IntArray(4)
        var destinationCount = 0
        while (true) {
            if (destinationCount == destinationSlots.size) {
                destinationSlots = destinationSlots.copyOf(destinationSlots.size * 2)
            }
            destinationSlots[destinationCount++] = alias.reservedSlot
            materializeOperand(alias)
            alias.tracksLocal = false
            val nextAlias = alias.nextLocalAlias ?: break
            nextAlias.previousLocalAlias = null
            alias.previousLocalAlias = null
            alias.nextLocalAlias = null
            alias = nextAlias
        }
        alias.previousLocalAlias = null
        alias.nextLocalAlias = null
        localAliases[index] = null
        emitCopies(
            sourceSlots = IntArray(destinationCount) { layout.localSlot(index) },
            destinationSlots = destinationSlots.copyOf(destinationCount),
        )
    }

    fun materialize(operand: Operand): Int {
        when (operand.sourceKind) {
            OperandSourceKind.I32Immediate -> emitI32Constant(operand.i32Immediate, operand.reservedSlot)
            OperandSourceKind.I64Immediate -> emitI64Constant(operand.i64Immediate, operand.reservedSlot)
            OperandSourceKind.F32Immediate -> emitF32Constant(operand.sourceBits.toInt(), operand.reservedSlot)
            OperandSourceKind.F64Immediate -> emitF64Constant(operand.sourceBits, operand.reservedSlot)
            OperandSourceKind.Local -> {
                emitCopy(operand.sourceSlot, operand.reservedSlot)
                untrackLocalAlias(operand)
            }
            OperandSourceKind.Frame -> Unit
        }
        materializeOperand(operand)
        return operand.reservedSlot
    }

    fun rewindFrame() {
        frame.rewindTo(operands.highestReservedSlot())
    }

    fun materializeBelow(topCount: Int) {
        val end = operands.size - topCount
        if (end <= 0) return
        while (true) {
            val index = operands.firstUnmaterializedIndex()
            if (index !in 0 until end) return
            materialize(operands[index])
        }
    }

    fun unwindToHeight(height: Int) {
        check(height in 0..operands.size)
        while (operands.size > height) pop()
    }

    fun replaceStack(baseHeight: Int, types: List<ValueType>, slots: IntArray) {
        check(types.size == slots.size)
        unwindToHeight(baseHeight)
        for (index in types.indices) {
            pushFrame(types[index], slots[index])
        }
    }

    fun resultRegionSlots(baseHeight: Int, arity: Int): IntArray {
        if (arity == 0) return emptyIntArray
        val highestPrefixSlot = maxOf(
            frame.temporarySlotBase - 1,
            operands.highestReservedSlot(baseHeight),
        )
        val temporaryHeight = highestPrefixSlot - frame.temporarySlotBase + 1
        return IntArray(arity) { index -> frame.temporarySlotBase + temporaryHeight + index }
    }

    private fun trackLocalAlias(operand: Operand) {
        if (operand.sourceKind != OperandSourceKind.Local) return
        val previousHead = localAliases[operand.sourceLocalIndex]
        operand.nextLocalAlias = previousHead
        previousHead?.previousLocalAlias = operand
        operand.tracksLocal = true
        localAliases[operand.sourceLocalIndex] = operand
    }

    private fun untrackLocalAlias(operand: Operand) {
        if (!operand.tracksLocal) return
        if (operand.sourceKind != OperandSourceKind.Local) return
        val localIndex = operand.sourceLocalIndex
        val previous = operand.previousLocalAlias
        val next = operand.nextLocalAlias
        if (previous == null) {
            localAliases[localIndex] = next
        } else {
            previous.nextLocalAlias = next
        }
        next?.previousLocalAlias = previous
        operand.previousLocalAlias = null
        operand.nextLocalAlias = null
        operand.tracksLocal = false
    }

    private fun materializeOperand(operand: Operand, sourceSlot: Int = operand.reservedSlot) {
        operand.materialize(sourceSlot)
        operands.markMaterialized(operand)
    }
}
