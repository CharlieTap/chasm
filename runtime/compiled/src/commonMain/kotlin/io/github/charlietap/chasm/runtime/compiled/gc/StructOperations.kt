package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType
import java.lang.reflect.Field

class StructOperations(
    private val layouts: ShapeLayouts,
    private val allocator: ManagedAllocator,
) {

    fun structType(struct: Any?): HostGcType {
        return layout(struct).type
    }

    fun readStructField(
        struct: Any?,
        index: Int,
    ): Long {

        val layout = layout(struct)

        checkIndex(index, layout.fields.size)
        require(layout.checks[index] == null) { "field $index holds a reference" }

        return readNumber(layout.fields[index], struct)
    }

    fun readStructReference(
        struct: Any?,
        index: Int,
    ): Any? {

        val layout = layout(struct)

        checkIndex(index, layout.fields.size)
        requireNotNull(layout.checks[index]) { "field $index holds a number" }

        return layout.fields[index].get(struct)
    }

    fun writeStructField(
        struct: Any?,
        index: Int,
        value: Long,
    ) {

        val layout = layout(struct)

        checkIndex(index, layout.fields.size)
        require(layout.checks[index] == null) { "field $index holds a reference" }
        require(layout.mutable[index]) { "field $index is immutable" }

        writeNumber(layout.fields[index], struct, value)
    }

    fun writeStructReference(
        struct: Any?,
        index: Int,
        value: Any?,
    ) {

        val layout = layout(struct)

        checkIndex(index, layout.fields.size)
        val check = requireNotNull(layout.checks[index]) { "field $index holds a number" }
        require(layout.mutable[index]) { "field $index is immutable" }
        require(check.accepts(value)) { "the value does not match the type of field $index" }

        layout.fields[index].set(struct, value)
    }

    fun unsafeWriteStructReference(
        struct: Any?,
        index: Int,
        value: Any?,
    ) {
        layout(struct).fields[index].set(struct, value)
    }

    fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): ManagedStruct {

        val layout = layouts.struct(type)
        val count = layout.fields.size

        require(fields.size >= count && references.size >= count) { "the struct has $count fields" }
        layout.checks.forEachIndexed { index, check ->
            require(check == null || check.accepts(references[index])) { "the value does not match the type of field $index" }
        }

        return fill(layout, fields, references)
    }

    fun unsafeAllocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): ManagedStruct {
        return fill(layouts.struct(type), fields, references)
    }

    private fun fill(
        layout: StructLayout,
        fields: LongArray,
        references: Array<Any?>,
    ): ManagedStruct {

        val struct = allocator.newStruct(layout)

        layout.fields.forEachIndexed { index, field ->
            if (layout.checks[index] == null) {
                writeNumber(field, struct, fields[index])
            } else {
                field.set(struct, references[index])
            }
        }

        return struct
    }

    private fun layout(struct: Any?): StructLayout {
        return requireNotNull(struct?.let(layouts::struct)) { "not a struct of this store" }
    }

    private fun readNumber(
        field: Field,
        struct: Any?,
    ): Long {
        return when (field.type) {
            Int::class.javaPrimitiveType -> field.getInt(struct).toLong()
            Long::class.javaPrimitiveType -> field.getLong(struct)
            Float::class.javaPrimitiveType -> field.getFloat(struct).toRawBits().toLong()
            Double::class.javaPrimitiveType -> field.getDouble(struct).toRawBits()
            Byte::class.javaPrimitiveType -> field.getByte(struct).toLong() and BYTE_MASK
            else -> field.getChar(struct).code.toLong()
        }
    }

    private fun writeNumber(
        field: Field,
        struct: Any?,
        value: Long,
    ) {
        when (field.type) {
            Int::class.javaPrimitiveType -> field.setInt(struct, value.toInt())
            Long::class.javaPrimitiveType -> field.setLong(struct, value)
            Float::class.javaPrimitiveType -> field.setFloat(struct, Float.fromBits(value.toInt()))
            Double::class.javaPrimitiveType -> field.setDouble(struct, Double.fromBits(value))
            Byte::class.javaPrimitiveType -> field.setByte(struct, value.toByte())
            else -> field.setChar(struct, value.toInt().toChar())
        }
    }

    private companion object {
        const val BYTE_MASK = 0xFFL
    }
}
