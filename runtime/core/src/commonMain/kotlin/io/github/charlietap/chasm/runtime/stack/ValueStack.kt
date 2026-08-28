package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.gc.GarbageCollectedHeap
import io.github.charlietap.chasm.gc.GcRootMarker
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instruction.OPERAND_TRANSFER_COPY_IMMEDIATE
import io.github.charlietap.chasm.runtime.instruction.OPERAND_TRANSFER_COPY_SLOT
import io.github.charlietap.chasm.runtime.instruction.OPERAND_TRANSFER_INDEX_MASK
import io.github.charlietap.chasm.runtime.instruction.OPERAND_TRANSFER_OPERATION_SHIFT
import io.github.charlietap.chasm.runtime.instruction.OPERAND_TRANSFER_RESTORE
import io.github.charlietap.chasm.runtime.instruction.OPERAND_TRANSFER_SAVE_SLOT
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer

/**
 * Chasm's unified value and activation stack.
 *
 * A compiled Wasm function has no separate frame object or frame stack. Its
 * activation frame is a contiguous region of this stack, and compiled
 * instructions address every value as `FP + slot`:
 *
 * ```text
 *                                           HIGHER ADDRESSES
 *                                                  ▲
 *                                                  │
 *           FP + F = SP ───────────▶ ┌─────────────┴─────────────┐
 *                                     │                           │
 *                                     │   compiler temporaries    │
 *                                     │     [I + 1 + L, F)        │
 *                                     │                           │
 *       FP + I + 1 + L ────────────▶ ├───────────────────────────┤
 *                                     │                           │
 *                                     │   retained local slots    │
 *                                     │    [I + 1, I + 1 + L)     │
 *                                     │                           │
 *           FP + I + 1 ────────────▶ ├───────────────────────────┤
 *                                     │     activation header     │
 *                                     │  return IP │ caller FP Δ  │
 *               FP + I ────────────▶ ╞═══════════════════════════╡
 *                                     │   shared call interface   │
 *                                     │          [0, I)           │
 *                                     │                           │
 *                                     │ entry   parameters [0, P) │
 *                                     │ return  results    [0, R) │
 *                                     │                           │
 *                   FP ────────────▶ └─────────────┬─────────────┘
 *                                                  │
 *                                                  ▼
 *                                           LOWER ADDRESSES
 * ```
 *
 * `P` is the parameter count, `R` is the result count, and
 * `I = max(P, R)` is the call-interface size. `L` is the number of defined-local
 * slots retained by the compiler; locals that need no runtime storage are not
 * present. `F` is the total number of slots in the activation frame.
 *
 * Parameters and results share the interface prefix. A caller places arguments
 * there before entry, and the callee leaves results in the same slots on return.
 * The single header slot at `FP + I` holds the return IP and the displacement to
 * the caller's FP. These are the only per-call bookkeeping values stored at
 * runtime.
 *
 * [fp] and [sp] are virtual-machine registers. Entry sets SP to the exclusive
 * end of the compiler-sized frame, `FP + F`; return restores the caller's FP and
 * contracts SP to the exclusive end of the result interface, `callee FP + R`.
 * Compiled execution uses assigned slots rather than dynamically pushing Wasm
 * operands. Push/pop operations remain for API boundaries and constant
 * expressions.
 */
class ValueStack(minCapacity: Int = MIN_CAPACITY) {

    /** `FP`: absolute index of slot 0 in the active activation frame. */
    var fp = 0

    /** `SP`: absolute exclusive end of the active stack region. */
    var sp = 0
        private set

    private var elements: LongArray

    init {
        val minimumCapacity = maxOf(minCapacity, MIN_CAPACITY)
        if (minimumCapacity > MAX_CAPACITY) {
            throw InvocationException(InvocationError.CallStackExhausted)
        }
        val arrayCapacity: Int =
            if (minimumCapacity.countOneBits() != 1) {
                (minimumCapacity - 1).takeHighestOneBit() shl 1
            } else {
                minimumCapacity
            }
        elements = LongArray(arrayCapacity)
    }

    fun getLocal(localIndex: Int): Long = getFrameSlot(localIndex)

    fun setLocal(
        localIndex: Int,
        value: Long,
    ) {
        setFrameSlot(localIndex, value)
    }

    fun getFrameSlot(slot: Int): Long = elements[fp + slot]

    fun getFrameSlot(
        fp: Int,
        slot: Int,
    ): Long = elements[fp + slot]

    fun setFrameSlot(
        slot: Int,
        value: Long,
    ) {
        elements[fp + slot] = value
    }

    fun setFrameSlot(
        fp: Int,
        slot: Int,
        value: Long,
    ) {
        elements[fp + slot] = value
    }

    fun peek(): Long = try {
        elements[sp - 1]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun push(value: Long) {
        if (sp == elements.size) {
            doubleCapacity()
        }
        elements[sp] = value
        sp++
    }

    fun pop(): Long = try {
        sp--
        elements[sp]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekI32(): Int = try {
        elements[sp - 1].toInt()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekNthI32(n: Int): Int = try {
        elements[sp - 1 - n].toInt()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushI32(value: Int) {
        if (sp == elements.size) {
            doubleCapacity()
        }
        elements[sp] = value.toLong()
        sp++
    }

    fun popI32(): Int = try {
        sp--
        elements[sp].toInt()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun peekNthI64(n: Int): Long = try {
        elements[sp - 1 - n]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushI64(value: Long) {
        if (sp == elements.size) {
            doubleCapacity()
        }
        elements[sp] = value
        sp++
    }

    fun popI64(): Long = try {
        sp--
        elements[sp]
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushF32(value: Float) {
        pushI32(value.toRawBits())
    }

    fun popF32(): Float = try {
        Float.fromBits(popI32())
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun pushF64(value: Double) {
        pushI64(value.toRawBits())
    }

    fun popF64(): Double = try {
        Double.fromBits(popI64())
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MissingStackValue)
    }

    fun push(
        values: LongArray,
    ) {
        val requiredSize = sp + values.size
        ensureCapacity(requiredSize)
        values.copyInto(elements, startIndex = 0, endIndex = values.size, destinationOffset = sp)
        sp += values.size
    }

    fun shrink(
        preserveTopN: Int,
        depth: Int,
    ) {
        elements.copyInto(
            destination = elements,
            destinationOffset = depth,
            startIndex = sp - preserveTopN,
            endIndex = sp,
        )
        sp = depth + preserveTopN
    }

    fun reserveDepth(depth: Int) {
        ensureCapacity(depth)
        if (sp < depth) {
            sp = depth
        }
    }

    /** Ensures backing storage without changing the logical stack depth. */
    fun ensureCapacity(depth: Int) {
        if (depth > elements.size) growCapacity(depth)
    }

    fun fillFrameSlots(
        fp: Int,
        firstSlot: Int,
        count: Int,
        value: Long,
    ) {
        elements.fill(
            element = value,
            fromIndex = fp + firstSlot,
            toIndex = fp + firstSlot + count,
        )
    }

    fun copyValuesToFrame(
        values: LongArray,
        fp: Int,
        firstSlot: Int,
    ) {
        values.copyInto(elements, destinationOffset = fp + firstSlot)
    }

    /** Executes the exact parallel-move schedule selected before execution. */
    fun transferOperands(
        currentFp: Int,
        destinationFp: Int,
        transfer: OperandTransfer,
    ) {
        val schedule = transfer.schedule
        var scratch = 0L
        var index = 0
        while (index < schedule.operationCount) {
            val encoded = schedule.operations[index]
            val operation = encoded ushr OPERAND_TRANSFER_OPERATION_SHIFT
            val destination = encoded and OPERAND_TRANSFER_INDEX_MASK
            val value = schedule.values[index]
            when (operation) {
                OPERAND_TRANSFER_COPY_SLOT -> elements[destinationFp + destination] = elements[currentFp + value.toInt()]
                OPERAND_TRANSFER_COPY_IMMEDIATE -> elements[destinationFp + destination] = value
                OPERAND_TRANSFER_SAVE_SLOT -> scratch = elements[currentFp + value.toInt()]
                OPERAND_TRANSFER_RESTORE -> elements[destinationFp + destination] = scratch
            }
            index++
        }
    }

    /** Activates a frame after its capacity has already been reserved. */
    fun activateFrame(
        fp: Int,
        frameSlots: Int,
    ) {
        this.fp = fp
        sp = fp + frameSlots
    }

    /** Activates a frame using an already calculated absolute stack depth. */
    fun activateFrameAtDepth(
        fp: Int,
        depth: Int,
    ) {
        this.fp = fp
        sp = depth
    }

    /**
     * Activates a compiler-linked callee whose operands already occupy its
     * interface slots.
     *
     * The callee frame end relative to the caller is resolved when the module
     * is linked. This keeps strategy lookup and separate header-write/frame-
     * activation calls out of the invocation path.
     */
    fun activateLinkedFrame(
        callFrameOffset: Int,
        frameEndOffset: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
    ) {
        val callerFp = fp
        val calleeFp = callerFp + callFrameOffset
        val requiredSp = callerFp + frameEndOffset
        if (requiredSp > elements.size) growCapacity(requiredSp)
        elements[calleeFp + activationHeaderSlot] = activationHeader
        fp = calleeFp
        sp = requiredSp
    }

    /** Activates a compiler-linked callee with one immediate operand. */
    fun activateLinkedFrameWithImmediate(
        callFrameOffset: Int,
        frameEndOffset: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
        operand: Long,
    ) {
        val callerFp = fp
        val calleeFp = callerFp + callFrameOffset
        val requiredSp = callerFp + frameEndOffset
        if (requiredSp > elements.size) growCapacity(requiredSp)
        elements[calleeFp] = operand
        elements[calleeFp + activationHeaderSlot] = activationHeader
        fp = calleeFp
        sp = requiredSp
    }

    /** Activates a compiler-linked callee with one slot operand. */
    fun activateLinkedFrameWithSlot(
        callFrameOffset: Int,
        frameEndOffset: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
        sourceSlot: Int,
    ) {
        val callerFp = fp
        val calleeFp = callerFp + callFrameOffset
        val requiredSp = callerFp + frameEndOffset
        if (requiredSp > elements.size) growCapacity(requiredSp)
        elements[calleeFp] = elements[callerFp + sourceSlot]
        elements[calleeFp + activationHeaderSlot] = activationHeader
        fp = calleeFp
        sp = requiredSp
    }

    /** Activates a compiler-linked callee with two staged slot operands. */
    fun activateLinkedFrameWithTwoSlots(
        callFrameOffset: Int,
        frameEndOffset: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
        firstSourceSlot: Int,
        secondSourceSlot: Int,
    ) {
        val callerFp = fp
        val calleeFp = callerFp + callFrameOffset
        val requiredSp = callerFp + frameEndOffset
        if (requiredSp > elements.size) growCapacity(requiredSp)
        val first = elements[callerFp + firstSourceSlot]
        val second = elements[callerFp + secondSourceSlot]
        elements[calleeFp] = first
        elements[calleeFp + 1] = second
        elements[calleeFp + activationHeaderSlot] = activationHeader
        fp = calleeFp
        sp = requiredSp
    }

    /** Activates a compiler-linked callee with three staged slot operands. */
    fun activateLinkedFrameWithThreeSlots(
        callFrameOffset: Int,
        frameEndOffset: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
        firstSourceSlot: Int,
        secondSourceSlot: Int,
        thirdSourceSlot: Int,
    ) {
        val callerFp = fp
        val calleeFp = callerFp + callFrameOffset
        val requiredSp = callerFp + frameEndOffset
        if (requiredSp > elements.size) growCapacity(requiredSp)
        val first = elements[callerFp + firstSourceSlot]
        val second = elements[callerFp + secondSourceSlot]
        val third = elements[callerFp + thirdSourceSlot]
        elements[calleeFp] = first
        elements[calleeFp + 1] = second
        elements[calleeFp + 2] = third
        elements[calleeFp + activationHeaderSlot] = activationHeader
        fp = calleeFp
        sp = requiredSp
    }

    /** Activates a compiler-linked callee with four staged slot operands. */
    fun activateLinkedFrameWithFourSlots(
        callFrameOffset: Int,
        frameEndOffset: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
        firstSourceSlot: Int,
        secondSourceSlot: Int,
        thirdSourceSlot: Int,
        fourthSourceSlot: Int,
    ) {
        val callerFp = fp
        val calleeFp = callerFp + callFrameOffset
        val requiredSp = callerFp + frameEndOffset
        if (requiredSp > elements.size) growCapacity(requiredSp)
        val first = elements[callerFp + firstSourceSlot]
        val second = elements[callerFp + secondSourceSlot]
        val third = elements[callerFp + thirdSourceSlot]
        val fourth = elements[callerFp + fourthSourceSlot]
        elements[calleeFp] = first
        elements[calleeFp + 1] = second
        elements[calleeFp + 2] = third
        elements[calleeFp + 3] = fourth
        elements[calleeFp + activationHeaderSlot] = activationHeader
        fp = calleeFp
        sp = requiredSp
    }

    /**
     * Writes the activation header immediately after the callee interface.
     *
     * The compiler and program installer validate both fields before execution;
     * this hot-path operation is intentionally unchecked.
     */
    fun writeActivationHeader(
        calleeFp: Int,
        activationHeaderSlot: Int,
        activationHeader: Long,
    ) {
        elements[calleeFp + activationHeaderSlot] = activationHeader
    }

    /** Writes the precomputed root activation header. */
    fun writeRootActivationHeader(activationHeaderSlot: Int) {
        elements[activationHeaderSlot] = ROOT_ACTIVATION_HEADER
    }

    /**
     * Restores the caller from the current activation header and returns its IP.
     *
     * Results remain in the current frame's interface slots. [sp] is moved to
     * the end of that result interface so the slots remain live to boundary and
     * root-scanning code without retaining the rest of the completed frame.
     */
    fun restoreCallerFrame(
        resultCount: Int,
        activationHeaderSlot: Int,
    ): Int {
        val calleeFp = fp
        val header = elements[calleeFp + activationHeaderSlot]
        sp = calleeFp + resultCount
        fp = calleeFp - activationCallerFrameDelta(header)
        return activationReturnIp(header)
    }

    /**
     * Returns the live backing storage for trusted host functions.
     *
     * The returned array must not be retained. Any operation that grows this
     * stack can replace it.
     */
    @UnsafeHostApi
    fun unsafeElements(): LongArray = elements

    internal fun replaceTopFieldsWithStruct(
        heap: GarbageCollectedHeap,
        descriptorKey: Int,
        fieldCount: Int,
    ) {
        if (fieldCount > sp) {
            throw InvocationException(InvocationError.MissingStackValue)
        }
        if (fieldCount == 0 && sp == elements.size) {
            doubleCapacity()
        }
        val sourceOffset = sp - fieldCount
        val rawReference = heap.allocateStruct(descriptorKey, elements, sourceOffset)
        elements[sourceOffset] = rawReference
        sp = sourceOffset + 1
    }

    internal fun replaceTopFieldsWithArray(
        heap: GarbageCollectedHeap,
        descriptorKey: Int,
        length: Int,
    ) {
        if (length > sp) {
            throw InvocationException(InvocationError.MissingStackValue)
        }
        if (length == 0 && sp == elements.size) {
            doubleCapacity()
        }
        val sourceOffset = sp - length
        val rawReference = heap.allocateArrayFromElements(descriptorKey, elements, sourceOffset, length)
        elements[sourceOffset] = rawReference
        sp = sourceOffset + 1
    }

    internal fun setFrameSlotToNewStruct(
        heap: GarbageCollectedHeap,
        descriptorKey: Int,
        firstFieldSlot: Int,
        destinationSlot: Int,
    ) {
        val sourceOffset = fp + firstFieldSlot
        val destinationOffset = fp + destinationSlot
        val rawReference = heap.allocateStruct(descriptorKey, elements, sourceOffset)
        elements[destinationOffset] = rawReference
    }

    internal fun setFrameSlotToNewArray(
        heap: GarbageCollectedHeap,
        descriptorKey: Int,
        firstElementSlot: Int,
        length: Int,
        destinationSlot: Int,
    ) {
        val sourceOffset = fp + firstElementSlot
        val destinationOffset = fp + destinationSlot
        val rawReference = heap.allocateArrayFromElements(descriptorKey, elements, sourceOffset, length)
        elements[destinationOffset] = rawReference
    }

    internal fun consumeTopFieldsToException(
        heap: GarbageCollectedHeap,
        descriptorKey: Int,
        fieldCount: Int,
    ): Long {
        if (fieldCount > sp) {
            throw InvocationException(InvocationError.MissingStackValue)
        }
        val sourceOffset = sp - fieldCount
        val rawReference = heap.allocateException(descriptorKey, elements, sourceOffset)
        sp = sourceOffset
        return rawReference
    }

    internal fun allocateExceptionFromFrame(
        heap: GarbageCollectedHeap,
        descriptorKey: Int,
        firstFieldSlot: Int,
    ): Long {
        val sourceOffset = fp + firstFieldSlot
        return heap.allocateException(descriptorKey, elements, sourceOffset)
    }

    internal fun visitGcRoots(rootMarker: GcRootMarker) {
        val end = sp
        var index = 0
        while (index < end) {
            rootMarker.markRoot(elements[index])
            index++
        }
    }

    fun clear() {
        elements.fill(0L)
        fp = 0
        sp = 0
    }

    private fun doubleCapacity() {
        if (elements.size == MAX_CAPACITY) {
            throw InvocationException(InvocationError.CallStackExhausted)
        }
        val newCapacity = minOf(elements.size * 2, MAX_CAPACITY)
        elements = elements.copyOf(newCapacity)
    }

    private fun growCapacity(requiredCapacity: Int) {
        if (requiredCapacity > MAX_CAPACITY) {
            throw InvocationException(InvocationError.CallStackExhausted)
        }
        val newCapacity = (requiredCapacity - 1).takeHighestOneBit() shl 1
        elements = elements.copyOf(newCapacity)
    }

    override fun toString(): String {
        return buildString {
            append("[")
            for (i in 0 until sp) {
                append(elements[i])
                if (i + 1 < sp) append(", ")
            }
            append("]")
        }
    }

    companion object {
        private val ROOT_ACTIVATION_HEADER = activationHeader(Int.MAX_VALUE, 0)
        private const val MIN_CAPACITY = 32
        private const val MAX_CAPACITY = 1 shl 25
    }
}
