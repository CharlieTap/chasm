package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import org.objectweb.asm.Opcodes

internal fun LowerContext.ret(
    results: List<Operand> = List(resultCount) { Operand.Slot(it) },
) {
    if (chunk != null) return chunkReturn(results)
    closeRoots()
    when (resultDescriptor) {
        "V" -> emit.insn(Opcodes.RETURN)
        "I" -> {
            pushInt(results[0])
            emit.insn(Opcodes.IRETURN)
        }
        "F" -> {
            pushFloat(results[0])
            emit.insn(Opcodes.FRETURN)
        }
        "D" -> {
            pushDouble(results[0])
            emit.insn(Opcodes.DRETURN)
        }
        "J" -> {
            pushLong(results[0])
            emit.insn(Opcodes.LRETURN)
        }
        else -> {
            emit.int(resultCount)
            emit.newLongArray()
            results.forEachIndexed { index, result ->
                emit.insn(Opcodes.DUP)
                emit.int(index)
                pushLong(result)
                emit.insn(Opcodes.LASTORE)
            }
            emit.insn(Opcodes.ARETURN)
        }
    }
}

private fun LowerContext.chunkReturn(
    results: List<Operand>,
) {
    if (results.isNotEmpty()) {
        pushContext()
        emit.invokeStatic(FRAME)
        emit.store(Opcodes.ASTORE, locals.scratchValues)
    }
    results.forEachIndexed { index, result ->
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.int(index)
        emit.insn(Opcodes.IADD)
        pushLong(result)
        emit.insn(Opcodes.LASTORE)
    }
    emit.int(Chunk.RETURN)
    emit.insn(Opcodes.IRETURN)
}

internal fun LowerContext.returnNothing() {
    if (chunk != null) {
        emit.int(Chunk.LEAVE_WITHOUT_RESULT)
        return emit.insn(Opcodes.IRETURN)
    }
    when (resultDescriptor) {
        "V" -> emit.insn(Opcodes.RETURN)
        "I" -> {
            emit.int(0)
            emit.insn(Opcodes.IRETURN)
        }
        "F" -> {
            emit.float(0)
            emit.insn(Opcodes.FRETURN)
        }
        "D" -> {
            emit.double(0L)
            emit.insn(Opcodes.DRETURN)
        }
        "J" -> {
            emit.long(0L)
            emit.insn(Opcodes.LRETURN)
        }
        else -> {
            emit.insn(Opcodes.ACONST_NULL)
            emit.insn(Opcodes.ARETURN)
        }
    }
}

internal fun LowerContext.trap(
    method: MethodRef,
) {
    emit.invokeStatic(method)
    emit.insn(Opcodes.POP)
    emit.insn(Opcodes.ACONST_NULL)
    emit.insn(Opcodes.ATHROW)
}
