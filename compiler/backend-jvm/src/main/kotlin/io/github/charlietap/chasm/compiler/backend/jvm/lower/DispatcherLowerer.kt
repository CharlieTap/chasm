package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.STACK_LIMIT_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import org.objectweb.asm.Opcodes

internal class ChunkMethod(
    val start: Int,
    val end: Int,
    val name: String,
    val owner: String,
    val entryCount: Int,
)

internal fun LowerContext.lowerDispatcher(
    chunks: List<ChunkMethod>,
    frameCost: Int,
    dynamic: Map<Int, Int>,
) {
    initializeScratch()
    emit.long(0L)
    emit.store(Opcodes.LSTORE, locals.rootsSaved)
    emit.int(0)
    emit.store(Opcodes.ISTORE, locals.rootBase)

    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    emit.int(frameCost)
    emit.insn(Opcodes.IADD)
    emit.insn(Opcodes.DUP)
    emit.store(Opcodes.ISTORE, Locals.DEPTH)
    pushContext()
    emit.getField(CONTEXT_TYPE, STACK_LIMIT_FIELD, "I")
    val fits = emit.label()
    emit.jump(Opcodes.IF_ICMPLE, fits)
    trap(CALL_STACK_EXHAUSTED)
    emit.place(fits)

    pushContext()
    emit.int(frame.frameSlots)
    emit.invokeStatic(OPEN_FRAME)
    emit.insn(Opcodes.DUP2)
    emit.store(Opcodes.LSTORE, locals.rootsSaved)
    emit.insn(Opcodes.L2I)
    emit.store(Opcodes.ISTORE, locals.rootBase)
    pushContext()
    emit.invokeStatic(FRAME)
    emit.store(Opcodes.ASTORE, locals.scratchValues)
    if (argumentArray) {
        emit.load(Opcodes.ALOAD, locals.parameterLocals[0])
        emit.int(0)
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.int(parameterCount)
        emit.invokeStatic(ARRAYCOPY)
    }
    if (!argumentArray) {
        parameterDescriptors.forEachIndexed { index, descriptor ->
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.load(Opcodes.ILOAD, locals.rootBase)
            emit.int(index)
            emit.insn(Opcodes.IADD)
            val local = locals.parameterLocals[index]
            when (descriptor) {
                "I" -> emit.load(Opcodes.ILOAD, local)
                "F" -> emit.load(Opcodes.FLOAD, local)
                "D" -> emit.load(Opcodes.DLOAD, local)
                else -> emit.load(Opcodes.LLOAD, local)
            }
            encode(descriptor)
            emit.insn(Opcodes.LASTORE)
        }
    }
    resetSlots(locals.scratchValues)

    val continuation = locals.exitPosition
    emit.int(0)
    emit.store(Opcodes.ISTORE, continuation)
    val loop = emit.label()
    val negative = emit.label()
    val unknown = emit.label()
    emit.place(loop)
    emit.load(Opcodes.ILOAD, continuation)
    emit.jump(Opcodes.IFLT, negative)
    val calls = chunks.map { emit.label() }
    val targets = chunks.flatMapIndexed { index, chunk -> List(chunk.entryCount) { calls[index] } }
    emit.load(Opcodes.ILOAD, continuation)
    emit.tableSwitch(0, unknown, targets.toTypedArray())
    chunks.forEachIndexed { index, chunk ->
        emit.place(calls[index])
        emit.load(Opcodes.ALOAD, Locals.BINDING)
        pushContext()
        emit.load(Opcodes.ILOAD, Locals.DEPTH)
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.load(Opcodes.ILOAD, continuation)
        emit.invokeStatic(MethodRef(chunk.owner, chunk.name, listOf(module.bindingType, CONTEXT_TYPE, "I", "I", "I"), "I"))
        emit.store(Opcodes.ISTORE, continuation)
        emit.jump(Opcodes.GOTO, loop)
    }

    emit.place(negative)
    val returns = emit.label()
    val nothing = emit.label()
    emit.load(Opcodes.ILOAD, continuation)
    emit.int(Chunk.RETURN)
    emit.jump(Opcodes.IF_ICMPEQ, returns)
    emit.load(Opcodes.ILOAD, continuation)
    emit.int(Chunk.LEAVE_WITHOUT_RESULT)
    emit.jump(Opcodes.IF_ICMPEQ, nothing)
    if (dynamic.isNotEmpty()) {
        val table = LongArray(dynamic.keys.max() + 1) { -1L }
        dynamic.forEach { (position, id) -> table[position] = id.toLong() }
        pushResource(Resource.Longs(table))
        emit.int(Chunk.DYNAMIC_EXIT_BASE)
        emit.load(Opcodes.ILOAD, continuation)
        emit.insn(Opcodes.ISUB)
        emit.insn(Opcodes.LALOAD)
        emit.insn(Opcodes.L2I)
        emit.insn(Opcodes.DUP)
        emit.store(Opcodes.ISTORE, continuation)
        emit.jump(Opcodes.IFGE, loop)
    }
    emit.place(unknown)
    trap(UNREACHABLE)

    emit.place(returns)
    returnFromFrame()
    emit.place(nothing)
    closeFrame()
    returnNothing()
}

private fun LowerContext.returnFromFrame() {
    pushContext()
    emit.invokeStatic(FRAME)
    emit.store(Opcodes.ASTORE, locals.scratchValues)
    when (resultDescriptor) {
        "V" -> {
            closeFrame()
            emit.insn(Opcodes.RETURN)
        }
        "[J" -> {
            emit.int(resultCount)
            emit.newLongArray()
            emit.store(Opcodes.ASTORE, locals.scratchObject)
            for (index in 0 until resultCount) {
                emit.load(Opcodes.ALOAD, locals.scratchObject)
                emit.type(Opcodes.CHECKCAST, "[J")
                emit.int(index)
                frameValue(index)
                emit.insn(Opcodes.LASTORE)
            }
            closeFrame()
            emit.load(Opcodes.ALOAD, locals.scratchObject)
            emit.type(Opcodes.CHECKCAST, "[J")
            emit.insn(Opcodes.ARETURN)
        }
        else -> {
            frameValue(0)
            when (resultDescriptor) {
                "I" -> emit.insn(Opcodes.L2I)
                "F" -> {
                    emit.insn(Opcodes.L2I)
                    emit.invokeStatic(INT_BITS_TO_FLOAT)
                }
                "D" -> emit.invokeStatic(LONG_BITS_TO_DOUBLE)
            }
            closeFrame()
            emit.insn(
                when (resultDescriptor) {
                    "I" -> Opcodes.IRETURN
                    "F" -> Opcodes.FRETURN
                    "D" -> Opcodes.DRETURN
                    else -> Opcodes.LRETURN
                },
            )
        }
    }
}

private fun LowerContext.frameValue(
    slot: Int,
) {
    emit.load(Opcodes.ALOAD, locals.scratchValues)
    emit.load(Opcodes.ILOAD, locals.rootBase)
    emit.int(slot)
    emit.insn(Opcodes.IADD)
    emit.insn(Opcodes.LALOAD)
}

private fun LowerContext.closeFrame() {
    pushContext()
    emit.load(Opcodes.LLOAD, locals.rootsSaved)
    emit.invokeStatic(CLOSE_FRAME)
}

internal fun chunkName(
    functionName: String,
    index: Int,
) = "$functionName\$c$index"
