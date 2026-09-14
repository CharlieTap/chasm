@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.mmap.commit
import io.github.charlietap.chasm.mmap.release
import io.github.charlietap.chasm.mmap.reserve
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.plus
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.toCPointer

internal fun reserveVirtualMemory(bytes: Long): CPointer<ByteVar> = try {
    requireNotNull(reserve(bytes).toLong().toCPointer())
} catch (error: IllegalStateException) {
    throw OutOfMemoryError(error.message)
}

internal fun commitVirtualMemory(
    base: CPointer<ByteVar>,
    offset: Int,
    bytes: Int,
) {
    try {
        commit((base + offset).rawValue, bytes.toLong())
    } catch (error: IllegalStateException) {
        throw OutOfMemoryError(error.message)
    }
}

internal fun releaseVirtualMemory(
    base: CPointer<ByteVar>,
    bytes: Long,
): Boolean = try {
    release(base.rawValue, bytes)
    true
} catch (_: IllegalStateException) {
    false
}
