package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.CLEAR_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.RootPublication
import org.objectweb.asm.Opcodes

internal class RootFrame(
    private val references: References,
    private val locals: Locals,
    private val emit: Emitter,
    private val chunked: Boolean,
) {
    private var position = RootPublication.COLD_CODE

    private var suppressed = false

    var isArrayCurrent = false
        private set

    fun publishes(slot: Int): Boolean {
        return !suppressed && references.publishes(position, slot)
    }

    fun enterPrologue() {
        position = RootPublication.ENTRY
    }

    fun enterInstruction(position: Int) {
        this.position = position
        isArrayCurrent = references.writesThrough && references.refreshes.arrayCurrentAt(position)
    }

    fun enterColdCode() {
        position = RootPublication.COLD_CODE
        isArrayCurrent = false
    }

    inline fun withoutPublication(lower: () -> Unit) {

        val previous = suppressed
        suppressed = true

        try {
            lower()
        } finally {
            suppressed = previous
        }
    }

    fun open() {

        if (!references.writesThrough) {
            emit.long(0L)
            emit.store(Opcodes.LSTORE, locals.rootsSaved)
            emit.int(0)
            emit.store(Opcodes.ISTORE, locals.rootBase)
            initialize(loadArray = false)
            return
        }

        pushContext()
        emit.int(references.slots.size)
        emit.invokeStatic(OPEN_ROOTS)
        emit.insn(Opcodes.DUP2)
        emit.store(Opcodes.LSTORE, locals.rootsSaved)
        emit.insn(Opcodes.L2I)
        emit.store(Opcodes.ISTORE, locals.rootBase)

        initialize(references.refreshes.afterEntry)
    }

    fun initialize(loadArray: Boolean) {
        if (loadArray) {
            loadArray()
            isArrayCurrent = true
        } else {
            emit.insn(Opcodes.ACONST_NULL)
            emit.store(Opcodes.ASTORE, locals.rootsArray)
        }
    }

    fun refresh() {

        if (!references.writesThrough) {
            return
        }

        loadArray()
        isArrayCurrent = true
    }

    fun refreshAfterCall(position: Int) {

        if (!references.writesThrough) {
            return
        }

        if (references.refreshes.refreshRequiredAfter(position)) {
            refresh()
        } else {
            isArrayCurrent = false
        }
    }

    fun loadArray() {
        pushContext()
        emit.invokeStatic(FRAME)
        emit.store(Opcodes.ASTORE, locals.rootsArray)
    }

    fun publish(slot: Int) {

        if (!isArrayCurrent) {
            refresh()
        }

        emit.load(Opcodes.ALOAD, locals.rootsArray)
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.int(if (chunked) slot else references.rootIndex[slot])
        emit.insn(Opcodes.IADD)
        emit.load(Opcodes.LLOAD, locals.slot(slot))
        emit.insn(Opcodes.LASTORE)
    }

    fun clear(
        start: Int,
        size: Int,
    ) {
        pushContext()
        emit.load(Opcodes.ILOAD, locals.rootBase)
        if (start != 0) {
            emit.int(start)
            emit.insn(Opcodes.IADD)
        }
        emit.int(size)
        emit.invokeStatic(CLEAR_ROOTS)
    }

    fun close() {

        if (!references.writesThrough || chunked) {
            return
        }

        pushContext()
        emit.load(Opcodes.LLOAD, locals.rootsSaved)
        emit.invokeStatic(CLOSE_FRAME)
    }

    private fun pushContext() {
        emit.load(Opcodes.ALOAD, Locals.CONTEXT)
    }
}
