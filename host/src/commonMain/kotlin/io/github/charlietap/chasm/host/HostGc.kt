package io.github.charlietap.chasm.host

/**
 * `HostGc` is an API for inspecting, changing, and allocating Wasm structs and
 * arrays in a Chasm store, and for running its garbage collector.
 *
 * Struct fields and array elements are read and written as unboxed [Long]
 * words; use [HostGcFieldInfo] to interpret them. Callers own bulk buffers. New
 * allocations are added to the active host reference scope.
 *
 * This is a trusted API. References, types, fields, and indexes must match the
 * guest module's declarations.
 */
interface HostGc {

    val allocatedBytes: Long

    context(resources: HostResources)
    fun collect()

    context(resources: HostResources)
    fun collect(
        additionalRoots: LongArray,
        rootOffset: Int = 0,
        rootCount: Int = additionalRoots.size - rootOffset,
    )

    fun structType(reference: HostReference): HostGcType

    fun structFieldCount(type: HostGcType): Int

    fun structFieldInfo(type: HostGcType, fieldIndex: Int): HostGcFieldInfo

    fun readStructField(reference: HostReference, fieldIndex: Int): Long

    fun writeStructField(reference: HostReference, fieldIndex: Int, value: Long)

    fun arrayType(reference: HostReference): HostGcType

    fun arrayElementInfo(type: HostGcType): HostGcFieldInfo

    fun arrayLength(reference: HostReference): Int

    fun readArrayElement(reference: HostReference, index: Int): Long

    fun writeArrayElement(reference: HostReference, index: Int, value: Long)

    fun fillArray(reference: HostReference, offset: Int, length: Int, value: Long)

    fun copyArray(
        source: HostReference,
        sourceOffset: Int,
        destination: HostReference,
        destinationOffset: Int,
        length: Int,
    )

    fun readArrayElements(
        reference: HostReference,
        sourceOffset: Int,
        destination: LongArray,
        destinationOffset: Int,
        length: Int,
    ): LongArray

    fun writeArrayElements(
        reference: HostReference,
        destinationOffset: Int,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    )

    fun runtimeType(module: HostModuleInstance, typeIndex: Int): HostGcType

    fun isSubtype(actual: HostGcType, expected: HostGcType): Boolean

    context(resources: HostResources)
    fun allocateStruct(
        type: HostGcType,
        fields: LongArray,
        fieldOffset: Int = 0,
    ): HostReference

    context(resources: HostResources)
    fun allocateArray(
        type: HostGcType,
        length: Int,
        initialValue: Long,
    ): HostReference

    context(resources: HostResources)
    fun allocateArray(
        type: HostGcType,
        elements: LongArray,
        elementOffset: Int = 0,
        elementCount: Int = elements.size - elementOffset,
    ): HostReference
}
