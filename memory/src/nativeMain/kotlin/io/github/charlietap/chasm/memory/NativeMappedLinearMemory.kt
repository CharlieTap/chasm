@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlin.experimental.ExperimentalNativeApi::class,
)

package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.NativePtr
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.plus
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.readBits
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.writeBits
import platform.posix.memcpy
import platform.posix.memmove
import platform.posix.memset
import kotlin.concurrent.AtomicLong
import kotlin.native.Platform
import kotlin.native.ref.createCleaner

internal const val MAX_NATIVE_MEMORY_PAGES = Int.MAX_VALUE / LinearMemory.PAGE_SIZE
internal const val MAX_NATIVE_MEMORY_BYTES = MAX_NATIVE_MEMORY_PAGES * LinearMemory.PAGE_SIZE

@PublishedApi
internal class NativeMappedLinearMemory private constructor(
    state: NativeMappedLinearMemoryState,
) : LinearMemory {

    @PublishedApi
    internal val base: CPointer<ByteVar> = state.base

    @PublishedApi
    internal val nativeBase: NativePtr = state.base.rawValue

    private val maximumByteSize: Int = state.maximumByteSize
    private val prefault: Boolean = state.prefault
    private val commitMemory: (CPointer<ByteVar>, Int, Int) -> Unit = state.commitMemory
    private val releaseState: NativeMemoryReleaseState = state.releaseState

    @Suppress("unused")
    private val cleaner = createCleaner(releaseState) { release ->
        release.release()
    }

    override var byteSize: Int = state.initialByteSize
        private set

    internal val baseAddress: Long
        get() = nativeBase.toLong()

    override fun grow(pagesToAdd: Int): LinearMemory {
        require(pagesToAdd >= 0) { "Linear memory cannot shrink" }
        if (pagesToAdd == 0) return this

        val previousByteSize = byteSize
        val nextByteSize = previousByteSize.toLong() + pagesToAdd.toLong() * LinearMemory.PAGE_SIZE
        require(nextByteSize <= maximumByteSize.toLong()) {
            "Native linear memory cannot exceed ${maximumByteSize / LinearMemory.PAGE_SIZE} pages"
        }

        val bytesToCommit = nextByteSize.toInt() - previousByteSize
        commitMemory(base, previousByteSize, bytesToCommit)
        if (prefault) {
            memset(base + previousByteSize, 0, bytesToCommit.convert())
        }
        byteSize = nextByteSize.toInt()
        return this
    }

    override fun readI8(memoryPointer: Int): Byte =
        loadI8Unchecked(checkedAddress(memoryPointer, Byte.SIZE_BYTES, byteSize))

    override fun readI16(memoryPointer: Int): Short =
        loadI16Unchecked(checkedAddress(memoryPointer, Short.SIZE_BYTES, byteSize))

    override fun readI32(memoryPointer: Int): Int =
        loadI32Unchecked(checkedAddress(memoryPointer, Int.SIZE_BYTES, byteSize))

    override fun readI64(memoryPointer: Int): Long =
        loadI64Unchecked(checkedAddress(memoryPointer, Long.SIZE_BYTES, byteSize))

    override fun readF32(memoryPointer: Int): Float =
        Float.fromBits(loadI32Unchecked(checkedAddress(memoryPointer, Float.SIZE_BYTES, byteSize)))

    override fun readF64(memoryPointer: Int): Double =
        Double.fromBits(loadI64Unchecked(checkedAddress(memoryPointer, Double.SIZE_BYTES, byteSize)))

    override fun read(
        buffer: ByteArray,
        memoryPointer: Int,
        bytesToRead: Int,
        bufferPointer: Int,
    ): ByteArray {
        checkedAddress(memoryPointer, bytesToRead, byteSize)
        checkedAddress(bufferPointer, bytesToRead, buffer.size)
        if (bytesToRead == 0) return buffer

        buffer.usePinned { pinned ->
            memcpy(
                pinned.addressOf(bufferPointer),
                base + memoryPointer,
                bytesToRead.convert(),
            )
            Unit
        }
        return buffer
    }

    override fun writeI8(memoryPointer: Int, value: Byte) {
        storeI8Unchecked(checkedAddress(memoryPointer, Byte.SIZE_BYTES, byteSize), value)
    }

    override fun writeI16(memoryPointer: Int, value: Short) {
        storeI16Unchecked(checkedAddress(memoryPointer, Short.SIZE_BYTES, byteSize), value)
    }

    override fun writeI32(memoryPointer: Int, value: Int) {
        storeI32Unchecked(checkedAddress(memoryPointer, Int.SIZE_BYTES, byteSize), value)
    }

    override fun writeI64(memoryPointer: Int, value: Long) {
        storeI64Unchecked(checkedAddress(memoryPointer, Long.SIZE_BYTES, byteSize), value)
    }

    override fun writeF32(memoryPointer: Int, value: Float) {
        storeI32Unchecked(checkedAddress(memoryPointer, Float.SIZE_BYTES, byteSize), value.toRawBits())
    }

    override fun writeF64(memoryPointer: Int, value: Double) {
        storeI64Unchecked(checkedAddress(memoryPointer, Double.SIZE_BYTES, byteSize), value.toRawBits())
    }

    override fun write(
        memoryPointer: Int,
        buffer: ByteArray,
        bufferPointer: Int,
        bytesToWrite: Int,
    ) {
        checkedAddress(memoryPointer, bytesToWrite, byteSize)
        checkedAddress(bufferPointer, bytesToWrite, buffer.size)
        if (bytesToWrite == 0) return

        buffer.usePinned { pinned ->
            memcpy(
                base + memoryPointer,
                pinned.addressOf(bufferPointer),
                bytesToWrite.convert(),
            )
            Unit
        }
    }

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) {
        checkedAddress(memoryPointer, bytesToFill, byteSize)
        fillUnchecked(memoryPointer, value, bytesToFill)
    }

    override fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory,
    ) {
        checkedAddress(sourcePointer, bytesToCopy, source.byteSize)
        checkedAddress(destinationPointer, bytesToCopy, byteSize)
        if (bytesToCopy == 0) return

        if (source is NativeMappedLinearMemory) {
            memcpy(
                base + destinationPointer,
                source.base + sourcePointer,
                bytesToCopy.convert(),
            )
        } else {
            val buffer = ByteArray(bytesToCopy)
            source.read(buffer, sourcePointer, bytesToCopy)
            write(destinationPointer, buffer)
        }
    }

    override fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory,
    ) {
        checkedAddress(sourcePointer, bytesToMove, source.byteSize)
        checkedAddress(destinationPointer, bytesToMove, byteSize)
        if (bytesToMove == 0 || source === this && sourcePointer == destinationPointer) return

        if (source is NativeMappedLinearMemory) {
            memmove(
                base + destinationPointer,
                source.base + sourcePointer,
                bytesToMove.convert(),
            )
        } else {
            val buffer = ByteArray(bytesToMove)
            source.read(buffer, sourcePointer, bytesToMove)
            write(destinationPointer, buffer)
        }
    }

    @PublishedApi
    internal inline fun loadI8Unchecked(address: Int): Byte =
        readBits(nativeBase + address.toLong(), 0, Byte.SIZE_BITS, signed = true).toByte()

    @PublishedApi
    internal inline fun loadI16Unchecked(address: Int): Short =
        readBits(nativeBase + address.toLong(), 0, Short.SIZE_BITS, signed = true).toShort()

    @PublishedApi
    internal inline fun loadI32Unchecked(address: Int): Int =
        readBits(nativeBase + address.toLong(), 0, Int.SIZE_BITS, signed = true).toInt()

    @PublishedApi
    internal inline fun loadI64Unchecked(address: Int): Long =
        readBits(nativeBase + address.toLong(), 0, Long.SIZE_BITS, signed = true)

    @PublishedApi
    internal inline fun storeI8Unchecked(address: Int, value: Byte) {
        writeBits(nativeBase + address.toLong(), 0, Byte.SIZE_BITS, value.toLong())
    }

    @PublishedApi
    internal inline fun storeI16Unchecked(address: Int, value: Short) {
        writeBits(nativeBase + address.toLong(), 0, Short.SIZE_BITS, value.toLong())
    }

    @PublishedApi
    internal inline fun storeI32Unchecked(address: Int, value: Int) {
        writeBits(nativeBase + address.toLong(), 0, Int.SIZE_BITS, value.toLong())
    }

    @PublishedApi
    internal inline fun storeI64Unchecked(address: Int, value: Long) {
        writeBits(nativeBase + address.toLong(), 0, Long.SIZE_BITS, value)
    }

    @PublishedApi
    internal fun fillUnchecked(address: Int, value: Byte, bytesToFill: Int) {
        if (bytesToFill != 0) {
            memset(base + address, value.toInt(), bytesToFill.convert())
        }
    }

    @PublishedApi
    internal fun moveFromUnchecked(
        source: NativeMappedLinearMemory,
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
    ) {
        if (bytesToMove != 0) {
            memmove(
                base + destinationPointer,
                source.base + sourcePointer,
                bytesToMove.convert(),
            )
        }
    }

    @PublishedApi
    internal fun writeFromUnchecked(
        source: ByteArray,
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToWrite: Int,
    ) {
        if (bytesToWrite == 0) return
        source.usePinned { pinned ->
            memcpy(
                base + destinationPointer,
                pinned.addressOf(sourcePointer),
                bytesToWrite.convert(),
            )
            Unit
        }
    }

    internal fun release() {
        check(releaseState.release()) { "Unable to release native linear memory" }
        byteSize = 0
    }

    companion object {
        internal fun create(
            pages: LinearMemory.Pages,
            maximumPages: LinearMemory.Pages? = null,
            config: LinearMemoryConfig = LinearMemoryConfig(),
            operations: NativeVirtualMemoryOperations = systemNativeVirtualMemoryOperations,
        ): NativeMappedLinearMemory {
            val state = createNativeMappedLinearMemoryState(pages, maximumPages, config, operations)
            return try {
                NativeMappedLinearMemory(state)
            } catch (error: Throwable) {
                state.releaseState.release()
                throw error
            }
        }
    }
}

internal class NativeMemoryReleaseState(
    base: CPointer<ByteVar>,
    private val reservedByteSize: Long,
    private val releaseMemory: (CPointer<ByteVar>, Long) -> Boolean,
) {
    private val address = AtomicLong(base.rawValue.toLong())

    fun release(): Boolean {
        var rawAddress: Long
        while (true) {
            rawAddress = address.value
            if (rawAddress == RELEASED) return true
            if (rawAddress == RELEASING) continue
            if (address.compareAndSet(rawAddress, RELEASING)) break
        }

        val base = rawAddress.toCPointer<ByteVar>() ?: run {
            address.value = rawAddress
            return false
        }
        val released = try {
            releaseMemory(base, reservedByteSize)
        } catch (_: Throwable) {
            false
        }
        address.value = if (released) RELEASED else rawAddress
        return released
    }

    private companion object {
        const val RELEASED = 0L
        const val RELEASING = -1L
    }
}

internal class NativeVirtualMemoryOperations(
    val reserve: (Long) -> CPointer<ByteVar>,
    val commit: (CPointer<ByteVar>, Int, Int) -> Unit,
    val release: (CPointer<ByteVar>, Long) -> Boolean,
)

private val systemNativeVirtualMemoryOperations = NativeVirtualMemoryOperations(
    reserve = ::reserveVirtualMemory,
    commit = ::commitVirtualMemory,
    release = ::releaseVirtualMemory,
)

private class NativeMappedLinearMemoryState(
    val base: CPointer<ByteVar>,
    val initialByteSize: Int,
    val maximumByteSize: Int,
    val prefault: Boolean,
    val commitMemory: (CPointer<ByteVar>, Int, Int) -> Unit,
    val releaseState: NativeMemoryReleaseState,
)

private fun createNativeMappedLinearMemoryState(
    pages: LinearMemory.Pages,
    maximumPages: LinearMemory.Pages?,
    config: LinearMemoryConfig,
    operations: NativeVirtualMemoryOperations,
): NativeMappedLinearMemoryState {
    check(Platform.isLittleEndian) {
        "Native mapped linear memory requires a little-endian target"
    }
    check(Platform.canAccessUnaligned) {
        "Native mapped linear memory requires unaligned scalar access"
    }

    val initialPages = pages.amount.toLong()
    require(initialPages <= MAX_NATIVE_MEMORY_PAGES) {
        "Native linear memory cannot exceed $MAX_NATIVE_MEMORY_PAGES pages"
    }
    val declaredMaximumPages = maximumPages?.amount?.toLong()
    require(declaredMaximumPages == null || declaredMaximumPages >= initialPages) {
        "Linear memory maximum cannot be smaller than its initial size"
    }

    val maximumPageCount = minOf(
        declaredMaximumPages ?: MAX_NATIVE_MEMORY_PAGES.toLong(),
        MAX_NATIVE_MEMORY_PAGES.toLong(),
    )
    val initialByteSize = (initialPages * LinearMemory.PAGE_SIZE).toInt()
    val maximumByteSize = (maximumPageCount * LinearMemory.PAGE_SIZE).toInt()
    val reservedByteSize = maxOf(maximumByteSize.toLong(), LinearMemory.PAGE_SIZE.toLong())
    val base = operations.reserve(reservedByteSize)
    val releaseState = NativeMemoryReleaseState(base, reservedByteSize, operations.release)

    try {
        operations.commit(base, 0, initialByteSize)
        if (config.prefault && initialByteSize != 0) {
            memset(base, 0, initialByteSize.convert())
        }
        return NativeMappedLinearMemoryState(
            base = base,
            initialByteSize = initialByteSize,
            maximumByteSize = maximumByteSize,
            prefault = config.prefault,
            commitMemory = operations.commit,
            releaseState = releaseState,
        )
    } catch (error: Throwable) {
        if (!releaseState.release()) {
            error.addSuppressed(IllegalStateException("Unable to release failed native linear-memory allocation"))
        }
        throw error
    }
}

@PublishedApi
internal fun checkedAddress(address: Int, byteCount: Int, byteSize: Int): Int {
    if (address < 0 || byteCount < 0 || address > byteSize - byteCount) {
        throw IndexOutOfBoundsException()
    }
    return address
}
