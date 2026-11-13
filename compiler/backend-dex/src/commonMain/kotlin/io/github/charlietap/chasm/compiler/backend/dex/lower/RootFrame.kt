package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.CLEAR_ROOTS
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.RootPublication
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal class RootFrame(
    private val references: References,
    private val registers: Registers,
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
            return
        }

        emit.const(Registers.I0, references.slots.size)
        emit.invoke(OPEN_ROOTS, Registers.CONTEXT, Registers.I0)
        emit.moveResultWide(Registers.W0)
        emit.moveWide(registers.activationSaved, Registers.W0)
        emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
        emit.move(registers.activationFp, Registers.I0)

        if (references.refreshes.afterEntry) {
            refresh()
        }
    }

    fun initialize() {

        if (!references.writesThrough) {
            return
        }

        emit.move(registers.activationFp, registers.frameBase)
        refresh()
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

    private fun loadArray() {
        emit.invoke(FRAME, Registers.CONTEXT)
        emit.moveResultObject(registers.activationArray)
    }

    fun publish(
        slot: Int,
        pair: Int,
    ) {

        if (!isArrayCurrent) {
            loadArray()
        }

        val root = if (chunked) slot else references.rootIndex[slot]
        if (root <= Byte.MAX_VALUE) {
            emit.literal(LiteralOp.AddInt, registers.activationIndex, registers.activationFp, root)
        } else {
            emit.const(registers.activationIndex, root)
            emit.binary(BinaryOp.AddInt, registers.activationIndex, registers.activationIndex, registers.activationFp)
        }
        emit.arrayPut(ArrayKind.Wide, pair, registers.activationArray, registers.activationIndex)
    }

    fun clear(
        start: Int,
        size: Int,
    ) {

        when {
            start == 0 -> emit.move(Registers.I0, registers.activationFp)
            start <= Byte.MAX_VALUE -> emit.literal(LiteralOp.AddInt, Registers.I0, registers.activationFp, start)
            else -> {
                emit.const(Registers.I0, start)
                emit.binary(BinaryOp.AddInt, Registers.I0, Registers.I0, registers.activationFp)
            }
        }

        emit.const(Registers.I1, size)
        emit.invoke(CLEAR_ROOTS, Registers.CONTEXT, Registers.I0, Registers.I1)
    }

    fun close() {

        if (!references.writesThrough || chunked) {
            return
        }

        emit.moveWide(Registers.W3, registers.activationSaved)
        emit.invoke(CLOSE_FRAME, Registers.CONTEXT, Registers.W3, Registers.W3 + 1)
    }
}
