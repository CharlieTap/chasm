package io.github.charlietap.chasm.runtime.compiled.gc

import java.lang.reflect.Field

interface ShapeAccessors {

    fun struct(
        shape: Class<*>,
        fields: List<Field>,
    ): StructAccessor

    fun array(
        shape: Class<*>,
        data: Field,
    ): ArrayAccessor
}

interface StructAccessor {

    fun readBits(
        struct: Any,
        index: Int,
    ): Long

    fun readReference(
        struct: Any,
        index: Int,
    ): Any?

    fun writeBits(
        struct: Any,
        index: Int,
        value: Long,
    )

    fun writeReference(
        struct: Any,
        index: Int,
        value: Any?,
    )

    fun newStruct(): ManagedStruct
}

interface ArrayAccessor {

    fun newStorage(length: Int): Any

    fun newArray(storage: Any): ManagedArray
}
