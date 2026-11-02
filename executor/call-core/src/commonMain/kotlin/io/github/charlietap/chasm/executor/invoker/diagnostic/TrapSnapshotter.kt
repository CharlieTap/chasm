package io.github.charlietap.chasm.executor.invoker.diagnostic

import io.github.charlietap.chasm.executor.invoker.function.exceptionalCallSiteIp
import io.github.charlietap.chasm.runtime.diagnostic.CompiledTrace
import io.github.charlietap.chasm.runtime.diagnostic.FailedMemoryAccess
import io.github.charlietap.chasm.runtime.diagnostic.TrapMemoryAccess
import io.github.charlietap.chasm.runtime.diagnostic.TrapSnapshot
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.stack.decodeActivationCallerFrameDelta
import io.github.charlietap.chasm.runtime.stack.decodeActivationReturnIp
import io.github.charlietap.chasm.runtime.store.Store

/**
 * Walks the activation headers without changing [vstack]. An inconsistent
 * header ends the walk incomplete rather than reporting a guessed frame.
 * Where the interpreter entered compiled code, the frames compiled code ran
 * are read from [exception] and take the place of that one entry.
 */
internal fun TrapSnapshot(
    store: Store,
    vstack: ValueStack,
    faultIp: Int,
    exception: Throwable? = null,
    headFrames: Int = HEAD_FRAMES,
    tailFrames: Int = TAIL_FRAMES,
): TrapSnapshot {
    val lookup = FunctionLookup(store)
    val programSize = store.program.size
    val frames = FrameRecorder(headFrames, tailFrames)
    var innermostFrameSlots = EMPTY_SLOTS
    var memorySizes = EMPTY_SLOTS
    var complete = true
    var trace: CompiledTrace? = null
    val entries = CompiledEntries(exception)

    var ip = faultIp
    // Compiled code may leave a frame of its own active; its entry recorded where the walk starts.
    val innermostEntry = entries.innermost(faultIp)
    var fp = if (innermostEntry < 0) vstack.fp else entries.framePointer(innermostEntry)
    while (true) {
        val index = if (ip in 0 until programSize) lookup.indexOf(ip) else -1
        if (index < 0) {
            complete = false
            break
        }
        val function = lookup.function(index)
        val strategy = function.callStrategy
        val innermost = frames.total == 0
        val compiled = if (strategy.compiledEntry != null) function.module.compiledFrames else null
        if (compiled != null && exception != null) {
            val reader = trace ?: compiled.trace(store, exception).also { created -> trace = created }
            val before = frames.total
            val reachedEntry = reader.segment(function) { address, position ->
                frames.add(address, position, TrapSnapshot.COMPILED_FRAME, TrapSnapshot.COMPILED_FRAME)
            }
            if (!reachedEntry) complete = false
            if (frames.total == before) {
                frames.add(lookup.address(index), -1, TrapSnapshot.COMPILED_FRAME, TrapSnapshot.COMPILED_FRAME)
            }
        } else {
            frames.add(lookup.address(index), ip, lookup.entryIp(index), lookup.endIp(index))
            // Only the interpreter's own frame needs its memories' sizes; compiled code records the access it failed.
            if (innermost) {
                val slotCount = minOf(strategy.frameSlots, vstack.sp - fp).coerceAtLeast(0)
                innermostFrameSlots = LongArray(slotCount) { slot -> vstack.getFrameSlot(fp, slot) }
                val memories = function.module.memAddresses
                memorySizes = LongArray(memories.size) { memoryIndex ->
                    store.memories.getOrNull(memories[memoryIndex].address)?.size?.toLong() ?: -1L
                }
            }
        }

        val headerSlot = fp + strategy.interfaceSlotCount
        if (fp < 0 || headerSlot >= vstack.sp) {
            complete = false
            break
        }
        val header = vstack.getFrameSlot(fp, strategy.interfaceSlotCount)
        val returnIp = decodeActivationReturnIp(header)
        val delta = decodeActivationCallerFrameDelta(header)
        if (returnIp == EXIT_IP) {
            // Compiled code called this function: carry on at the compiled code's entry below it.
            val entry = if (delta > 0) entries.below(fp - delta) else -1
            if (entry < 0) {
                complete = complete && delta == 0 && fp == 0
                break
            }
            fp = entries.framePointer(entry)
            ip = entries.entryIp(entry)
            continue
        }
        if (delta <= 0 || delta > fp) {
            complete = false
            break
        }
        fp -= delta
        ip = exceptionalCallSiteIp(returnIp, function.functionType.results.types.size)
    }

    val failedAccess = (exception as? InvocationException)?.memoryAccess
    val memoryAccess = if (failedAccess != null && frames.total > 0) memoryAccess(store, frames.firstAddress, failedAccess) else null
    return frames.snapshot(complete, innermostFrameSlots, memorySizes, memoryAccess)
}

private fun memoryAccess(
    store: Store,
    address: Int,
    access: FailedMemoryAccess,
): TrapMemoryAccess? {
    val function = store.functions.getOrNull(address) as? FunctionInstance.WasmFunction ?: return null
    val index = function.module.memAddresses.indexOfFirst { memory -> store.memories.getOrNull(memory.address) === access.memory }
    return TrapMemoryAccess(access.kind, index, access.address, access.width, access.source, access.memory.size.toLong())
}

private class FrameRecorder(
    private val headCapacity: Int,
    private val tailCapacity: Int,
) {
    private val head = IntArray(headCapacity * FIELDS)
    private val tail = IntArray(tailCapacity * FIELDS)
    var total = 0
        private set

    val firstAddress: Int
        get() = if (headCapacity > 0) head[0] else tail[0]

    fun add(address: Int, ip: Int, entryIp: Int, endIp: Int) {
        val target: IntArray
        val offset: Int
        if (total < headCapacity) {
            target = head
            offset = total * FIELDS
        } else if (tailCapacity > 0) {
            target = tail
            offset = ((total - headCapacity) % tailCapacity) * FIELDS
        } else {
            total++
            return
        }
        target[offset] = address
        target[offset + 1] = ip
        target[offset + 2] = entryIp
        target[offset + 3] = endIp
        total++
    }

    fun snapshot(
        complete: Boolean,
        innermostFrameSlots: LongArray,
        memorySizes: LongArray,
        memoryAccess: TrapMemoryAccess?,
    ): TrapSnapshot {
        val headCount = minOf(total, headCapacity)
        val tailRecorded = total - headCount
        val tailCount = minOf(tailRecorded, tailCapacity)
        val count = headCount + tailCount
        val addresses = IntArray(count)
        val ips = IntArray(count)
        val entryIps = IntArray(count)
        val endIps = IntArray(count)

        fun copy(source: IntArray, sourceFrame: Int, destination: Int) {
            val offset = sourceFrame * FIELDS
            addresses[destination] = source[offset]
            ips[destination] = source[offset + 1]
            entryIps[destination] = source[offset + 2]
            endIps[destination] = source[offset + 3]
        }

        for (frame in 0 until headCount) copy(head, frame, frame)
        // The tail is a ring buffer; its oldest retained entry follows the newest.
        val oldest = if (tailRecorded > tailCapacity) tailRecorded % tailCapacity else 0
        for (frame in 0 until tailCount) copy(tail, (oldest + frame) % tailCapacity, headCount + frame)

        return TrapSnapshot(
            functionAddresses = addresses,
            ips = ips,
            entryIps = entryIps,
            endIps = endIps,
            omittedFrames = total - count,
            omissionIndex = headCount,
            complete = complete,
            innermostFrameSlots = innermostFrameSlots,
            memorySizes = memorySizes,
            memoryAccess = memoryAccess,
        )
    }

    private companion object {
        const val FIELDS = 4
    }
}

private val EMPTY_SLOTS = LongArray(0)

internal const val HEAD_FRAMES = 64
internal const val TAIL_FRAMES = 64
