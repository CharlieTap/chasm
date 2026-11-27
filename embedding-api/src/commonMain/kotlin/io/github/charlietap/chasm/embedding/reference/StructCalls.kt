package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.StructReference

fun structType(struct: StructReference): HostGcType {
    return struct.engine().structType(struct)
}

fun readStructField(
    struct: StructReference,
    index: Int,
): Long {
    return struct.engine().readStructField(struct, index)
}

fun readStructFields(
    struct: StructReference,
    offset: Int,
    buffer: LongArray,
    bufferOffset: Int = 0,
    count: Int = buffer.size - bufferOffset,
) {
    struct.engine().readStructFields(struct, offset, buffer, bufferOffset, count)
}

fun readStructReference(
    struct: StructReference,
    index: Int,
): Any? {
    return struct.engine().readStructReference(struct, index)
}

fun writeStructField(
    struct: StructReference,
    index: Int,
    value: Long,
) {
    struct.engine().writeStructField(struct, index, value)
}

fun writeStructReference(
    struct: StructReference,
    index: Int,
    value: Any?,
) {
    struct.engine().writeStructReference(struct, index, value)
}

@UnsafeHostApi
fun unsafeWriteStructReference(
    struct: StructReference,
    index: Int,
    value: Any?,
) {
    struct.engine().unsafeWriteStructReference(struct, index, value)
}
