package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.CLEAR_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MODULE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL_REF
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.tailCallArguments
import io.github.charlietap.chasm.compiler.backend.analysis.isSelfTailCall
import io.github.charlietap.dexter.ArrayKind

internal fun LowerContext.lowerTailCall(
    instruction: ControlInstruction,
) {
    val arguments = checkNotNull(instruction.tailCallArguments())
    if (instruction is ControlInstruction.ReturnCall && instruction.isSelfTailCall(function, regionAt(position))) return restart(arguments)
    emit.const(Registers.I0, maxOf(arguments.size, resultCount, 1))
    emit.newArray(Registers.OBJECT, Registers.I0, "[J")
    arguments.forEachIndexed { index, argument ->
        loadWide(Registers.W0, argument)
        emit.const(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    when (instruction) {
        is ControlInstruction.ReturnCall -> {
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            loadObject(Registers.W1 + 1, Resource.Function(instruction.function.index), FUNCTION_INSTANCE)
            emit.invoke(TAIL_CALL, Registers.CONTEXT, Registers.W1, Registers.W1 + 1, Registers.OBJECT)
        }
        is ControlInstruction.ReturnCallIndirectI ->
            recordIndirect(instruction.table.index, instruction.type.index, Operand.Immediate(instruction.elementIndex.toLong()))
        is ControlInstruction.ReturnCallIndirectS ->
            recordIndirect(instruction.table.index, instruction.type.index, Operand.Slot(instruction.elementIndexSlot))
        is ControlInstruction.ReturnCallRefS -> {
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            loadWide(Registers.W2, instruction.functionSlot)
            emit.invoke(TAIL_CALL_REF, Registers.CONTEXT, Registers.W1, Registers.W2, Registers.W2 + 1, Registers.OBJECT)
        }
        else -> throw Unsupported(instruction::class.simpleName ?: "tail call")
    }
    closeActivation()
    returnUnreachable()
}

private fun LowerContext.restart(
    arguments: List<Operand>,
) {
    if (chunk != null) return chunkRestart(arguments)
    if (references.writesThrough) {
        emit.move(Registers.I0, registers.activationFp)
        emit.const(Registers.I1, references.slots.size)
        emit.invoke(CLEAR_ROOTS, Registers.CONTEXT, Registers.I0, Registers.I1)
    }
    parallelCopy(arguments, arguments.indices.toList())
    if (references.writesThrough) {
        for (slot in 0 until parameterCount) {
            if (!references.isReference[slot]) continue
            loadWide(Registers.W0, slot)
            publish(slot, Registers.W0)
        }
    }
    initializeLocals()
    emit.gotoLabel(label(0))
}

private fun LowerContext.recordIndirect(
    table: Int,
    type: Int,
    elementIndex: Operand,
) {
    val window = registers.window + 1
    emit.moveObject(window + 5, Registers.OBJECT)
    emit.moveObject(window, Registers.CONTEXT)
    loadValueInt(Registers.I0, Resource.RuntimeType(type))
    emit.move(window + 3, Registers.I0)
    loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
    emit.moveObject(window + 1, Registers.W1)
    loadObject(Registers.W1, Resource.Table(table), TABLE_INSTANCE)
    emit.moveObject(window + 2, Registers.W1)
    loadInt(Registers.I0, Registers.W0, elementIndex)
    emit.move(window + 4, Registers.I0)
    emit.invokeRange(TAIL_CALL_INDIRECT, window, TAIL_CALL_INDIRECT.words)
}

private fun LowerContext.chunkRestart(
    arguments: List<Operand>,
) {
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
    arguments.forEachIndexed { index, argument ->
        loadWide(Registers.W0, argument)
        frameIndex(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    resetFrame(Registers.OBJECT)
    returnCode(0)
}

internal fun LowerContext.resetFrame(
    frame: Int,
) {
    if (parameterCount >= frameSlots) return
    loadObject(Registers.W1, Resource.Longs(analysis.initialValues.copyOfRange(parameterCount, frameSlots)), "[J")
    emit.const(Registers.I0, 0)
    emit.moveObject(Registers.W1 + 1, frame)
    frameIndex(Registers.I2, parameterCount)
    emit.const(Registers.I3, frameSlots - parameterCount)
    emit.invoke(ARRAYCOPY, Registers.W1, Registers.I0, Registers.W1 + 1, Registers.I2, Registers.I3)
}
