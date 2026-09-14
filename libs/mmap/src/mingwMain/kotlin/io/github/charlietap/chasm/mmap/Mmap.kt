@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.charlietap.chasm.mmap

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.NativePtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.toCPointer
import platform.windows.GetLastError
import platform.windows.MEM_COMMIT
import platform.windows.MEM_RELEASE
import platform.windows.MEM_RESERVE
import platform.windows.PAGE_NOACCESS
import platform.windows.PAGE_READWRITE
import platform.windows.VirtualAlloc
import platform.windows.VirtualFree

internal actual fun platformReserve(byteCount: Long): NativePtr =
    VirtualAlloc(
        null,
        byteCount.convert(),
        MEM_RESERVE.toUInt(),
        PAGE_NOACCESS.toUInt(),
    )?.rawValue
        ?: error("Unable to reserve $byteCount bytes: error=${GetLastError()}")

internal actual fun platformCommit(
    address: NativePtr,
    byteCount: Long,
) {
    val pointer = requireNotNull(address.toLong().toCPointer<ByteVar>())
    if (
        VirtualAlloc(
            pointer,
            byteCount.convert(),
            MEM_COMMIT.toUInt(),
            PAGE_READWRITE.toUInt(),
        ) == null
    ) {
        error("Unable to commit $byteCount bytes: error=${GetLastError()}")
    }
}

internal actual fun platformRelease(
    address: NativePtr,
    byteCount: Long,
) {
    val pointer = requireNotNull(address.toLong().toCPointer<ByteVar>())
    if (VirtualFree(pointer, 0.convert(), MEM_RELEASE.toUInt()) == 0) {
        error("Unable to release $byteCount bytes: error=${GetLastError()}")
    }
}
