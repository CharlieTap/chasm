package io.github.charlietap.chasm.runtime.heap

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.gc.AllocationAvailability
import io.github.charlietap.chasm.gc.GarbageCollectedHeap
import io.github.charlietap.chasm.gc.GcRootSink
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.runtime.type.RuntimeTypeRegistry
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType

class WasmHeap internal constructor(
    private val garbageCollectedHeap: GarbageCollectedHeap,
) {
    constructor() : this(GarbageCollectedHeap())

    private val runtimeTypes = RuntimeTypeRegistry()
    private var tagInstances = arrayOfNulls<TagInstance>(0)
    private var tagCount = 0
    private var structDescriptorKeys = IntArray(0)
    private var structTypes = arrayOfNulls<StructType>(0)
    private var arrayDescriptorKeys = IntArray(0)
    private var arrayTypes = arrayOfNulls<ArrayType>(0)
    private var exceptionDescriptorKeys = IntArray(0)

    private var nextAutomaticGcThresholdWords = 0L

    fun registerTag(
        rtt: RTT,
        type: TagType,
    ): Address.Tag {
        val parameterTypes = type.functionType.params.types
        val referenceFieldIndices = IntArray(parameterTypes.size)
        var referenceFieldCount = 0
        var parameterIndex = 0
        while (parameterIndex < parameterTypes.size) {
            if (parameterTypes[parameterIndex] is ValueType.Reference) {
                referenceFieldIndices[referenceFieldCount++] = parameterIndex
            }
            parameterIndex++
        }
        val address = Address.Tag(tagCount)
        ensureTagCapacity(tagCount + 1)
        val descriptorKey = garbageCollectedHeap.registerException(
            tagAddress = address.address,
            payloadWords = parameterTypes.size,
            referenceFieldIndices = if (referenceFieldCount == referenceFieldIndices.size) {
                referenceFieldIndices
            } else {
                referenceFieldIndices.copyOf(referenceFieldCount)
            },
        )
        tagInstances[tagCount] = TagInstance(rtt, type)
        exceptionDescriptorKeys[tagCount] = descriptorKey
        tagCount++
        return address
    }

    fun tag(address: Address.Tag): TagInstance {
        return tagInstances.getOrNull(address.address)
            ?: throw InvocationException(InvocationError.TagLookupFailed(address))
    }

    fun allocateException(
        tagAddress: Address.Tag,
        fields: LongArray,
    ): Long {
        return garbageCollectedHeap.allocateException(exceptionDescriptorKey(tagAddress), fields)
    }

    fun allocateException(
        context: ExecutionContext,
        tagAddress: Address.Tag,
        fields: LongArray,
    ): Long {
        prepareExceptionAllocation(context, tagAddress)
        return allocateException(tagAddress, fields)
    }

    fun allocateExceptionFromStack(
        tagAddress: Address.Tag,
        valueStack: ValueStack,
    ): Long {
        val fieldCount = tag(tagAddress).type.functionType.params.types.size
        return valueStack.consumeTopFieldsToException(
            garbageCollectedHeap,
            exceptionDescriptorKey(tagAddress),
            fieldCount,
        )
    }

    fun allocateExceptionFromStack(
        context: ExecutionContext,
        tagAddress: Address.Tag,
    ): Long {
        prepareExceptionAllocation(context, tagAddress)
        return allocateExceptionFromStack(tagAddress, context.vstack)
    }

    fun allocateExceptionFromFrame(
        tagAddress: Address.Tag,
        firstPayloadSlot: Int,
        valueStack: ValueStack,
    ): Long {
        return valueStack.allocateExceptionFromFrame(
            garbageCollectedHeap,
            exceptionDescriptorKey(tagAddress),
            firstPayloadSlot,
        )
    }

    fun allocateExceptionFromFrame(
        context: ExecutionContext,
        tagAddress: Address.Tag,
        firstPayloadSlot: Int,
    ): Long {
        prepareExceptionAllocation(context, tagAddress)
        return allocateExceptionFromFrame(tagAddress, firstPayloadSlot, context.vstack)
    }

    fun exceptionTagAddress(rawReference: Long): Address.Tag {
        val tagAddress = garbageCollectedHeap.exceptionTagAddressOrNegative(rawReference)
        if (tagAddress < 0) {
            requireExceptionTag(rawReference)
            throw InvocationException(
                InvocationError.ExceptionLookupFailed(
                    Address.Exception((rawReference ushr RV_SHIFT_BITS).toInt()),
                ),
            )
        }
        return Address.Tag(tagAddress)
    }

    fun exceptionTagAddressOrNegative(rawReference: Long): Int {
        val tagAddress = garbageCollectedHeap.exceptionTagAddressOrNegative(rawReference)
        return if (tagAddress < 0 || tagInstances.getOrNull(tagAddress) == null) -1 else tagAddress
    }

    fun getExceptionFieldTrusted(
        rawReference: Long,
        fieldIndex: Int,
    ): Long {
        return garbageCollectedHeap.getExceptionField(rawReference, fieldIndex)
    }

    fun registerRuntimeTypes(types: List<DefinedType>): RuntimeTypeMap {
        val runtimeTypeMap = runtimeTypes.register(types)
        var typeIndex = 0
        while (typeIndex < types.size) {
            val runtimeType = runtimeTypeMap[typeIndex]
            when (val compositeType = definedCompositeType(types[typeIndex])) {
                is CompositeType.Struct -> {
                    ensureStructTypeCapacity(runtimeType.value + 1)
                    registerStructType(runtimeType, compositeType.structType)
                }
                is CompositeType.Array -> {
                    ensureArrayTypeCapacity(runtimeType.value + 1)
                    registerArrayType(runtimeType, compositeType.arrayType)
                }
                else -> Unit
            }
            typeIndex++
        }
        return runtimeTypeMap
    }

    fun registerRuntimeType(type: DefinedType): RTT {
        val compositeType = definedCompositeType(type)
        val runtimeType = runtimeTypes.register(type)
        if (compositeType is CompositeType.Struct) {
            ensureStructTypeCapacity(runtimeType.value + 1)
            registerStructType(runtimeType, compositeType.structType)
        } else if (compositeType is CompositeType.Array) {
            ensureArrayTypeCapacity(runtimeType.value + 1)
            registerArrayType(runtimeType, compositeType.arrayType)
        }
        return runtimeType
    }

    fun matchesRuntimeType(
        actual: RTT,
        expected: RTT,
    ): Boolean = runtimeTypes.matches(actual, expected)

    fun allocateStruct(
        runtimeType: RTT,
        initialFields: LongArray,
    ): Long {
        return garbageCollectedHeap.allocateStruct(
            structDescriptorKey(runtimeType),
            initialFields,
        )
    }

    fun allocateStruct(
        context: ExecutionContext,
        runtimeType: RTT,
        initialFields: LongArray,
    ): Long {
        prepareStructAllocation(context, runtimeType)
        return allocateStruct(runtimeType, initialFields)
    }

    fun allocateStructFromStack(
        runtimeType: RTT,
        fieldCount: Int,
        valueStack: ValueStack,
    ) {
        valueStack.replaceTopFieldsWithStruct(
            garbageCollectedHeap,
            structDescriptorKey(runtimeType),
            fieldCount,
        )
    }

    fun allocateStructFromStack(
        context: ExecutionContext,
        runtimeType: RTT,
        fieldCount: Int,
    ) {
        prepareStructAllocation(context, runtimeType)
        allocateStructFromStack(runtimeType, fieldCount, context.vstack)
    }

    fun allocateStructFromFrame(
        runtimeType: RTT,
        firstFieldSlot: Int,
        destinationSlot: Int,
        valueStack: ValueStack,
    ) {
        valueStack.setFrameSlotToNewStruct(
            garbageCollectedHeap,
            structDescriptorKey(runtimeType),
            firstFieldSlot,
            destinationSlot,
        )
    }

    fun allocateStructFromFrame(
        context: ExecutionContext,
        runtimeType: RTT,
        firstFieldSlot: Int,
        destinationSlot: Int,
    ) {
        prepareStructAllocation(context, runtimeType)
        allocateStructFromFrame(runtimeType, firstFieldSlot, destinationSlot, context.vstack)
    }

    fun getStructField(
        rawReference: Long,
        fieldIndex: Int,
    ): Long {
        val type = resolveStructType(rawReference)
        if (fieldIndex !in type.fields.indices) {
            throw InvocationException(InvocationError.StructFieldLookupFailed(fieldIndex))
        }
        return garbageCollectedHeap.getStructField(rawReference, fieldIndex)
    }

    fun getStructFieldTrusted(
        rawReference: Long,
        fieldIndex: Int,
    ): Long {
        requireStructTag(rawReference)
        return garbageCollectedHeap.getStructField(rawReference, fieldIndex)
    }

    fun setStructField(
        rawReference: Long,
        fieldIndex: Int,
        value: Long,
    ) {
        val type = resolveStructType(rawReference)
        if (fieldIndex !in type.fields.indices) {
            throw InvocationException(InvocationError.StructFieldLookupFailed(fieldIndex))
        }
        garbageCollectedHeap.setStructField(rawReference, fieldIndex, value)
    }

    fun setStructFieldTrusted(
        rawReference: Long,
        fieldIndex: Int,
        value: Long,
    ) {
        requireStructTag(rawReference)
        garbageCollectedHeap.setStructField(rawReference, fieldIndex, value)
    }

    fun structFieldType(
        rawReference: Long,
        fieldIndex: Int,
    ): FieldType {
        val type = resolveStructType(rawReference)
        val field = type.fields.getOrNull(fieldIndex)
            ?: throw InvocationException(InvocationError.StructFieldLookupFailed(fieldIndex))
        return field
    }

    fun structRuntimeTypeIdOrNegative(rawReference: Long): Int {
        val runtimeTypeId = garbageCollectedHeap.structSemanticIdOrNegative(rawReference)
        return if (runtimeTypeId < 0 || structTypes.getOrNull(runtimeTypeId) == null) -1 else runtimeTypeId
    }

    fun arrayRuntimeTypeIdOrNegative(rawReference: Long): Int {
        val runtimeTypeId = garbageCollectedHeap.arraySemanticIdOrNegative(rawReference)
        return if (runtimeTypeId < 0 || arrayTypes.getOrNull(runtimeTypeId) == null) -1 else runtimeTypeId
    }

    fun allocateArrayFilled(
        runtimeType: RTT,
        length: Int,
        value: Long,
    ): Long {
        return garbageCollectedHeap.allocateArrayFilled(arrayDescriptorKey(runtimeType), length, value)
    }

    fun allocateArrayFilled(
        context: ExecutionContext,
        runtimeType: RTT,
        length: Int,
        value: Long,
    ): Long {
        prepareArrayAllocation(context, runtimeType, length)
        return allocateArrayFilled(runtimeType, length, value)
    }

    fun allocateArrayFromElements(
        runtimeType: RTT,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ): Long {
        return garbageCollectedHeap.allocateArrayFromElements(
            arrayDescriptorKey(runtimeType),
            source,
            sourceOffset,
            length,
        )
    }

    fun allocateArrayFromElements(
        context: ExecutionContext,
        runtimeType: RTT,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ): Long {
        prepareArrayAllocation(context, runtimeType, length)
        return allocateArrayFromElements(runtimeType, source, sourceOffset, length)
    }

    fun allocateArrayFromStack(
        runtimeType: RTT,
        length: Int,
        valueStack: ValueStack,
    ) {
        valueStack.replaceTopFieldsWithArray(
            garbageCollectedHeap,
            arrayDescriptorKey(runtimeType),
            length,
        )
    }

    fun allocateArrayFromStack(
        context: ExecutionContext,
        runtimeType: RTT,
        length: Int,
    ) {
        prepareArrayAllocation(context, runtimeType, length)
        allocateArrayFromStack(runtimeType, length, context.vstack)
    }

    fun allocateArrayFromFrame(
        runtimeType: RTT,
        firstElementSlot: Int,
        length: Int,
        destinationSlot: Int,
        valueStack: ValueStack,
    ) {
        valueStack.setFrameSlotToNewArray(
            garbageCollectedHeap,
            arrayDescriptorKey(runtimeType),
            firstElementSlot,
            length,
            destinationSlot,
        )
    }

    fun allocateArrayFromFrame(
        context: ExecutionContext,
        runtimeType: RTT,
        firstElementSlot: Int,
        length: Int,
        destinationSlot: Int,
    ) {
        prepareArrayAllocation(context, runtimeType, length)
        allocateArrayFromFrame(runtimeType, firstElementSlot, length, destinationSlot, context.vstack)
    }

    fun allocateArrayFromData(
        runtimeType: RTT,
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ): Long {
        return garbageCollectedHeap.allocateArrayFromData(
            arrayDescriptorKey(runtimeType),
            source,
            sourceByteOffset,
            length,
            elementByteWidth,
        )
    }

    fun allocateArrayFromData(
        context: ExecutionContext,
        runtimeType: RTT,
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ): Long {
        prepareArrayAllocation(context, runtimeType, length)
        return allocateArrayFromData(runtimeType, source, sourceByteOffset, length, elementByteWidth)
    }

    fun arrayLength(rawReference: Long): Int {
        resolveArrayType(rawReference)
        return garbageCollectedHeap.arrayLength(rawReference)
    }

    fun arrayLengthTrusted(rawReference: Long): Int {
        requireArrayTag(rawReference)
        return garbageCollectedHeap.arrayLength(rawReference)
    }

    fun getArrayElement(
        rawReference: Long,
        index: Int,
    ): Long {
        resolveArrayType(rawReference)
        return try {
            garbageCollectedHeap.getArrayElement(rawReference, index)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
        }
    }

    fun getArrayElementTrusted(
        rawReference: Long,
        index: Int,
    ): Long {
        requireArrayTag(rawReference)
        return try {
            garbageCollectedHeap.getArrayElement(rawReference, index)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
        }
    }

    fun setArrayElement(
        rawReference: Long,
        index: Int,
        value: Long,
    ) {
        resolveArrayType(rawReference)
        try {
            garbageCollectedHeap.setArrayElement(rawReference, index, value)
        } catch (_: IllegalArgumentException) {
            throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
        }
    }

    fun setArrayElementTrusted(
        rawReference: Long,
        index: Int,
        value: Long,
    ) {
        requireArrayTag(rawReference)
        garbageCollectedHeap.setArrayElement(rawReference, index, value)
    }

    fun arrayFieldType(rawReference: Long): FieldType = resolveArrayType(rawReference).fieldType

    fun fillArray(
        rawReference: Long,
        offset: Int,
        length: Int,
        value: Long,
    ) {
        requireArrayTag(rawReference)
        garbageCollectedHeap.fillArray(rawReference, offset, length, value)
    }

    fun copyArray(
        sourceReference: Long,
        sourceOffset: Int,
        destinationReference: Long,
        destinationOffset: Int,
        length: Int,
    ) {
        requireArrayTag(sourceReference)
        requireArrayTag(destinationReference)
        garbageCollectedHeap.copyArray(
            sourceReference,
            sourceOffset,
            destinationReference,
            destinationOffset,
            length,
        )
    }

    fun initializeArrayFromElements(
        rawReference: Long,
        destinationOffset: Int,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ) {
        requireArrayTag(rawReference)
        garbageCollectedHeap.initializeArrayFromElements(
            rawReference,
            destinationOffset,
            source,
            sourceOffset,
            length,
        )
    }

    fun initializeArrayFromData(
        rawReference: Long,
        destinationOffset: Int,
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ) {
        requireArrayTag(rawReference)
        garbageCollectedHeap.initializeArrayFromData(
            rawReference,
            destinationOffset,
            source,
            sourceByteOffset,
            length,
            elementByteWidth,
        )
    }

    fun allocatedGuestBytes(): Long {
        val allocatedWords = garbageCollectedHeap.allocatedPayloadWords()
        val managedBytes = if (allocatedWords > Long.MAX_VALUE / Long.SIZE_BYTES) {
            Long.MAX_VALUE
        } else {
            allocatedWords * Long.SIZE_BYTES
        }
        return managedBytes
    }

    fun drop() {
        garbageCollectedHeap.drop()
    }

    fun shouldCollectGarbage(thresholdBytes: Long): Boolean {
        val allocatedWords = garbageCollectedHeap.allocatedPayloadWords()
        val floorWords = minOf(
            bytesToWordsCeiling(thresholdBytes),
            garbageCollectedHeap.maximumPayloadWords(),
        )
        return allocatedWords != 0L && allocatedWords >= floorWords
    }

    private fun prepareStructAllocation(
        store: Store,
        runtimeType: RTT,
        thresholdBytes: Long,
        valueStack: ValueStack,
    ) {
        val descriptorKey = structDescriptorKey(runtimeType)
        prepareAllocation(
            store = store,
            pendingSlotWords = garbageCollectedHeap.fixedAllocationSlotWords(descriptorKey),
            availability = garbageCollectedHeap.fixedAllocationAvailability(descriptorKey),
            thresholdBytes = thresholdBytes,
            valueStack = valueStack,
        )
    }

    private fun prepareArrayAllocation(
        store: Store,
        runtimeType: RTT,
        length: Int,
        thresholdBytes: Long,
        valueStack: ValueStack,
    ) {
        val descriptorKey = arrayDescriptorKey(runtimeType)
        prepareAllocation(
            store = store,
            pendingSlotWords = garbageCollectedHeap.arrayAllocationSlotWords(descriptorKey, length),
            availability = garbageCollectedHeap.arrayAllocationAvailability(descriptorKey, length),
            thresholdBytes = thresholdBytes,
            valueStack = valueStack,
        )
    }

    private fun prepareExceptionAllocation(
        store: Store,
        tagAddress: Address.Tag,
        thresholdBytes: Long,
        valueStack: ValueStack,
    ) {
        val descriptorKey = exceptionDescriptorKey(tagAddress)
        prepareAllocation(
            store = store,
            pendingSlotWords = garbageCollectedHeap.fixedAllocationSlotWords(descriptorKey),
            availability = garbageCollectedHeap.fixedAllocationAvailability(descriptorKey),
            thresholdBytes = thresholdBytes,
            valueStack = valueStack,
        )
    }

    private fun prepareStructAllocation(
        context: ExecutionContext,
        runtimeType: RTT,
    ) {
        if (context.config.gcStrategy != GCStrategy.TRADITIONAL) return
        prepareStructAllocation(
            store = context.store,
            runtimeType = runtimeType,
            thresholdBytes = context.config.gcThreshold.bytes,
            valueStack = context.vstack,
        )
    }

    private fun prepareArrayAllocation(
        context: ExecutionContext,
        runtimeType: RTT,
        length: Int,
    ) {
        if (context.config.gcStrategy != GCStrategy.TRADITIONAL) return
        prepareArrayAllocation(
            store = context.store,
            runtimeType = runtimeType,
            length = length,
            thresholdBytes = context.config.gcThreshold.bytes,
            valueStack = context.vstack,
        )
    }

    private fun prepareExceptionAllocation(
        context: ExecutionContext,
        tagAddress: Address.Tag,
    ) {
        if (context.config.gcStrategy != GCStrategy.TRADITIONAL) return
        prepareExceptionAllocation(
            store = context.store,
            tagAddress = tagAddress,
            thresholdBytes = context.config.gcThreshold.bytes,
            valueStack = context.vstack,
        )
    }

    private fun visitGcRoots(store: Store, rootSink: GcRootSink) {
        var globalIndex = 0
        while (globalIndex < store.globals.size) {
            val global = store.globals[globalIndex]
            if (global.type.valueType is ValueType.Reference) {
                rootSink.markRoot(global.value)
            }
            globalIndex++
        }

        var tableIndex = 0
        while (tableIndex < store.tables.size) {
            val tableElements = store.tables[tableIndex].elements
            var elementIndex = 0
            while (elementIndex < tableElements.size) {
                rootSink.markRoot(tableElements[elementIndex])
                elementIndex++
            }
            tableIndex++
        }

        var segmentIndex = 0
        while (segmentIndex < store.elements.size) {
            val segmentElements = store.elements[segmentIndex].elements
            var elementIndex = 0
            while (elementIndex < segmentElements.size) {
                rootSink.markRoot(segmentElements[elementIndex])
                elementIndex++
            }
            segmentIndex++
        }
    }

    fun collectGarbage(
        store: Store,
        supplementalStack: ValueStack? = null,
    ) {
        collectGarbage(store, supplementalStack, 0L)
    }

    private fun collectGarbage(
        store: Store,
        supplementalStack: ValueStack?,
        pendingSlotWords: Long,
    ) {
        garbageCollectedHeap.beginCollection()
        try {
            supplementalStack?.visitGcRoots(garbageCollectedHeap)
            visitGcRoots(store, garbageCollectedHeap)
            garbageCollectedHeap.finishCollection()
            updateAutomaticGcThreshold(pendingSlotWords)
        } catch (failure: Throwable) {
            garbageCollectedHeap.abortCollection()
            throw failure
        }
    }

    private fun prepareAllocation(
        store: Store,
        pendingSlotWords: Int,
        availability: AllocationAvailability,
        thresholdBytes: Long,
        valueStack: ValueStack,
    ) {
        val maximumWords = garbageCollectedHeap.maximumPayloadWords()
        val floorWords = minOf(bytesToWordsCeiling(thresholdBytes), maximumWords)
        val thresholdWords = maxOf(floorWords, nextAutomaticGcThresholdWords)
        val projectedWords = saturatingAdd(
            garbageCollectedHeap.allocatedPayloadWords(),
            pendingSlotWords.toLong(),
        )
        val thresholdCrossed = projectedWords >= thresholdWords
        if (availability == AllocationAvailability.REUSABLE) return

        if (availability != AllocationAvailability.EXHAUSTED && !thresholdCrossed) return
        try {
            collectGarbage(
                store = store,
                supplementalStack = valueStack,
                pendingSlotWords = pendingSlotWords.toLong(),
            )
        } catch (failure: OutOfMemoryError) {
            throw InvocationException(
                InvocationError.GarbageCollectionFailed(
                    failure.message ?: "automatic collection exhausted host memory",
                ),
            )
        }
    }

    private fun updateAutomaticGcThreshold(pendingSlotWords: Long) {
        val maximumWords = garbageCollectedHeap.maximumPayloadWords()
        val liveTarget = saturatingDouble(garbageCollectedHeap.allocatedPayloadWords())
        val pendingTarget = saturatingDouble(pendingSlotWords)
        nextAutomaticGcThresholdWords = minOf(
            maximumWords,
            maxOf(liveTarget, pendingTarget),
        )
    }

    private fun exceptionDescriptorKey(tagAddress: Address.Tag): Int = exceptionDescriptorKeys[tagAddress.address]

    private fun definedCompositeType(type: DefinedType): CompositeType =
        type.recursiveType.subTypes[type.recursiveTypeIndex].compositeType

    private fun requireExceptionTag(rawReference: Long) {
        if (rawReference and RV_TYPE_MASK != RV_TYPE_EXCEPTION) {
            throw InvocationException(InvocationError.ExceptionReferenceExpected)
        }
    }

    private fun ensureTagCapacity(requiredCapacity: Int) {
        if (requiredCapacity <= tagInstances.size) return
        var capacity = maxOf(MINIMUM_ROOT_REGISTRY_CAPACITY, tagInstances.size)
        while (capacity < requiredCapacity) {
            capacity = capacity shl 1
        }
        val grownTagInstances = tagInstances.copyOf(capacity)
        val grownDescriptorKeys = exceptionDescriptorKeys.copyOf(capacity)
        tagInstances = grownTagInstances
        exceptionDescriptorKeys = grownDescriptorKeys
    }

    private fun registerStructType(
        runtimeType: RTT,
        structType: StructType,
    ) {
        val referenceFieldIndices = IntArray(structType.fields.size)
        var referenceFieldCount = 0
        var fieldIndex = 0
        while (fieldIndex < structType.fields.size) {
            val storageType = structType.fields[fieldIndex].storageType
            if (storageType is StorageType.Value && storageType.type is ValueType.Reference) {
                referenceFieldIndices[referenceFieldCount++] = fieldIndex
            }
            fieldIndex++
        }
        val descriptorKey = garbageCollectedHeap.registerStruct(
            semanticId = runtimeType.value,
            payloadWords = structType.fields.size,
            referenceFieldIndices = if (referenceFieldCount == referenceFieldIndices.size) {
                referenceFieldIndices
            } else {
                referenceFieldIndices.copyOf(referenceFieldCount)
            },
        )
        val existingType = structTypes[runtimeType.value]
        if (existingType == null) {
            structTypes[runtimeType.value] = structType
            structDescriptorKeys[runtimeType.value] = descriptorKey
        } else {
            check(structDescriptorKeys[runtimeType.value] == descriptorKey)
        }
    }

    private fun ensureStructTypeCapacity(requiredCapacity: Int) {
        if (requiredCapacity <= structDescriptorKeys.size) return
        var capacity = maxOf(MINIMUM_ROOT_REGISTRY_CAPACITY, structDescriptorKeys.size)
        while (capacity < requiredCapacity) {
            capacity = capacity shl 1
        }
        val grownDescriptorKeys = structDescriptorKeys.copyOf(capacity)
        val grownStructTypes = structTypes.copyOf(capacity)
        structDescriptorKeys = grownDescriptorKeys
        structTypes = grownStructTypes
    }

    private fun ensureArrayTypeCapacity(requiredCapacity: Int) {
        if (requiredCapacity <= arrayDescriptorKeys.size) return
        var capacity = maxOf(MINIMUM_ROOT_REGISTRY_CAPACITY, arrayDescriptorKeys.size)
        while (capacity < requiredCapacity) {
            capacity = capacity shl 1
        }
        val grownDescriptorKeys = arrayDescriptorKeys.copyOf(capacity)
        val grownArrayTypes = arrayTypes.copyOf(capacity)
        arrayDescriptorKeys = grownDescriptorKeys
        arrayTypes = grownArrayTypes
    }

    private fun registerArrayType(
        runtimeType: RTT,
        arrayType: ArrayType,
    ) {
        val valueType = (arrayType.fieldType.storageType as? StorageType.Value)?.type
        val elementsMayContainReferences = valueType is ValueType.Reference
        val descriptorKey = garbageCollectedHeap.registerArray(
            semanticId = runtimeType.value,
            elementsMayContainReferences = elementsMayContainReferences,
        )
        val existing = arrayTypes[runtimeType.value]
        if (existing == null) {
            arrayTypes[runtimeType.value] = arrayType
            arrayDescriptorKeys[runtimeType.value] = descriptorKey
        } else {
            check(arrayDescriptorKeys[runtimeType.value] == descriptorKey)
        }
    }

    private fun arrayDescriptorKey(runtimeType: RTT): Int = arrayDescriptorKeys[runtimeType.value]

    private fun structDescriptorKey(runtimeType: RTT): Int = structDescriptorKeys[runtimeType.value]

    private fun requireStructTag(rawReference: Long) {
        if (rawReference and RV_TYPE_MASK != RV_TYPE_STRUCT) {
            throw InvocationException(InvocationError.StructReferenceExpected)
        }
    }

    private fun resolveStructType(rawReference: Long): StructType {
        requireStructTag(rawReference)
        val runtimeTypeId = try {
            garbageCollectedHeap.structSemanticId(rawReference)
        } catch (_: IllegalArgumentException) {
            val address = (rawReference shr RV_SHIFT_BITS).toInt()
            throw InvocationException(InvocationError.StructLookupFailed(Address.Struct(address)))
        }
        return checkNotNull(structTypes.getOrNull(runtimeTypeId)) {
            "live struct has no registered semantic metadata"
        }
    }

    private fun requireArrayTag(rawReference: Long) {
        if (rawReference and RV_TYPE_MASK != RV_TYPE_ARRAY) {
            throw InvocationException(InvocationError.ArrayReferenceExpected)
        }
    }

    private fun resolveArrayType(rawReference: Long): ArrayType {
        requireArrayTag(rawReference)
        val runtimeTypeId = try {
            garbageCollectedHeap.arraySemanticId(rawReference)
        } catch (_: IllegalArgumentException) {
            val address = (rawReference shr RV_SHIFT_BITS).toInt()
            throw InvocationException(InvocationError.ArrayLookupFailed(Address.Array(address)))
        }
        return checkNotNull(arrayTypes.getOrNull(runtimeTypeId)) {
            "live array has no registered semantic metadata"
        }
    }

    private fun bytesToWordsCeiling(bytes: Long): Long =
        bytes / Long.SIZE_BYTES + if (bytes % Long.SIZE_BYTES == 0L) 0L else 1L

    private fun saturatingAdd(
        left: Long,
        right: Long,
    ): Long = if (left > Long.MAX_VALUE - right) Long.MAX_VALUE else left + right

    private fun saturatingDouble(value: Long): Long =
        if (value > Long.MAX_VALUE / 2L) Long.MAX_VALUE else value * 2L

    private companion object {
        const val MINIMUM_ROOT_REGISTRY_CAPACITY = 4
    }
}
