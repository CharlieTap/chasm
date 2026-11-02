package io.github.charlietap.chasm.runtime.diagnostic

import io.github.charlietap.chasm.runtime.instance.MemoryInstance

/**
 * The access compiled code was making when it trapped: [width] bytes of
 * [memory] from [address], an unsigned effective address. [source] is true
 * when a copy failed on its source rather than its destination.
 */
class FailedMemoryAccess(
    val kind: Kind,
    val memory: MemoryInstance,
    val address: Long,
    val width: Long,
    val source: Boolean = false,
) {
    enum class Kind { READ, WRITE, COPY, FILL, INIT }
}
