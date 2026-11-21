package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.StructReference

fun structType(struct: StructReference): HostGcType {
    return engineOf(struct).structType(struct)
}

fun readStructField(
    struct: StructReference,
    index: Int,
): Long {
    return engineOf(struct).readStructField(struct, index)
}

fun readStructReference(
    struct: StructReference,
    index: Int,
): Any? {
    return engineOf(struct).readStructReference(struct, index)
}

fun writeStructField(
    struct: StructReference,
    index: Int,
    value: Long,
) {
    engineOf(struct).writeStructField(struct, index, value)
}

fun writeStructReference(
    struct: StructReference,
    index: Int,
    value: Any?,
) {
    engineOf(struct).writeStructReference(struct, index, value)
}

@UnsafeHostApi
fun unsafeWriteStructReference(
    struct: StructReference,
    index: Int,
    value: Any?,
) {
    engineOf(struct).unsafeWriteStructReference(struct, index, value)
}
