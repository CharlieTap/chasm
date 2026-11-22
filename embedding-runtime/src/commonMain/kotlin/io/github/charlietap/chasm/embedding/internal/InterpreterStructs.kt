package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.host.withScope
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability

internal interface StructAccess {

    fun structType(struct: StructReference): HostGcType

    fun readStructField(
        struct: StructReference,
        index: Int,
    ): Long

    fun readStructReference(
        struct: StructReference,
        index: Int,
    ): Any?

    fun writeStructField(
        struct: StructReference,
        index: Int,
        value: Long,
    )

    fun writeStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    )

    fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference
}

internal class InterpreterStructs(
    private val codec: ReferenceCodec,
    private val heldReferences: HeldReferences,
    private val allocator: GcAllocator,
    private val heap: WasmHeap,
) : StructAccess {

    override fun structType(struct: StructReference): HostGcType {
        return HostGcType(codec.struct(struct).runtimeType)
    }

    override fun readStructField(
        struct: StructReference,
        index: Int,
    ): Long {

        val held = codec.struct(struct)
        val field = heap.runtimeStructFieldType(HostGcType(held.runtimeType), index)
        require(referenceType(field) == null) { "field $index holds a reference" }

        return withAccessorFailures {
            packedValue(field, heap.getStructFieldTrusted(held.raw, index))
        }
    }

    override fun readStructReference(
        struct: StructReference,
        index: Int,
    ): Any? {

        val held = codec.struct(struct)
        val field = heap.runtimeStructFieldType(HostGcType(held.runtimeType), index)
        requireNotNull(referenceType(field)) { "field $index holds a number" }

        return withAccessorFailures {
            codec.decode(heap.getStructFieldTrusted(held.raw, index))
        }
    }

    override fun writeStructField(
        struct: StructReference,
        index: Int,
        value: Long,
    ) {

        val held = codec.struct(struct)
        val field = heap.runtimeStructFieldType(HostGcType(held.runtimeType), index)
        require(referenceType(field) == null) { "field $index holds a reference" }
        requireMutable(field, index)

        withAccessorFailures {
            heap.setStructFieldTrusted(held.raw, index, value)
        }
    }

    override fun writeStructReference(
        struct: StructReference,
        index: Int,
        value: Any?,
    ) {

        val held = codec.struct(struct)
        val type = HostGcType(held.runtimeType)
        val reference = requireNotNull(codec.structFieldTypes(type)[index]) { "field $index holds a number" }
        requireMutable(heap.runtimeStructFieldType(type, index), index)

        withAccessorFailures {
            heap.withScope {
                heap.rootScoped(held.raw)
                heap.setStructFieldTrusted(held.raw, index, codec.encode(reference, value))
            }
        }
    }

    override fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        references: Array<Any?>,
    ): StructReference {

        require(heap.isStructType(type)) { "$type is not a struct type" }

        val fieldTypes = codec.structFieldTypes(type)
        val count = fieldTypes.size
        val hasReferences = fieldTypes.any { fieldType -> fieldType != null }
        require(fields.size >= count) { "the struct has $count fields" }
        require(!hasReferences || references.size >= count) { "the struct has $count fields" }

        return withAccessorFailures {
            heap.withScope {
                val words = LongArray(count) { index ->
                    val reference = fieldTypes[index]
                    if (reference == null) {
                        fields[index]
                    } else {
                        codec.encode(reference, references[index])
                    }
                }
                heldReferences.struct(allocator.struct(RTT(type.id), words), type.id)
            }
        }
    }

    private fun requireMutable(
        field: FieldType,
        index: Int,
    ) {
        require(field.mutability == Mutability.Var) { "field $index is immutable" }
    }
}
