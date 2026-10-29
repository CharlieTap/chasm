package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import org.objectweb.asm.Opcodes

internal fun LowerContext.openRoots() {
    if (!references.writesThrough) {
        emit.long(0L)
        emit.store(Opcodes.LSTORE, locals.rootsSaved)
        emit.int(0)
        emit.store(Opcodes.ISTORE, locals.rootBase)
        return
    }
    pushContext()
    emit.int(references.slots.size)
    emit.invokeStatic(OPEN_ROOTS)
    emit.insn(Opcodes.DUP2)
    emit.store(Opcodes.LSTORE, locals.rootsSaved)
    emit.insn(Opcodes.L2I)
    emit.store(Opcodes.ISTORE, locals.rootBase)
}

internal fun LowerContext.publish(
    slot: Int,
) {
    pushContext()
    emit.invokeStatic(FRAME)
    emit.load(Opcodes.ILOAD, locals.rootBase)
    emit.int(if (chunk != null) slot else references.rootIndex[slot])
    emit.insn(Opcodes.IADD)
    emit.load(Opcodes.LLOAD, locals.slot(slot))
    emit.insn(Opcodes.LASTORE)
}

internal fun LowerContext.closeRoots() {
    if (!references.writesThrough || chunk != null) return
    pushContext()
    emit.load(Opcodes.LLOAD, locals.rootsSaved)
    emit.invokeStatic(CLOSE_FRAME)
}
