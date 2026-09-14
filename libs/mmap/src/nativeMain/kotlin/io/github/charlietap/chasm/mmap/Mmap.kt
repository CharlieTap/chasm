@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.charlietap.chasm.mmap

import kotlinx.cinterop.NativePtr

/**
 * Reserves an inaccessible virtual address range.
 *
 * @param byteCount the positive number of bytes to reserve
 * @return the base address of the reservation
 */
public fun reserve(byteCount: Long): NativePtr {
    require(byteCount > 0) { "Reservation size must be positive" }
    return platformReserve(byteCount)
}

/**
 * Commits a range inside a reservation as readable and writable memory.
 *
 * The address and size must satisfy the platform's page-alignment requirements.
 * Committing an empty range has no effect.
 */
public fun commit(
    address: NativePtr,
    byteCount: Long,
) {
    require(byteCount >= 0) { "Commit size cannot be negative" }
    if (byteCount == 0L) return
    require(address.toLong() != 0L) { "Commit address cannot be null" }
    platformCommit(address, byteCount)
}

/**
 * Releases an entire reservation.
 *
 * The address must be one returned by [reserve], and [byteCount] must be the
 * corresponding reservation size.
 */
public fun release(
    address: NativePtr,
    byteCount: Long,
) {
    require(address.toLong() != 0L) { "Release address cannot be null" }
    require(byteCount > 0) { "Release size must be positive" }
    platformRelease(address, byteCount)
}

internal expect fun platformReserve(byteCount: Long): NativePtr

internal expect fun platformCommit(
    address: NativePtr,
    byteCount: Long,
)

internal expect fun platformRelease(
    address: NativePtr,
    byteCount: Long,
)
