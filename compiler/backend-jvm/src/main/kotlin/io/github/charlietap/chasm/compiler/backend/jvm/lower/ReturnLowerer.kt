package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.type.ValueType
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

internal fun LowerContext.ret(
    results: List<Operand> = List(resultCount) { Operand.Slot(it) },
) {
    if (chunk != null) return chunkReturn(results)
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
        OBJECT_TYPE -> {
            pushObject(results[0])
            emit.insn(Opcodes.ARETURN)
        }
        else -> {
            val types = type.results.types
            if (types.any { result -> result is ValueType.Reference }) {
                pushContext()
                emit.int(resultCount)
                emit.invokeStatic(RESULT_REFERENCES)
                types.forEachIndexed { index, result ->
                    if (result is ValueType.Reference) {
                        emit.insn(Opcodes.DUP)
                        emit.int(index)
                        pushObject(results[index])
                        emit.insn(Opcodes.AASTORE)
                    }
                }
                emit.insn(Opcodes.POP)
            }
            emit.int(resultCount)
            emit.newLongArray()
            types.forEachIndexed { index, result ->
                if (result !is ValueType.Reference) {
                    emit.insn(Opcodes.DUP)
                    emit.int(index)
                    pushLong(results[index])
                    emit.insn(Opcodes.LASTORE)
                }
            }
            emit.insn(Opcodes.ARETURN)
        }
    }
}

private fun LowerContext.chunkReturn(
    results: List<Operand>,
) {
    val types = type.results.types
    if (types.any { result -> result !is ValueType.Reference }) {
        pushContext()
        emit.invokeStatic(FRAME)
        emit.store(Opcodes.ASTORE, locals.scratchValues)
    }
    results.forEachIndexed { index, result ->
        if (types[index] is ValueType.Reference) {
            emit.load(Opcodes.ALOAD, locals.objectFrame)
            emit.int(index)
            pushObject(result)
            emit.insn(Opcodes.AASTORE)
        } else {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.load(Opcodes.ILOAD, locals.frameBase)
            emit.int(index)
            emit.insn(Opcodes.IADD)
            pushLong(result)
            emit.insn(Opcodes.LASTORE)
        }
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

internal fun LowerContext.trapLabel(method: MethodRef): Label {

    val shared = !module.options.stacktraces
    if (shared) {
        sharedTrapLabels[method]?.let { label -> return label }
    }

    val label = emit.label()
    if (shared) {
        sharedTrapLabels[method] = label
    }

    val origin = position
    outOfLine += {
        emit.place(label)
        markSite(origin, label)
        trap(method)
    }

    return label
}
