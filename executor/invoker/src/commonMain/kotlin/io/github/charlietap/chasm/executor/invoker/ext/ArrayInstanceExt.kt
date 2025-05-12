package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.runtime.instance.ArrayInstance

private const val OBJECT_HEADER_BYTES = 12 // mark-word (8) + klass ptr (4)
private const val POINTER_BYTES = 4 // compressed 64-bit pointer
private const val LONG_ARRAY_HEADER = 16 // 12 header + 4 length int

private const val ARRAY_INSTANCE_SIZE = OBJECT_HEADER_BYTES + (3 * POINTER_BYTES)

private inline fun align(bytes: Int) = (bytes + 7) and 0xFFFFFFF8.toInt()

val ArrayInstance.sizeInBytes: Int
    get() {
        val longArraySize = align(LONG_ARRAY_HEADER + fields.size * Long.SIZE_BYTES)
        return ARRAY_INSTANCE_SIZE + longArraySize
    }
