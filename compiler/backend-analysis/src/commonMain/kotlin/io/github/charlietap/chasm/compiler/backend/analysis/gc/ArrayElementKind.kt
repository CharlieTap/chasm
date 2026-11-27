package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.abi.FILL_BYTES
import io.github.charlietap.chasm.compiler.backend.abi.FILL_CHARS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_DOUBLES
import io.github.charlietap.chasm.compiler.backend.abi.FILL_FLOATS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_INTS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_LONGS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
enum class ArrayElementKind(
    val descriptor: String,
    val shift: Int,
    val rangeFill: MethodRef,
) {
    BYTE("B", 0, FILL_BYTES),
    CHAR("C", 1, FILL_CHARS),
    INT("I", 2, FILL_INTS),
    LONG("J", 3, FILL_LONGS),
    FLOAT("F", 2, FILL_FLOATS),
    DOUBLE("D", 3, FILL_DOUBLES),
    REFERENCE(OBJECT_TYPE, 2, FILL_OBJECTS),
    ;

    val arrayDescriptor: String = "[$descriptor"
}

@InternalChasmApi
fun arrayElementKind(type: FieldType): ArrayElementKind {
    return when (val storage = type.storageType) {
        is StorageType.Packed -> when (storage.type) {
            PackedType.I8 -> ArrayElementKind.BYTE
            PackedType.I16 -> ArrayElementKind.CHAR
        }
        is StorageType.Value -> when (val value = storage.type) {
            is ValueType.Number -> when (value.numberType) {
                NumberType.I32 -> ArrayElementKind.INT
                NumberType.I64 -> ArrayElementKind.LONG
                NumberType.F32 -> ArrayElementKind.FLOAT
                NumberType.F64 -> ArrayElementKind.DOUBLE
            }
            else -> ArrayElementKind.REFERENCE
        }
    }
}
