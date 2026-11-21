package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.MAX_UNCHARGED_LEAF_FRAME_COST
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.STACK_LIMIT_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.takesReferenceArray
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.LiteralOp

internal fun LowerContext.lowerPrologue() {
    emit.moveObject(Registers.CONTEXT, registers.contextParameter)
    emit.move(Registers.DEPTH, registers.depthParameter)
    emit.moveObject(registers.binding, registers.bindingParameter)
    refreshMemory()
    val memory = module.inlineMemory
    val cachedMemory = if (memory != null && analysis.usesInlineMemory) cachedConstantIndex[Resource.Memory(memory.index)] else null
    if (cachedMemory != null) {
        emit.moveObject(registers.cachedConstant(cachedMemory), MEMORY_INSTANCE_REGISTER)
    }
    if (analysis.unsafeMemory) {
        loadObject(registers.unsafe, Resource.Unsafe, UNSAFE_TYPE)
        emit.constWide(registers.byteArrayBase, checkNotNull(module.options.byteArrayBaseOffset))
    }
    if (!facts.isLeaf || frameCost(registers) > MAX_UNCHARGED_LEAF_FRAME_COST) {
        chargeStackBudget()
    }
    analysis.cachedConstants.forEachIndexed { index, constant ->
        if (index == cachedMemory) {
            return@forEachIndexed
        }
        loadCachedConstant(constant)
        emit.moveObject(registers.cachedConstant(index), Registers.OBJECT)
    }
    cachesLoaded = true
    openWindow()
    receiveParameters()
    initializeLocals()
    if (facts.window.callBufferWords > 0) {
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
    trap(CALL_STACK_EXHAUSTED)
    emit.place(ok)
    emit.move(Registers.DEPTH, Registers.I0)
}

private fun LowerContext.receiveParameters() {
    if (argumentArray) {
        val types = facts.type.params.types
        val referenceArray = takesReferenceArray(facts.type, module.referenceDescriptor)
        for (index in 0 until parameterCount) {
            if (referenceArray && types[index] is ValueType.Reference) {
                emit.moveObject(Registers.SECOND_OBJECT, registers.parameter(1))
                emit.const(Registers.I1, index)
                emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I1)
                storeReference(index, Registers.OBJECT)
            } else {
                emit.moveObject(Registers.OBJECT, registers.parameter(0))
                emit.const(Registers.I2, index)
                emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
                store(index, Registers.W0)
            }
        }
        return
    }
    for (index in 0 until parameterCount) {
        val parameter = registers.parameter(parameterOffsets[index])
        val kind = kinds.writeKind(index)
        when (parameterDescriptors[index]) {
            "I" -> if (kind == SlotKindFlow.INT) {
                emit.move(registers.home(index, kind), parameter)
            } else {
                emit.move(Registers.I0, parameter)
                storeInt(index, Registers.I0)
            }
            "F" -> if (kind == SlotKindFlow.FLOAT) {
                emit.move(registers.home(index, kind), parameter)
            } else {
                emit.move(Registers.I0, parameter)
                storeFloat(index, Registers.I0)
            }
            "D" -> if (kind == SlotKindFlow.DOUBLE) {
                emit.moveWide(registers.home(index, kind), parameter)
            } else {
                emit.moveWide(Registers.W0, parameter)
                storeDouble(index, Registers.W0)
            }
            OBJECT_TYPE -> emit.moveObject(registers.home(index, kinds.readKind(index, SlotKindFlow.OBJECT)), parameter)
            else -> if (kind != SlotKindFlow.LONG) {
                emit.moveWide(Registers.W0, parameter)
                store(index, Registers.W0)
            } else {
                emit.moveWide(registers.home(index, kind), parameter)
            }
        }
    }
}

internal fun LowerContext.initializeLocals() {

    val initial = facts.initialValues
    val firstTemporary = if (module.analysis.usesExceptions) frameSlots else facts.firstTemporary

    var zeroed = false
    for (slot in parameterCount until frameSlots) {

        if (slot >= firstTemporary || kinds.homeMask(slot) == 0) {
            continue
        }
        if (!zeroed) {
            emit.constWide(Registers.W0, 0L)
            emit.const(Registers.NARROW, 0)
            zeroed = true
        }

        val value = initial[slot]
        forEachHome(slot) { kind ->
            val home = registers.home(slot, kind)
            val wide = SlotKindFlow.wide(kind)
            when {
                kind == SlotKindFlow.OBJECT -> emit.moveObject(home, Registers.NARROW)
                !wide && value == 0L -> emit.move(home, Registers.NARROW)
                !wide -> {
                    emit.const(Registers.I0, value.toInt())
                    emit.move(home, Registers.I0)
                }
                value == 0L -> emit.moveWide(home, Registers.W0)
                else -> {
                    emit.constWide(Registers.W1, value)
                    emit.moveWide(home, Registers.W1)
                }
            }
        }
    }
}

internal fun LowerContext.openWindow() {
    if (!facts.callsDirectly) {
        return
    }
    emit.moveObject(registers.window, registers.binding)
    emit.moveObject(registers.window + 1, Registers.CONTEXT)
    emit.move(registers.window + 2, Registers.DEPTH)
}

internal fun LowerContext.restoreWindow(
    binding: Boolean = false,
) {
    if (!facts.callsDirectly) {
        return
    }
    if (binding) emit.moveObject(registers.window, registers.binding)
    emit.moveObject(registers.window + 1, Registers.CONTEXT)
    emit.move(registers.window + 2, Registers.DEPTH)
}

private const val REGISTER_BYTES = 8

private const val FRAME_OVERHEAD_BYTES = 256

private const val UNIT_BYTES = 32
