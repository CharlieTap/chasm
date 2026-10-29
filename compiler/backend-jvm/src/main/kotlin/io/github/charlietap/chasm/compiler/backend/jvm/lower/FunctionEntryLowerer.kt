package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.SAVE_STACK
import io.github.charlietap.chasm.compiler.backend.abi.STACK_LIMIT_FIELD
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import org.objectweb.asm.Opcodes

internal fun LowerContext.lowerPrologue(
    frameCost: Int,
) {
    position = SlotKinds.PROLOGUE
    openRoots()
    for (slot in 0 until parameterCount) initializeShadows(slot, 0L)
    parameterDescriptors.forEachIndexed { index, descriptor ->
        if (argumentArray) {
            emit.load(Opcodes.ALOAD, locals.parameterLocals[0])
            emit.int(index)
            emit.insn(Opcodes.LALOAD)
            return@forEachIndexed storeLong(index)
        }
        val local = locals.parameterLocals[index]
        when (descriptor) {
            "I" -> {
                emit.load(Opcodes.ILOAD, local)
                storeInt(index)
            }
            "F" -> {
                emit.load(Opcodes.FLOAD, local)
                storeFloat(index)
            }
            "D" -> {
                emit.load(Opcodes.DLOAD, local)
                storeDouble(index)
            }
            else -> {
                emit.load(Opcodes.LLOAD, local)
                storeLong(index)
            }
        }
    }
    for (slot in parameterCount until frame.frameSlots) initialize(slot, frame.initialValues[slot])
    initializeScratch()
    if (function.exceptions != null) {
        pushContext()
        emit.invokeStatic(SAVE_STACK)
        emit.store(Opcodes.LSTORE, locals.stackSaved)
    }

    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    emit.int(frameCost)
    emit.insn(Opcodes.IADD)
    emit.insn(Opcodes.DUP)
    emit.store(Opcodes.ISTORE, Locals.DEPTH)
    emit.load(Opcodes.ALOAD, Locals.CONTEXT)
    emit.getField(CONTEXT_TYPE, STACK_LIMIT_FIELD, "I")
    val fits = emit.label()
    emit.jump(Opcodes.IF_ICMPLE, fits)
    trap(CALL_STACK_EXHAUSTED)
    emit.place(fits)
}

internal fun LowerContext.initializeScratch() {
    locals.scratchLong.forEach { local ->
        emit.long(0L)
        emit.store(Opcodes.LSTORE, local)
    }
    emit.int(0)
    emit.store(Opcodes.ISTORE, locals.scratchInt)
    emit.int(0)
    emit.store(Opcodes.ISTORE, locals.scratchValue)
    emit.int(0)
    emit.store(Opcodes.ISTORE, locals.exitPosition)
    emit.long(0L)
    emit.store(Opcodes.LSTORE, locals.stackSaved)
    emit.insn(Opcodes.ACONST_NULL)
    emit.store(Opcodes.ASTORE, locals.scratchValues)
    emit.insn(Opcodes.ACONST_NULL)
    emit.store(Opcodes.ASTORE, locals.scratchObject)
    initializeMemory()
}

internal fun LowerContext.initialize(
    slot: Int,
    bits: Long,
) {
    val local = locals.slot(slot)
    when (locals.kind(slot)) {
        SlotKinds.INT -> {
            emit.int(bits.toInt())
            emit.store(Opcodes.ISTORE, local)
        }
        SlotKinds.FLOAT -> {
            emit.float(bits.toInt())
            emit.store(Opcodes.FSTORE, local)
        }
        SlotKinds.DOUBLE -> {
            emit.double(bits)
            emit.store(Opcodes.DSTORE, local)
        }
        else -> {
            pushLong(Operand.Immediate(bits))
            emit.store(Opcodes.LSTORE, local)
        }
    }
    initializeShadows(slot, bits)
}
