@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.charlietap.chasm.mmap

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.NativePtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.toCPointer
import platform.posix.MAP_ANON
import platform.posix.MAP_FAILED
import platform.posix.MAP_PRIVATE
import platform.posix.PROT_NONE
import platform.posix.PROT_READ
import platform.posix.PROT_WRITE
import platform.posix.errno
import platform.posix.mmap
import platform.posix.mprotect
import platform.posix.munmap

internal actual fun platformReserve(byteCount: Long): NativePtr {
    val mapping = mmap(
        null,
        byteCount.convert(),
        PROT_NONE,
        MAP_PRIVATE or MAP_ANON,
        -1,
        0,
    )
    if (mapping == null || mapping == MAP_FAILED) {
        error("Unable to reserve $byteCount bytes: errno=$errno")
    }
    return mapping.rawValue
}

internal actual fun platformCommit(
    address: NativePtr,
    byteCount: Long,
) {
    val pointer = requireNotNull(address.toLong().toCPointer<ByteVar>())
    if (mprotect(pointer, byteCount.convert(), PROT_READ or PROT_WRITE) != 0) {
        error("Unable to commit $byteCount bytes: errno=$errno")
    }
}

internal actual fun platformRelease(
    address: NativePtr,
    byteCount: Long,
) {
    val pointer = requireNotNull(address.toLong().toCPointer<ByteVar>())
    if (munmap(pointer, byteCount.convert()) != 0) {
        error("Unable to release $byteCount bytes: errno=$errno")
    }
}
