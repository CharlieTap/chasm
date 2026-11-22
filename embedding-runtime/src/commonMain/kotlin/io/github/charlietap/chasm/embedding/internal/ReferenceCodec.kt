package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

internal interface ReferenceCodec {

    fun decode(raw: Long): Any?

    fun encode(
        type: ResolvedReferenceType,
        value: Any?,
    ): Long

    fun matches(
        type: ResolvedReferenceType,
        raw: Long,
    ): Boolean

    fun resolve(type: ReferenceType): ResolvedReferenceType

    fun slots(types: List<ValueType>): ReferenceSlots

    fun structFieldTypes(type: HostGcType): Array<ResolvedReferenceType?>

    fun arrayElementType(type: HostGcType): ResolvedReferenceType

    fun struct(reference: StructReference): HeldStruct

    fun array(reference: ArrayReference): HeldArray

    fun exception(reference: ExceptionReference): HeldException

    fun function(reference: FunctionReference): Int
}

internal class InterpreterReferenceCodec(
    private val binding: EngineBinding,
    private val store: StoreRuntime,
    private val heldReferences: HeldReferences,
    private val heap: WasmHeap,
) : ReferenceCodec {

    private var structFieldTypes = arrayOfNulls<Array<ResolvedReferenceType?>>(0)
    private var arrayElementTypes = arrayOfNulls<ResolvedReferenceType>(0)

    override fun decode(raw: Long): Any? {
        return when (raw and RV_TYPE_MASK) {
            RV_TYPE_NULL -> null
            RV_TYPE_I31 -> I31Reference((raw shr RV_SHIFT_BITS).toInt())
            RV_TYPE_STRUCT -> heldReferences.struct(raw, heap.structRuntimeTypeIdOrNegative(raw))
            RV_TYPE_ARRAY -> heldReferences.array(raw, heap.arrayRuntimeTypeIdOrNegative(raw))
            RV_TYPE_EXCEPTION -> heldReferences.exception(raw)
            RV_TYPE_FUNCTION -> InterpreterFunctionReference(binding, (raw shr RV_SHIFT_BITS).toInt())
            RV_TYPE_HOST -> heap.hostValue(raw)
            RV_TYPE_EXTERN -> decode(raw shr RV_SHIFT_BITS)
            else -> error("not a reference word: $raw")
        }
    }

    override fun encode(
        type: ResolvedReferenceType,
        value: Any?,
    ): Long {

        if (value == null) {
            require(type.nullable) { "null is not a value of ${type.referenceType}" }
            return type.nullWord
        }

        val heapType = type.referenceType.heapType
        if (heapType == AbstractHeapType.Extern) {
            return externalized(value)
        }

        if (heapType == AbstractHeapType.Any && hostObject(value)) {
            return scopedExtern(value) shr RV_SHIFT_BITS
        }

        val raw = internal(value)
        require(matches(type, raw)) { "$value is not a value of ${type.referenceType}" }

        return raw
    }

    override fun matches(
        type: ResolvedReferenceType,
        raw: Long,
    ): Boolean {

        val tag = raw and RV_TYPE_MASK
        if (tag == RV_TYPE_NULL) {
            return type.nullable
        }

        if (type.runtimeType == NO_RUNTIME_TYPE) {
            return type.acceptedTags and tagBit(tag) != 0
        }

        val actual = when (tag) {
            RV_TYPE_STRUCT -> heap.structRuntimeTypeIdOrNegative(raw)
            RV_TYPE_ARRAY -> heap.arrayRuntimeTypeIdOrNegative(raw)
            RV_TYPE_FUNCTION -> functionRuntimeType(raw)
            else -> NO_RUNTIME_TYPE
        }

        return actual == type.runtimeType ||
            actual != NO_RUNTIME_TYPE && heap.matchesRuntimeType(RTT(actual), RTT(type.runtimeType))
    }

    override fun resolve(type: ReferenceType): ResolvedReferenceType {

        val heapType = type.heapType
        val runtimeType = if (heapType is ConcreteHeapType.Defined) {
            heap.registerRuntimeType(heapType.definedType).value
        } else {
            NO_RUNTIME_TYPE
        }

        return ResolvedReferenceType(
            referenceType = type,
            nullable = type is ReferenceType.RefNull,
            nullWord = (HeapTypeEncoder(nullHeapType(heapType)).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL,
            acceptedTags = acceptedTags(heapType),
            runtimeType = runtimeType,
        )
    }

    override fun slots(types: List<ValueType>): ReferenceSlots {

        val valueIndices = types.indices.filter { index -> referenceType(types[index]) != null }.toIntArray()
        val resolved = Array(valueIndices.size) { position ->
            resolve(requireNotNull(referenceType(types[valueIndices[position]])))
        }

        return ReferenceSlots(types.size, valueIndices, resolved)
    }

    override fun structFieldTypes(type: HostGcType): Array<ResolvedReferenceType?> {

        val id = type.id
        if (id >= structFieldTypes.size) {
            structFieldTypes = structFieldTypes.copyOf(maxOf(id + 1, structFieldTypes.size * 2))
        }

        val cached = structFieldTypes[id]
        if (cached != null) {
            return cached
        }

        val resolved = Array(heap.structFieldCount(type)) { index ->
            referenceType(heap.runtimeStructFieldType(type, index))?.let(::resolve)
        }
        structFieldTypes[id] = resolved

        return resolved
    }

    override fun arrayElementType(type: HostGcType): ResolvedReferenceType {

        val id = type.id
        if (id >= arrayElementTypes.size) {
            arrayElementTypes = arrayElementTypes.copyOf(maxOf(id + 1, arrayElementTypes.size * 2))
        }

        val cached = arrayElementTypes[id]
        if (cached != null) {
            return cached
        }

        val resolved = resolve(requireNotNull(referenceType(heap.runtimeArrayFieldType(type))) { "the array type holds numbers" })
        arrayElementTypes[id] = resolved

        return resolved
    }

    override fun struct(reference: StructReference): HeldStruct {

        require(reference is HeldStruct) { "not a struct of this store" }
        requireOwned(reference.engine() === binding.engine)

        return reference
    }

    override fun array(reference: ArrayReference): HeldArray {

        require(reference is HeldArray) { "not an array of this store" }
        requireOwned(reference.engine() === binding.engine)

        return reference
    }

    override fun exception(reference: ExceptionReference): HeldException {

        require(reference is HeldException) { "not an exception of this store" }
        requireOwned(reference.engine() === binding.engine)

        return reference
    }

    override fun function(reference: FunctionReference): Int {

        require(reference is InterpreterFunctionReference) { "not a function of this store" }
        requireOwned(reference.engine() === binding.engine)

        return reference.address
    }

    private fun externalized(value: Any): Long {

        if (internalizable(value)) {
            return (internal(value) shl RV_SHIFT_BITS) or RV_TYPE_EXTERN
        }

        return scopedExtern(value)
    }

    private fun internalizable(value: Any): Boolean {
        return value is I31Reference ||
            value is HeldStruct && value.engine() === binding.engine ||
            value is HeldArray && value.engine() === binding.engine
    }

    private fun scopedExtern(value: Any): Long {
        return heap.createScopedExtern(store.liveStore(), value)
    }

    private fun hostObject(value: Any): Boolean {
        return value !is I31Reference &&
            value !is StructReference &&
            value !is ArrayReference &&
            value !is ExceptionReference &&
            value !is FunctionReference
    }

    private fun internal(value: Any): Long {
        return when (value) {
            is I31Reference -> (value.value.toLong() shl RV_SHIFT_BITS) or RV_TYPE_I31
            is HeldStruct -> rooted(struct(value).raw)
            is HeldArray -> rooted(array(value).raw)
            is HeldException -> rooted(exception(value).raw)
            is InterpreterFunctionReference -> (function(value).toLong() shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION
            else -> throw IllegalArgumentException("$value is not a reference of this store")
        }
    }

    private fun functionRuntimeType(raw: Long): Int {

        val function = store.liveStore().functions.getOrNull((raw shr RV_SHIFT_BITS).toInt()) ?: return NO_RUNTIME_TYPE

        return function.rtt.value
    }

    private fun nullHeapType(heapType: HeapType): HeapType {
        return when (heapType) {
            is ConcreteHeapType.Defined -> {
                val definedType = heapType.definedType
                when (definedType.recursiveType.subTypes[definedType.recursiveTypeIndex].compositeType) {
                    is CompositeType.Function -> AbstractHeapType.NoFunc
                    is CompositeType.Struct, is CompositeType.Array -> AbstractHeapType.None
                }
            }
            is ConcreteHeapType.RecursiveTypeIndex -> AbstractHeapType.None
            else -> heapType
        }
    }

    private fun acceptedTags(heapType: HeapType): Int {
        return when (heapType) {
            AbstractHeapType.Func -> tagBit(RV_TYPE_FUNCTION)
            AbstractHeapType.Extern -> tagBit(RV_TYPE_EXTERN)
            AbstractHeapType.Exception -> tagBit(RV_TYPE_EXCEPTION)
            AbstractHeapType.Any -> tagBit(RV_TYPE_I31) or tagBit(RV_TYPE_STRUCT) or tagBit(RV_TYPE_ARRAY) or tagBit(RV_TYPE_HOST)
            AbstractHeapType.Eq -> tagBit(RV_TYPE_I31) or tagBit(RV_TYPE_STRUCT) or tagBit(RV_TYPE_ARRAY)
            AbstractHeapType.Struct -> tagBit(RV_TYPE_STRUCT)
            AbstractHeapType.Array -> tagBit(RV_TYPE_ARRAY)
            AbstractHeapType.I31 -> tagBit(RV_TYPE_I31)
            is ConcreteHeapType -> tagBit(RV_TYPE_STRUCT) or tagBit(RV_TYPE_ARRAY) or tagBit(RV_TYPE_FUNCTION)
            else -> NO_TAGS
        }
    }

    private fun tagBit(tag: Long): Int {
        return 1 shl tag.toInt()
    }

    private fun rooted(raw: Long): Long {
        return heap.rootScoped(raw)
    }

    private fun requireOwned(owned: Boolean) {

        require(owned) { "the reference belongs to a different store" }

        store.liveStore()
    }

    private companion object {
        const val NO_TAGS = 0
    }
}
