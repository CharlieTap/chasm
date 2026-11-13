package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.MAX_UNCHARGED_LEAF_FRAME_COST
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.SAVE_STACK
import io.github.charlietap.chasm.compiler.backend.abi.STACK_LIMIT_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_TYPE
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal fun LowerContext.lowerPrologue() {
    emit.moveObject(Registers.CONTEXT, registers.contextParameter)
    emit.move(Registers.DEPTH, registers.depthParameter)
    emit.moveObject(registers.binding, registers.bindingParameter)
    refreshMemory()
    if (analysis.unsafeMemory) {
        loadObject(registers.unsafe, Resource.Unsafe, UNSAFE_TYPE)
        emit.constWide(registers.byteArrayBase, checkNotNull(module.options.byteArrayBaseOffset))
    }
    if (!analysis.isLeaf || frameCost(registers) > MAX_UNCHARGED_LEAF_FRAME_COST) {
        chargeStackBudget()
    }
    if (references.writesThrough) {
        emit.const(Registers.I0, references.slots.size)
        emit.invoke(OPEN_ROOTS, Registers.CONTEXT, Registers.I0)
        emit.moveResultWide(Registers.W0)
        emit.moveWide(registers.activationSaved, Registers.W0)
        emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
        emit.move(registers.activationFp, Registers.I0)
        refreshActivation()
    }
    analysis.cachedConstants.forEachIndexed { index, constant ->
        loadCachedConstant(constant)
        emit.moveObject(registers.cachedConstant(index), Registers.OBJECT)
    }
    cachesLoaded = true
    openWindow()
    if (function.exceptions != null) {
        emit.invoke(SAVE_STACK, Registers.CONTEXT)
        emit.moveResultWide(Registers.W0)
        emit.moveWide(registers.savedStack, Registers.W0)
    }
    position = SlotKinds.PROLOGUE
    receiveParameters()
    initializeLocals()
    if (analysis.window.callBufferWords > 0) {
        emit.const(Registers.I0, 0)
        emit.moveObject(registers.callBuffer, Registers.I0)
    }
}

internal fun frameCost(
    registers: Registers,
): Int = (registers.count * REGISTER_BYTES + FRAME_OVERHEAD_BYTES + UNIT_BYTES - 1) / UNIT_BYTES

internal fun LowerContext.chargeStackBudget(
    cost: Int = frameCost(registers),
) {
    val ok = emit.freshLabel()
    if (cost <= Short.MAX_VALUE) {
        emit.literal(LiteralOp.AddInt, Registers.I0, Registers.DEPTH, cost)
    } else {
        emit.const(Registers.I0, cost)
        emit.binary(BinaryOp.AddInt, Registers.I0, Registers.DEPTH, Registers.I0)
    }
    emit.getField(Registers.I1, Registers.CONTEXT, CONTEXT_TYPE, STACK_LIMIT_FIELD, "I")
    emit.branchCompare(Cond.Le, Registers.I0, Registers.I1, ok)
    emit.invoke(CALL_STACK_EXHAUSTED)
    returnUnreachable()
    emit.place(ok)
    emit.move(Registers.DEPTH, Registers.I0)
}

private fun LowerContext.receiveParameters() {
    if (argumentArray) {
        for (index in 0 until parameterCount) {
            emit.moveObject(Registers.OBJECT, registers.parameter(0))
            emit.const(Registers.I2, index)
            emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
            store(index, Registers.W0)
        }
        return
    }
    for (index in 0 until parameterCount) {
        val parameter = registers.parameter(parameterOffsets[index])
        val home = registers.home(index)
        when (parameterDescriptors[index]) {
            "I" -> if (isInt(index)) {
                checkNarrowWrite(index)
                emit.move(home, parameter)
            } else {
                emit.move(Registers.I0, parameter)
                storeInt(index, Registers.I0)
            }
            "F" -> if (isFloat(index)) {
                checkNarrowWrite(index)
                emit.move(home, parameter)
            } else {
                emit.move(Registers.I0, parameter)
                storeFloat(index, Registers.I0)
            }
            "D" -> if (isDouble(index)) {
                emit.moveWide(home, parameter)
            } else {
                emit.moveWide(Registers.W0, parameter)
                storeDouble(index, Registers.W0)
            }
            else -> if ((references.writesThrough && references.isReference[index]) || !isLong(index)) {
                emit.moveWide(Registers.W0, parameter)
                store(index, Registers.W0)
            } else {
                emit.moveWide(home, parameter)
            }
        }
    }
}

internal fun LowerContext.initializeLocals() {
    val initial = analysis.initialValues
    emit.constWide(Registers.W0, 0L)
    emit.const(Registers.NARROW, 0)
    val firstTemporary = if (module.analysis.usesExceptions) frameSlots else analysis.firstTemporary
    for (slot in parameterCount until frameSlots) {
        if (slot >= firstTemporary && !references.isReference[slot]) continue
        if (isDouble(slot)) {
            emit.moveWide(registers.home(slot), Registers.W0)
        } else if (isNarrow(slot)) {
            if (initial[slot] == 0L) {
                emit.move(registers.home(slot), Registers.NARROW)
            } else {
                emit.const(Registers.I0, initial[slot].toInt())
                emit.move(registers.home(slot), Registers.I0)
            }
        } else if (initial[slot] == 0L) {
            emit.moveWide(registers.home(slot), Registers.W0)
        } else {
            emit.constWide(Registers.W1, initial[slot])
            emit.moveWide(registers.home(slot), Registers.W1)
        }
    }
}

internal fun LowerContext.openWindow() {
    if (!analysis.callsDirectly) return
    emit.moveObject(registers.window, registers.binding)
    emit.moveObject(registers.window + 1, Registers.CONTEXT)
    emit.move(registers.window + 2, Registers.DEPTH)
}

internal fun LowerContext.restoreWindow(
    binding: Boolean = false,
) {
    if (!analysis.callsDirectly) return
    if (binding) emit.moveObject(registers.window, registers.binding)
    emit.moveObject(registers.window + 1, Registers.CONTEXT)
    emit.move(registers.window + 2, Registers.DEPTH)
}

private const val REGISTER_BYTES = 8

private const val FRAME_OVERHEAD_BYTES = 256

private const val UNIT_BYTES = 32
