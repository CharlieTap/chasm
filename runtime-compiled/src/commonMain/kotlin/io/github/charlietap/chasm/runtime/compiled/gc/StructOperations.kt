package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType

internal class StructOperations(
    private val layouts: ShapeLayouts,
    private val allocator: ManagedAllocator,
) {

    fun structType(struct: Any?): HostGcType {
        return layout(ownedStruct(struct)).type
    }

    fun writeStructField(
        struct: Any?,
        index: Int,
        value: Long,
    ) {

        val compiledStruct = ownedStruct(struct)
        val layout = layout(compiledStruct)

        checkFieldIndex(index, layout)
        require(layout.checks[index] == null) { "field $index holds a reference" }
        require(layout.mutable[index]) { "field $index is immutable" }

        layout.storageLayout.writeBits(compiledStruct, index, value)
    }

    fun writeStructReference(
        struct: Any?,
        index: Int,
        value: Any?,
    ) {

        val compiledStruct = ownedStruct(struct)
        val layout = layout(compiledStruct)

        checkFieldIndex(index, layout)
        val check = requireNotNull(layout.checks[index]) { "field $index holds a number" }
        require(layout.mutable[index]) { "field $index is immutable" }
        require(check.accepts(value)) { "the value does not match the type of field $index" }

        layout.storageLayout.writeReference(compiledStruct, index, value)
    }

    fun unsafeWriteStructReference(
        struct: Any?,
        index: Int,
        value: Any?,
    ) {

        val compiledStruct = ownedStruct(struct)

        layout(compiledStruct).storageLayout.writeReference(compiledStruct, index, value)
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
                layout.storageLayout.writeBits(struct, index, fields[index])
            } else {
                layout.storageLayout.writeReference(struct, index, references[index])
            }
        }

        return struct
    }

    private fun ownedStruct(struct: Any?): CompiledStruct {

        require(struct is CompiledStruct) { "not a struct of this store" }

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

    private fun storageLayout(struct: CompiledStruct): StructStorageLayout {
        return requireNotNull(layouts.structStorage(struct)) { "not a struct of this store" }
    }

    private fun layout(struct: CompiledStruct): StructLayout {
        return requireNotNull(layouts.struct(struct)) { "not a struct of this store" }
    }
}
