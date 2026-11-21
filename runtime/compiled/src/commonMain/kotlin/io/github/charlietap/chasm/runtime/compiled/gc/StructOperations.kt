package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType

class StructOperations(
    private val layouts: ShapeLayouts,
    private val allocator: ManagedAllocator,
) {

    fun structType(struct: Any?): HostGcType {
        return layout(ownedStruct(struct)).type
    }

    fun readStructField(
        struct: Any?,
        index: Int,
    ): Long {

        val managedStruct = ownedStruct(struct)

        return layout(managedStruct).accessor.readBits(managedStruct, index)
    }

    fun readStructReference(
        struct: Any?,
        index: Int,
    ): Any? {

        val managedStruct = ownedStruct(struct)

        return layout(managedStruct).accessor.readReference(managedStruct, index)
    }

    fun writeStructField(
        struct: Any?,
        index: Int,
        value: Long,
    ) {

        val managedStruct = ownedStruct(struct)
        val layout = layout(managedStruct)

        checkFieldIndex(index, layout)
        require(layout.checks[index] == null) { "field $index holds a reference" }
        require(layout.mutable[index]) { "field $index is immutable" }

        layout.accessor.writeBits(managedStruct, index, value)
    }

    fun writeStructReference(
        struct: Any?,
        index: Int,
        value: Any?,
    ) {

        val managedStruct = ownedStruct(struct)
        val layout = layout(managedStruct)

        checkFieldIndex(index, layout)
        val check = requireNotNull(layout.checks[index]) { "field $index holds a number" }
        require(layout.mutable[index]) { "field $index is immutable" }
        require(check.accepts(value)) { "the value does not match the type of field $index" }

        layout.accessor.writeReference(managedStruct, index, value)
    }

    fun unsafeWriteStructReference(
        struct: Any?,
        index: Int,
        value: Any?,
    ) {

        val managedStruct = ownedStruct(struct)

        layout(managedStruct).accessor.writeReference(managedStruct, index, value)
    }

    fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): ManagedStruct {

        val layout = layouts.struct(type)
        val count = layout.checks.size

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

        layout.checks.forEachIndexed { index, check ->
            if (check == null) {
                layout.accessor.writeBits(struct, index, fields[index])
            } else {
                layout.accessor.writeReference(struct, index, references[index])
            }
        }

        return struct
    }

    private fun ownedStruct(struct: Any?): ManagedStruct {

        require(struct is ManagedStruct) { "not a struct of this store" }

        return struct
    }

    private fun checkFieldIndex(
        index: Int,
        layout: StructLayout,
    ) {
        if (index !in layout.checks.indices) {
            throw fieldIndexError(index)
        }
    }

    private fun layout(struct: ManagedStruct): StructLayout {
        return requireNotNull(layouts.struct(struct)) { "not a struct of this store" }
    }
}
