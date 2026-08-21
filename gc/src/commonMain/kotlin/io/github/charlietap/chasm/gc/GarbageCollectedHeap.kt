package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT

private const val PAGE_SHIFT = 11
private const val PAGE_WORDS = 1 shl PAGE_SHIFT
private const val PAGE_MASK = PAGE_WORDS - 1

const val MAXIMUM_FIXED_PAYLOAD_WORDS = PAGE_WORDS
private const val DEFAULT_INITIAL_PAGE_DIRECTORY_CAPACITY = 16
private const val DEFAULT_INITIAL_DESCRIPTOR_DIRECTORY_CAPACITY = 16
private const val DEDICATED_ADDRESS_BIT = 1 shl 30
private const val DEDICATED_ID_MASK = DEDICATED_ADDRESS_BIT - 1
private const val MAXIMUM_PAGE_COUNT = DEDICATED_ID_MASK ushr PAGE_SHIFT
private const val DESCRIPTOR_KIND_BITS = 2
private const val DESCRIPTOR_KIND_MASK = (1 shl DESCRIPTOR_KIND_BITS) - 1
private const val MAXIMUM_SEMANTIC_ID = (1 shl (Int.SIZE_BITS - DESCRIPTOR_KIND_BITS)) - 1
private const val STRUCT_DESCRIPTOR_KIND = 1
private const val ARRAY_DESCRIPTOR_KIND = 2
private const val EXCEPTION_DESCRIPTOR_KIND = 3
private const val NO_PAGE_ID = 0
private const val NO_SLOT = -1
private const val ARRAY_DEDICATED_CUTOFF = 1024
private const val ARRAY_EXACT_CLASS_MAXIMUM = 96
private const val ARRAY_CLASS_COUNT = 104
private const val ARRAY_CLASS_INDEX_BITS = 7
private const val ARRAY_CLASS_INDEX_MASK = (1 shl ARRAY_CLASS_INDEX_BITS) - 1
private const val DEDICATED_DESCRIPTOR_MASK = 0xFFFF_FFFFL
private const val DEDICATED_MARK_BIT = 1L shl Int.SIZE_BITS
private const val RETAINED_MARK_WORKLIST_CAPACITY = 16 * 1024

private val EMPTY_REFERENCE_FIELD_INDICES = IntArray(0)
private val EMPTY_MARK_WORKLIST = IntArray(0)
private val ARRAY_LAYOUT_BY_LENGTH = IntArray(ARRAY_DEDICATED_CUTOFF) { length ->
    val classIndex = when {
        length <= ARRAY_EXACT_CLASS_MAXIMUM -> length
        length <= 128 -> 97
        length <= 192 -> 98
        length <= 256 -> 99
        length <= 384 -> 100
        length <= 512 -> 101
        length <= 768 -> 102
        else -> 103
    }
    val capacity = when (classIndex) {
        in 0..ARRAY_EXACT_CLASS_MAXIMUM -> classIndex
        97 -> 128
        98 -> 192
        99 -> 256
        100 -> 384
        101 -> 512
        102 -> 768
        else -> 1023
    }
    ((capacity + 1) shl ARRAY_CLASS_INDEX_BITS) or classIndex
}

enum class AllocationAvailability {
    /** Existing storage can satisfy the request. */
    REUSABLE,

    /** The request needs more storage within the configured limit. */
    GROWABLE,

    /** The configured heap limit cannot satisfy the request. */
    EXHAUSTED,
}

class GuestHeapOutOfMemoryError(
    message: String,
) : Error(message)

interface GcRootSink {
    fun markRoot(rawValue: Long)
}

/**
 * Stores structs, arrays, and exceptions for one Chasm store.
 *
 * The heap must not be accessed concurrently.
 *
 * Semantic IDs are runtime type IDs for structs and arrays, and tag addresses
 * for exceptions. Descriptor keys are opaque and only valid for this heap.
 */
class GarbageCollectedHeap(
    configuration: Configuration = Configuration(),
) : GcRootSink {

    /**
     * [maximumPageCount] sets the payload limit for ordinary pages and dedicated
     * arrays. [initialPageDirectoryCapacity] includes page zero. Both initial
     * directory capacities grow as needed.
     */
    class Configuration(
        initialPageDirectoryCapacity: Int = DEFAULT_INITIAL_PAGE_DIRECTORY_CAPACITY,
        initialDescriptorDirectoryCapacity: Int = DEFAULT_INITIAL_DESCRIPTOR_DIRECTORY_CAPACITY,
        maximumPageCount: Int = MAXIMUM_PAGE_COUNT,
    ) {
        internal val initialPageDirectoryCapacity = initialPageDirectoryCapacity
        internal val initialDescriptorDirectoryCapacity = initialDescriptorDirectoryCapacity
        internal val maximumPageCount = maximumPageCount

        init {
            require(maximumPageCount in 1..MAXIMUM_PAGE_COUNT) {
                "maximumPageCount must be between 1 and $MAXIMUM_PAGE_COUNT"
            }
            require(initialPageDirectoryCapacity in 2..maximumPageCount + 1) {
                "initialPageDirectoryCapacity must include page zero and fit maximumPageCount"
            }
            require(initialDescriptorDirectoryCapacity > 0) {
                "initialDescriptorDirectoryCapacity must be positive"
            }
            require(initialDescriptorDirectoryCapacity <= MAXIMUM_SEMANTIC_ID + 1) {
                "initialDescriptorDirectoryCapacity exceeds the semantic ID space"
            }
        }
    }

    class Statistics internal constructor(
        /** Pages currently assigned to a descriptor. */
        val activePageCount: Int,
        /** Words retained by the ordinary arena, including spare capacity. */
        val committedWords: Long,
        /** Allocated ordinary slot words and dedicated backing words. */
        val allocatedSlotWords: Long,
        /** Words retained by dedicated arrays, including their headers. */
        val dedicatedPayloadWords: Long,
        /** Total words retained by the heap. */
        val retainedPayloadWords: Long,
        /** Unused capacity inside allocated ordinary array slots. */
        val arrayClassSlackWords: Long,
        /** Words in free slots that can be reused by the same layout. */
        val freeSlotWords: Long,
        /** Words in unused bump slots that can be used by the same layout. */
        val virginSlotWords: Long,
        /** Page tail words too small for another slot. */
        val pageTailWords: Long,
        /** Dedicated arrays currently allocated. */
        val dedicatedArrayCount: Int,
        /** Current capacity of the mark worklist. */
        val markWorklistCapacity: Int,
        /** Largest worklist size in the current or most recent collection. */
        val markWorklistPeakSize: Int,
        /** Words retained by allocation and mark bitmaps. */
        val bitmapWords: Long,
        /** Registered struct, array, and exception descriptors. */
        val descriptorCount: Int,
        /** Page directory capacity, including page zero. */
        val pageDirectoryCapacity: Int,
        /** Ordinary pages available for reuse by another descriptor. */
        val recycledPageCount: Int,
    )

    private val maximumPageCount = configuration.maximumPageCount
    private var pages = arrayOfNulls<Page>(configuration.initialPageDirectoryCapacity)
    private var pageIdTop = 1
    private var activePageIds = IntArray(configuration.initialPageDirectoryCapacity - 1)
    private var activePageCount = 0
    private var recycledPageIds = IntArray(configuration.initialPageDirectoryCapacity - 1)
    private var recycledPageCount = 0
    private var aggregateDescriptors =
        arrayOfNulls<AggregateDescriptor>(configuration.initialDescriptorDirectoryCapacity)
    private var exceptionDescriptors =
        arrayOfNulls<FixedDescriptor>(configuration.initialDescriptorDirectoryCapacity)

    // Logical page N owns arena words [(N - 1) * PAGE_WORDS, N * PAGE_WORDS).
    private var payloadWords = LongArray(0)
    private var allocatedSlotWords = 0L
    private var dedicatedPayloads = arrayOfNulls<LongArray>(1)
    private var dedicatedIds = IntArray(0)
    private var dedicatedActiveCount = 0
    private var dedicatedIdTop = 1
    private var dedicatedPayloadWords = 0L
    private var markWorklist = EMPTY_MARK_WORKLIST
    private var markWorklistSize = 0
    private var markWorklistPeakSize = 0

    fun registerStruct(
        semanticId: Int,
        payloadWords: Int,
        referenceFieldIndices: IntArray,
    ): Int {
        return registerAggregateFixed(
            semanticId = semanticId,
            payloadWords = payloadWords,
            referenceFieldIndices = referenceFieldIndices,
            descriptorKind = STRUCT_DESCRIPTOR_KIND,
        )
    }

    fun registerArray(
        semanticId: Int,
        elementsMayContainReferences: Boolean,
    ): Int {
        validateSemanticId(semanticId)
        val descriptorKey = descriptorKey(semanticId, ARRAY_DESCRIPTOR_KIND)
        val existing = aggregateDescriptors.getOrNull(semanticId)
        if (existing != null) {
            require(
                existing is ArrayDescriptor &&
                    existing.elementsMayContainReferences == elementsMayContainReferences,
            ) { "semanticId $semanticId is already registered with a different layout" }
            return descriptorKey
        }

        ensureAggregateDescriptorCapacity(semanticId + 1)
        aggregateDescriptors[semanticId] = ArrayDescriptor(elementsMayContainReferences)
        return descriptorKey
    }

    fun registerException(
        tagAddress: Int,
        payloadWords: Int,
        referenceFieldIndices: IntArray,
    ): Int {
        validateSemanticId(tagAddress)
        val normalizedReferenceFieldIndices =
            normalizedReferenceFieldIndices(payloadWords, referenceFieldIndices)
        val descriptorKey = descriptorKey(tagAddress, EXCEPTION_DESCRIPTOR_KIND)
        val existing = exceptionDescriptors.getOrNull(tagAddress)
        if (existing != null) {
            require(existing.hasLayout(payloadWords, normalizedReferenceFieldIndices)) {
                "tagAddress $tagAddress is already registered with a different layout"
            }
            return descriptorKey
        }

        ensureExceptionDescriptorCapacity(tagAddress + 1)
        exceptionDescriptors[tagAddress] = FixedDescriptor(
            payloadWords = payloadWords,
            referenceFieldIndices = normalizedReferenceFieldIndices,
        )
        return descriptorKey
    }

    fun allocateStruct(
        descriptorKey: Int,
        initialFields: LongArray,
    ): Long {
        require(descriptorKind(descriptorKey) == STRUCT_DESCRIPTOR_KIND) {
            "descriptorKey does not identify a struct descriptor"
        }
        val descriptor = fixedDescriptor(descriptorKey)
        require(descriptor != null) {
            "descriptorKey does not identify a registered struct descriptor"
        }
        require(initialFields.size == descriptor.payloadWords) {
            "initialFields must contain exactly ${descriptor.payloadWords} words"
        }
        return allocateFixed(
            descriptorKey = descriptorKey,
            descriptor = descriptor,
            source = initialFields,
            sourceOffset = 0,
            referenceTag = RV_TYPE_STRUCT,
        )
    }

    fun allocateStruct(
        descriptorKey: Int,
        source: LongArray,
        sourceOffset: Int,
    ): Long {
        require(descriptorKind(descriptorKey) == STRUCT_DESCRIPTOR_KIND) {
            "descriptorKey does not identify a struct descriptor"
        }
        val descriptor = fixedDescriptor(descriptorKey)
        require(descriptor != null) {
            "descriptorKey does not identify a registered struct descriptor"
        }
        require(
            sourceOffset >= 0 &&
                sourceOffset <= source.size &&
                descriptor.payloadWords <= source.size - sourceOffset,
        ) {
            "source must contain ${descriptor.payloadWords} words from sourceOffset"
        }
        return allocateFixed(
            descriptorKey = descriptorKey,
            descriptor = descriptor,
            source = source,
            sourceOffset = sourceOffset,
            referenceTag = RV_TYPE_STRUCT,
        )
    }

    fun getStructField(
        rawReference: Long,
        fieldIndex: Int,
    ): Long {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        return payloadWords[address - PAGE_WORDS + fieldIndex]
    }

    fun setStructField(
        rawReference: Long,
        fieldIndex: Int,
        value: Long,
    ) {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        payloadWords[address - PAGE_WORDS + fieldIndex] = value
    }

    fun structSemanticId(rawReference: Long): Int {
        require(
            rawReference and RV_TYPE_MASK == RV_TYPE_STRUCT &&
                isAllocatedReference(rawReference),
        ) { "rawReference does not identify a live struct in this heap" }
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val page = checkNotNull(pages[address ushr PAGE_SHIFT])
        return descriptorSemanticId(page.descriptorKey)
    }

    fun structSemanticIdOrNegative(rawReference: Long): Int {
        if (
            rawReference and RV_TYPE_MASK != RV_TYPE_STRUCT ||
            !isAllocatedReference(rawReference)
        ) {
            return -1
        }
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        return descriptorSemanticId(checkNotNull(pages[address ushr PAGE_SHIFT]).descriptorKey)
    }

    fun allocatedPayloadWords(): Long = allocatedSlotWords

    fun maximumPayloadWords(): Long = maximumPageCount.toLong() * PAGE_WORDS

    fun fixedAllocationSlotWords(descriptorKey: Int): Int {
        val descriptor = requireNotNull(fixedDescriptor(descriptorKey)) {
            "descriptorKey does not identify a registered fixed descriptor"
        }
        return maxOf(1, descriptor.payloadWords)
    }

    fun fixedAllocationAvailability(descriptorKey: Int): AllocationAvailability {
        val descriptor = requireNotNull(fixedDescriptor(descriptorKey)) {
            "descriptorKey does not identify a registered fixed descriptor"
        }
        return when {
            descriptor.availablePageHead != NO_PAGE_ID || recycledPageCount != 0 ->
                AllocationAvailability.REUSABLE
            canCommitOrdinaryPage() -> AllocationAvailability.GROWABLE
            else -> AllocationAvailability.EXHAUSTED
        }
    }

    fun arrayAllocationSlotWords(
        descriptorKey: Int,
        length: Int,
    ): Int {
        requireArrayDescriptor(descriptorKey)
        validateArrayLength(length)
        return if (length >= ARRAY_DEDICATED_CUTOFF) {
            length + 1
        } else {
            ARRAY_LAYOUT_BY_LENGTH[length] ushr ARRAY_CLASS_INDEX_BITS
        }
    }

    fun arrayAllocationAvailability(
        descriptorKey: Int,
        length: Int,
    ): AllocationAvailability {
        val descriptor = requireArrayDescriptor(descriptorKey)
        validateArrayLength(length)
        if (length >= ARRAY_DEDICATED_CUTOFF) {
            val requestedWords = length + 1
            val retainedWordLimit = maximumPayloadWords()
            val hasPayloadCapacity =
                payloadWords.size.toLong() + dedicatedPayloadWords + requestedWords <= retainedWordLimit
            val hasIdCapacity =
                dedicatedActiveCount < dedicatedIdTop - 1 || dedicatedIdTop <= DEDICATED_ID_MASK
            return if (hasPayloadCapacity && hasIdCapacity) {
                AllocationAvailability.GROWABLE
            } else {
                AllocationAvailability.EXHAUSTED
            }
        }

        val layout = ARRAY_LAYOUT_BY_LENGTH[length]
        val classIndex = layout and ARRAY_CLASS_INDEX_MASK
        val availablePageHeads = descriptor.availablePageHeads
        return when {
            availablePageHeads != null && availablePageHeads[classIndex] != NO_PAGE_ID ->
                AllocationAvailability.REUSABLE
            recycledPageCount != 0 -> AllocationAvailability.REUSABLE
            canCommitOrdinaryPage() -> AllocationAvailability.GROWABLE
            else -> AllocationAvailability.EXHAUSTED
        }
    }

    fun drop() {
        pages = arrayOfNulls(1)
        pageIdTop = 1
        activePageIds = IntArray(0)
        activePageCount = 0
        recycledPageIds = IntArray(0)
        recycledPageCount = 0
        aggregateDescriptors = arrayOfNulls(0)
        exceptionDescriptors = arrayOfNulls(0)
        payloadWords = LongArray(0)
        allocatedSlotWords = 0L
        dedicatedPayloads = arrayOfNulls(1)
        dedicatedIds = IntArray(0)
        dedicatedActiveCount = 0
        dedicatedIdTop = 1
        dedicatedPayloadWords = 0L
        markWorklist = EMPTY_MARK_WORKLIST
        markWorklistSize = 0
        markWorklistPeakSize = 0
    }

    fun allocateArrayFilled(
        descriptorKey: Int,
        length: Int,
        value: Long,
    ): Long {
        val descriptor = requireArrayDescriptor(descriptorKey)
        validateArrayLength(length)
        return if (length >= ARRAY_DEDICATED_CUTOFF) {
            allocateDedicatedArrayFilled(descriptorKey, length, value)
        } else {
            allocateOrdinaryArrayFilled(descriptorKey, descriptor, length, value)
        }
    }

    fun allocateArrayFromElements(
        descriptorKey: Int,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ): Long {
        val descriptor = requireArrayDescriptor(descriptorKey)
        validateArrayLength(length)
        requireRange(source.size, sourceOffset, length, "source")
        return if (length >= ARRAY_DEDICATED_CUTOFF) {
            allocateDedicatedArrayFromElements(descriptorKey, source, sourceOffset, length)
        } else {
            allocateOrdinaryArrayFromElements(
                descriptorKey,
                descriptor,
                source,
                sourceOffset,
                length,
            )
        }
    }

    fun allocateArrayFromData(
        descriptorKey: Int,
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ): Long {
        val descriptor = requireArrayDescriptor(descriptorKey)
        validateArrayLength(length)
        requireDataRange(source.size, sourceByteOffset, length, elementByteWidth)
        return if (length >= ARRAY_DEDICATED_CUTOFF) {
            allocateDedicatedArrayFromData(
                descriptorKey,
                source,
                sourceByteOffset,
                length,
                elementByteWidth,
            )
        } else {
            allocateOrdinaryArrayFromData(
                descriptorKey,
                descriptor,
                source,
                sourceByteOffset,
                length,
                elementByteWidth,
            )
        }
    }

    fun arrayLength(rawReference: Long): Int {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        return if (isDedicatedAddress(address)) {
            checkNotNull(dedicatedPayloads[dedicatedId(address)]).size - 1
        } else {
            payloadWords[address - PAGE_WORDS].toInt()
        }
    }

    fun getArrayElement(
        rawReference: Long,
        index: Int,
    ): Long {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        if (isDedicatedAddress(address)) {
            val words = checkNotNull(dedicatedPayloads[dedicatedId(address)])
            requireElementIndex(words.size - 1, index)
            return words[index + 1]
        }
        val headerOffset = address - PAGE_WORDS
        val length = payloadWords[headerOffset].toInt()
        requireElementIndex(length, index)
        return payloadWords[headerOffset + index + 1]
    }

    fun setArrayElement(
        rawReference: Long,
        index: Int,
        value: Long,
    ) {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        if (isDedicatedAddress(address)) {
            val words = checkNotNull(dedicatedPayloads[dedicatedId(address)])
            requireElementIndex(words.size - 1, index)
            words[index + 1] = value
            return
        }
        val headerOffset = address - PAGE_WORDS
        val length = payloadWords[headerOffset].toInt()
        requireElementIndex(length, index)
        payloadWords[headerOffset + index + 1] = value
    }

    fun fillArray(
        rawReference: Long,
        offset: Int,
        length: Int,
        value: Long,
    ) {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        if (isDedicatedAddress(address)) {
            val words = checkNotNull(dedicatedPayloads[dedicatedId(address)])
            requireRange(words.size - 1, offset, length, "destination")
            words.fill(value, offset + 1, offset + length + 1)
            return
        }
        val headerOffset = address - PAGE_WORDS
        val arrayLength = payloadWords[headerOffset].toInt()
        requireRange(arrayLength, offset, length, "destination")
        payloadWords.fill(value, headerOffset + offset + 1, headerOffset + offset + length + 1)
    }

    fun copyArray(
        sourceReference: Long,
        sourceOffset: Int,
        destinationReference: Long,
        destinationOffset: Int,
        length: Int,
    ) {
        val sourceAddress = (sourceReference ushr RV_SHIFT_BITS).toInt()
        val sourceWords: LongArray
        val sourceBase: Int
        val sourceLength: Int
        if (isDedicatedAddress(sourceAddress)) {
            sourceWords = checkNotNull(dedicatedPayloads[dedicatedId(sourceAddress)])
            sourceBase = 1
            sourceLength = sourceWords.size - 1
        } else {
            sourceWords = payloadWords
            sourceBase = sourceAddress - PAGE_WORDS + 1
            sourceLength = payloadWords[sourceBase - 1].toInt()
        }

        val destinationAddress = (destinationReference ushr RV_SHIFT_BITS).toInt()
        val destinationWords: LongArray
        val destinationBase: Int
        val destinationLength: Int
        if (isDedicatedAddress(destinationAddress)) {
            destinationWords = checkNotNull(dedicatedPayloads[dedicatedId(destinationAddress)])
            destinationBase = 1
            destinationLength = destinationWords.size - 1
        } else {
            destinationWords = payloadWords
            destinationBase = destinationAddress - PAGE_WORDS + 1
            destinationLength = payloadWords[destinationBase - 1].toInt()
        }

        requireRange(sourceLength, sourceOffset, length, "source")
        requireRange(destinationLength, destinationOffset, length, "destination")
        sourceWords.copyInto(
            destination = destinationWords,
            destinationOffset = destinationBase + destinationOffset,
            startIndex = sourceBase + sourceOffset,
            endIndex = sourceBase + sourceOffset + length,
        )
    }

    fun initializeArrayFromElements(
        rawReference: Long,
        destinationOffset: Int,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ) {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val destinationWords: LongArray
        val destinationBase: Int
        val destinationLength: Int
        if (isDedicatedAddress(address)) {
            destinationWords = checkNotNull(dedicatedPayloads[dedicatedId(address)])
            destinationBase = 1
            destinationLength = destinationWords.size - 1
        } else {
            destinationWords = payloadWords
            destinationBase = address - PAGE_WORDS + 1
            destinationLength = payloadWords[destinationBase - 1].toInt()
        }
        requireRange(destinationLength, destinationOffset, length, "destination")
        requireRange(source.size, sourceOffset, length, "source")
        source.copyInto(
            destination = destinationWords,
            destinationOffset = destinationBase + destinationOffset,
            startIndex = sourceOffset,
            endIndex = sourceOffset + length,
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
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val destinationWords: LongArray
        val destinationBase: Int
        val destinationLength: Int
        if (isDedicatedAddress(address)) {
            destinationWords = checkNotNull(dedicatedPayloads[dedicatedId(address)])
            destinationBase = 1
            destinationLength = destinationWords.size - 1
        } else {
            destinationWords = payloadWords
            destinationBase = address - PAGE_WORDS + 1
            destinationLength = payloadWords[destinationBase - 1].toInt()
        }
        requireRange(destinationLength, destinationOffset, length, "destination")
        requireDataRange(source.size, sourceByteOffset, length, elementByteWidth)
        decodeDataInto(
            source,
            sourceByteOffset,
            length,
            elementByteWidth,
            destinationWords,
            destinationBase + destinationOffset,
        )
    }

    fun arraySemanticId(rawReference: Long): Int {
        require(
            rawReference and RV_TYPE_MASK == RV_TYPE_ARRAY &&
                isAllocatedReference(rawReference),
        ) { "rawReference does not identify a live array in this heap" }
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val descriptorKey = if (isDedicatedAddress(address)) {
            checkNotNull(dedicatedPayloads[dedicatedId(address)])[0].toInt()
        } else {
            checkNotNull(pages[address ushr PAGE_SHIFT]).descriptorKey
        }
        return descriptorSemanticId(descriptorKey)
    }

    fun arraySemanticIdOrNegative(rawReference: Long): Int {
        if (
            rawReference and RV_TYPE_MASK != RV_TYPE_ARRAY ||
            !isAllocatedReference(rawReference)
        ) {
            return -1
        }
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val descriptorKey = if (isDedicatedAddress(address)) {
            checkNotNull(dedicatedPayloads[dedicatedId(address)])[0].toInt()
        } else {
            checkNotNull(pages[address ushr PAGE_SHIFT]).descriptorKey
        }
        return if (descriptorKind(descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
            descriptorSemanticId(descriptorKey)
        } else {
            -1
        }
    }

    fun allocateException(
        descriptorKey: Int,
        initialFields: LongArray,
    ): Long {
        require(descriptorKind(descriptorKey) == EXCEPTION_DESCRIPTOR_KIND) {
            "descriptorKey does not identify an exception descriptor"
        }
        val descriptor = fixedDescriptor(descriptorKey)
        require(descriptor != null) {
            "descriptorKey does not identify a registered exception descriptor"
        }
        require(initialFields.size == descriptor.payloadWords) {
            "initialFields must contain exactly ${descriptor.payloadWords} words"
        }
        return allocateFixed(
            descriptorKey = descriptorKey,
            descriptor = descriptor,
            source = initialFields,
            sourceOffset = 0,
            referenceTag = RV_TYPE_EXCEPTION,
        )
    }

    fun allocateException(
        descriptorKey: Int,
        source: LongArray,
        sourceOffset: Int,
    ): Long {
        require(descriptorKind(descriptorKey) == EXCEPTION_DESCRIPTOR_KIND) {
            "descriptorKey does not identify an exception descriptor"
        }
        val descriptor = fixedDescriptor(descriptorKey)
        require(descriptor != null) {
            "descriptorKey does not identify a registered exception descriptor"
        }
        require(
            sourceOffset >= 0 &&
                sourceOffset <= source.size &&
                descriptor.payloadWords <= source.size - sourceOffset,
        ) {
            "source must contain ${descriptor.payloadWords} words from sourceOffset"
        }
        return allocateFixed(
            descriptorKey = descriptorKey,
            descriptor = descriptor,
            source = source,
            sourceOffset = sourceOffset,
            referenceTag = RV_TYPE_EXCEPTION,
        )
    }

    fun getExceptionField(
        rawReference: Long,
        fieldIndex: Int,
    ): Long {
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        return payloadWords[address - PAGE_WORDS + fieldIndex]
    }

    fun exceptionTagAddress(rawReference: Long): Int {
        require(
            rawReference and RV_TYPE_MASK == RV_TYPE_EXCEPTION &&
                isAllocatedReference(rawReference),
        ) { "rawReference does not identify a live exception in this heap" }
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val page = checkNotNull(pages[address ushr PAGE_SHIFT])
        return descriptorSemanticId(page.descriptorKey)
    }

    fun exceptionTagAddressOrNegative(rawReference: Long): Int {
        if (
            rawReference and RV_TYPE_MASK != RV_TYPE_EXCEPTION ||
            !isAllocatedReference(rawReference)
        ) {
            return -1
        }
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val page = pages[address ushr PAGE_SHIFT] ?: return -1
        return if (descriptorKind(page.descriptorKey) == EXCEPTION_DESCRIPTOR_KIND) {
            descriptorSemanticId(page.descriptorKey)
        } else {
            -1
        }
    }

    /** Starts a collection. Add roots with [markRoot], then finish or abort it. */
    fun beginCollection() {
        beginCollectionCycle()
    }

    /** Adds a root. Values that do not refer to this heap are ignored. */
    override fun markRoot(rawValue: Long) {
        submitCollectionRoot(rawValue, Int.MAX_VALUE)
    }

    /** Traces the submitted roots and reclaims unreachable objects. */
    fun finishCollection() {
        finishCollectionCycle(maximumRecycledPageCapacity = maximumPageCount)
    }

    /** Clears partial marks. This is safe to call after [finishCollection]. */
    fun abortCollection() {
        abortMarking()
    }

    fun snapshotStatistics(): Statistics {
        var bitmapWords = 0L
        var arrayClassSlackWords = 0L
        var freeSlotWords = 0L
        var virginSlotWords = 0L
        var pageTailWords = 0L
        var pageId = 1
        while (pageId < pageIdTop) {
            val page = pages[pageId]
            if (page != null) {
                bitmapWords += page.allocationBits.size + page.markBits.size
                if (page.descriptorKey != 0) {
                    val slotCount = PAGE_WORDS / page.slotWords
                    var allocatedSlots = 0
                    var bitmapIndex = 0
                    val activeBitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
                    while (bitmapIndex < activeBitmapWords) {
                        allocatedSlots += page.allocationBits[bitmapIndex].countOneBits()
                        bitmapIndex++
                    }
                    freeSlotWords += (page.bumpSlot - allocatedSlots).toLong() * page.slotWords
                    virginSlotWords += (slotCount - page.bumpSlot).toLong() * page.slotWords
                    pageTailWords += PAGE_WORDS - slotCount * page.slotWords
                    if (descriptorKind(page.descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
                        val pageBase = pagePayloadWordOffset(pageId)
                        var slotIndex = 0
                        while (slotIndex < slotCount) {
                            if (slotIsAllocated(page, slotIndex)) {
                                val length =
                                    payloadWords[pageBase + slotIndex * page.slotWords].toInt()
                                arrayClassSlackWords += page.slotWords - length - 1L
                            }
                            slotIndex++
                        }
                    }
                }
            }
            pageId++
        }

        var descriptorCount = 0
        var descriptorId = 0
        while (descriptorId < aggregateDescriptors.size) {
            if (aggregateDescriptors[descriptorId] != null) descriptorCount++
            descriptorId++
        }
        descriptorId = 0
        while (descriptorId < exceptionDescriptors.size) {
            if (exceptionDescriptors[descriptorId] != null) descriptorCount++
            descriptorId++
        }

        return Statistics(
            activePageCount = activePageCount,
            committedWords = payloadWords.size.toLong(),
            allocatedSlotWords = allocatedSlotWords,
            dedicatedPayloadWords = dedicatedPayloadWords,
            retainedPayloadWords = payloadWords.size + dedicatedPayloadWords,
            arrayClassSlackWords = arrayClassSlackWords,
            freeSlotWords = freeSlotWords,
            virginSlotWords = virginSlotWords,
            pageTailWords = pageTailWords,
            dedicatedArrayCount = dedicatedActiveCount,
            markWorklistCapacity = markWorklist.size,
            markWorklistPeakSize = markWorklistPeakSize,
            bitmapWords = bitmapWords,
            descriptorCount = descriptorCount,
            pageDirectoryCapacity = pages.size,
            recycledPageCount = recycledPageCount,
        )
    }

    internal fun beginMarkingForTesting() {
        beginCollectionCycle()
    }

    private fun beginCollectionCycle() {
        markWorklistPeakSize = 0
    }

    internal fun markRootForTesting(
        rawValue: Long,
        maximumWorklistCapacity: Int = Int.MAX_VALUE,
    ) {
        submitCollectionRoot(rawValue, maximumWorklistCapacity)
    }

    private fun submitCollectionRoot(
        rawValue: Long,
        maximumWorklistCapacity: Int,
    ) {
        markCandidate(rawValue, maximumWorklistCapacity)
    }

    internal fun drainMarkWorklistForTesting(
        maximumWorklistCapacity: Int = Int.MAX_VALUE,
    ) {
        drainMarkWorklist(maximumWorklistCapacity)
    }

    private fun drainMarkWorklist(maximumWorklistCapacity: Int) {
        while (markWorklistSize != 0) {
            val address = markWorklist[--markWorklistSize]
            traceMarkedObject(address, maximumWorklistCapacity)
        }
    }

    internal fun abortMarkingForTesting() {
        abortMarking()
    }

    internal fun finishCollectionForTesting(
        maximumRecycledPageCapacity: Int = maximumPageCount,
    ) {
        finishCollectionCycle(maximumRecycledPageCapacity)
    }

    internal fun isMarkedForTesting(rawReference: Long): Boolean {
        if (!isAllocatedReference(rawReference)) return false
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        if (isDedicatedAddress(address)) {
            val words = dedicatedPayloads[dedicatedId(address)] ?: return false
            return words[0] and DEDICATED_MARK_BIT != 0L
        }
        val page = pages[address ushr PAGE_SHIFT] ?: return false
        val slotIndex = (address and PAGE_MASK) / page.slotWords
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        return page.markBits[slotIndex ushr 6] and bit != 0L
    }

    internal fun markWorklistCapacityForTesting(): Int = markWorklist.size

    internal fun markWorklistPeakSizeForTesting(): Int = markWorklistPeakSize

    internal fun markedObjectCountForTesting(): Int {
        var markedObjects = 0
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val page = checkNotNull(pages[activePageIds[activeIndex]])
            val bitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
            var bitmapIndex = 0
            while (bitmapIndex < bitmapWords) {
                markedObjects += page.markBits[bitmapIndex].countOneBits()
                bitmapIndex++
            }
            activeIndex++
        }
        var dedicatedIndex = 0
        while (dedicatedIndex < dedicatedActiveCount) {
            val words = checkNotNull(dedicatedPayloads[dedicatedIds[dedicatedIndex]])
            if (words[0] and DEDICATED_MARK_BIT != 0L) markedObjects++
            dedicatedIndex++
        }
        return markedObjects
    }

    private fun markCandidate(
        rawValue: Long,
        maximumWorklistCapacity: Int,
    ) {
        when (rawValue and RV_TYPE_MASK) {
            RV_TYPE_STRUCT -> markAggregateCandidate(
                rawValue,
                STRUCT_DESCRIPTOR_KIND,
                maximumWorklistCapacity,
            )
            RV_TYPE_ARRAY -> markAggregateCandidate(
                rawValue,
                ARRAY_DESCRIPTOR_KIND,
                maximumWorklistCapacity,
            )
            RV_TYPE_EXCEPTION -> markAggregateCandidate(
                rawValue,
                EXCEPTION_DESCRIPTOR_KIND,
                maximumWorklistCapacity,
            )
        }
    }

    private fun markAggregateCandidate(
        rawReference: Long,
        expectedDescriptorKind: Int,
        maximumWorklistCapacity: Int,
    ) {
        val logicalAddress = rawReference shr RV_SHIFT_BITS
        if (logicalAddress <= 0L || logicalAddress > Int.MAX_VALUE) return
        val address = logicalAddress.toInt()
        if (isDedicatedAddress(address)) {
            if (expectedDescriptorKind != ARRAY_DESCRIPTOR_KIND) return
            markDedicatedCandidate(address, maximumWorklistCapacity)
            return
        }

        val pageId = address ushr PAGE_SHIFT
        if (pageId == NO_PAGE_ID || pageId >= pageIdTop) return
        val page = pages[pageId] ?: return
        if (
            page.descriptorKey == 0 ||
            descriptorKind(page.descriptorKey) != expectedDescriptorKind ||
            !descriptorExists(page.descriptorKey)
        ) {
            return
        }

        val wordOffset = address and PAGE_MASK
        val slotIndex = wordOffset / page.slotWords
        if (slotIndex * page.slotWords != wordOffset) return
        val slotCount = PAGE_WORDS / page.slotWords
        if (slotIndex >= slotCount) return
        val bitmapIndex = slotIndex ushr 6
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        if (page.allocationBits[bitmapIndex] and bit == 0L) return
        if (page.markBits[bitmapIndex] and bit != 0L) return

        ensureMarkWorklistCapacity(markWorklistSize + 1, maximumWorklistCapacity)
        page.markBits[bitmapIndex] = page.markBits[bitmapIndex] or bit
        pushMarkedAddress(address)
    }

    private fun markDedicatedCandidate(
        address: Int,
        maximumWorklistCapacity: Int,
    ) {
        val dedicatedId = dedicatedId(address)
        if (dedicatedId == 0 || dedicatedId >= dedicatedIdTop) return
        val words = dedicatedPayloads.getOrNull(dedicatedId) ?: return
        val metadata = words[0]
        if (metadata and DEDICATED_MARK_BIT != 0L) return
        val descriptorKey = metadata.toInt()
        if (arrayDescriptor(descriptorKey) == null) return

        ensureMarkWorklistCapacity(markWorklistSize + 1, maximumWorklistCapacity)
        words[0] = metadata or DEDICATED_MARK_BIT
        pushMarkedAddress(address)
    }

    private fun pushMarkedAddress(address: Int) {
        markWorklist[markWorklistSize++] = address
        if (markWorklistSize > markWorklistPeakSize) {
            markWorklistPeakSize = markWorklistSize
        }
    }

    private fun ensureMarkWorklistCapacity(
        requiredCapacity: Int,
        maximumCapacity: Int,
    ) {
        if (requiredCapacity <= markWorklist.size) return
        if (requiredCapacity < 0 || requiredCapacity > maximumCapacity) {
            throw GuestHeapOutOfMemoryError("mark worklist capacity exhausted")
        }
        markWorklist = markWorklist.copyOf(
            grownPrimitiveCapacity(markWorklist.size, requiredCapacity, maximumCapacity),
        )
    }

    private fun traceMarkedObject(
        address: Int,
        maximumWorklistCapacity: Int,
    ) {
        if (isDedicatedAddress(address)) {
            val words = checkNotNull(dedicatedPayloads[dedicatedId(address)])
            val descriptor = checkNotNull(arrayDescriptor(words[0].toInt()))
            if (!descriptor.elementsMayContainReferences) return
            var elementIndex = 1
            while (elementIndex < words.size) {
                markCandidate(words[elementIndex], maximumWorklistCapacity)
                elementIndex++
            }
            return
        }

        val page = checkNotNull(pages[address ushr PAGE_SHIFT])
        val payloadOffset = address - PAGE_WORDS
        when (descriptorKind(page.descriptorKey)) {
            STRUCT_DESCRIPTOR_KIND, EXCEPTION_DESCRIPTOR_KIND -> {
                val descriptor = checkNotNull(fixedDescriptor(page.descriptorKey))
                val referenceFieldIndices = descriptor.referenceFieldIndices
                var referenceIndex = 0
                while (referenceIndex < referenceFieldIndices.size) {
                    markCandidate(
                        payloadWords[payloadOffset + referenceFieldIndices[referenceIndex]],
                        maximumWorklistCapacity,
                    )
                    referenceIndex++
                }
            }
            ARRAY_DESCRIPTOR_KIND -> {
                val descriptor = checkNotNull(arrayDescriptor(page.descriptorKey))
                if (!descriptor.elementsMayContainReferences) return
                val length = payloadWords[payloadOffset].toInt()
                var elementIndex = 0
                while (elementIndex < length) {
                    markCandidate(
                        payloadWords[payloadOffset + elementIndex + 1],
                        maximumWorklistCapacity,
                    )
                    elementIndex++
                }
            }
            else -> error("marked page has an invalid descriptor kind")
        }
    }

    private fun finishCollectionCycle(maximumRecycledPageCapacity: Int) {
        drainMarkWorklist(Int.MAX_VALUE)
        ensureRecycledPageCapacity(
            recycledPageCount + activePageCount,
            maximumRecycledPageCapacity,
        )
        val emptyPageCount = checkCollectionMarksBeforeSweep()

        clearAvailabilityChainsForSweep()
        val liveOrdinarySlotWords = sweepOrdinaryPages(emptyPageCount)
        sweepDedicatedArrays()
        rebuildSurvivingAvailabilityChains()
        allocatedSlotWords = liveOrdinarySlotWords + dedicatedPayloadWords
        markWorklistSize = 0
        retainMarkWorklistAfterCycle()
    }

    private fun checkCollectionMarksBeforeSweep(): Int {
        check(markWorklistSize == 0)
        val emptyPageStart = recycledPageCount
        var emptyPageCount = 0
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val pageId = activePageIds[activeIndex]
            val page = checkNotNull(pages[pageId])
            val activeBitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
            var hasMarks = false
            var bitmapIndex = 0
            while (bitmapIndex < activeBitmapWords) {
                val marks = page.markBits[bitmapIndex]
                check(marks and page.allocationBits[bitmapIndex].inv() == 0L)
                if (marks != 0L) hasMarks = true
                bitmapIndex++
            }
            if (!hasMarks) {
                recycledPageIds[emptyPageStart + emptyPageCount] = pageId
                emptyPageCount++
            }
            activeIndex++
        }
        var dedicatedIndex = 0
        while (dedicatedIndex < dedicatedActiveCount) {
            val words = checkNotNull(dedicatedPayloads[dedicatedIds[dedicatedIndex]])
            check(arrayDescriptor(words[0].toInt()) != null)
            dedicatedIndex++
        }
        return emptyPageCount
    }

    private fun clearAvailabilityChainsForSweep() {
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val page = checkNotNull(pages[activePageIds[activeIndex]])
            clearAvailabilityHead(page.descriptorKey, page.slotWords)
            page.nextAvailablePageId = NO_PAGE_ID
            activeIndex++
        }
    }

    /** Compacts active page IDs and adds reclaimed slots to each page's free list. */
    private fun sweepOrdinaryPages(emptyPageCount: Int): Long {
        val previousActivePageCount = activePageCount
        var emptyPageIndex = recycledPageCount
        val emptyPageEnd = emptyPageIndex + emptyPageCount
        var readIndex = 0
        var writeIndex = 0
        var liveSlotWords = 0L
        while (readIndex < previousActivePageCount) {
            val pageId = activePageIds[readIndex]
            val page = checkNotNull(pages[pageId])
            val activeBitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
            if (
                emptyPageIndex < emptyPageEnd &&
                recycledPageIds[emptyPageIndex] == pageId
            ) {
                page.allocationBits.fill(0L, 0, activeBitmapWords)
                page.markBits.fill(0L, 0, activeBitmapWords)
                page.recycle()
                recycledPageIds[recycledPageCount++] = pageId
                emptyPageIndex++
                readIndex++
                continue
            }

            val pagePayloadOffset = pagePayloadWordOffset(pageId)
            var bitmapIndex = 0
            while (bitmapIndex < activeBitmapWords) {
                val marks = page.markBits[bitmapIndex]
                var dead = page.allocationBits[bitmapIndex] and marks.inv()
                page.allocationBits[bitmapIndex] = marks
                page.markBits[bitmapIndex] = 0L
                liveSlotWords += marks.countOneBits().toLong() * page.slotWords
                while (dead != 0L) {
                    val bitIndex = dead.countTrailingZeroBits()
                    val slotIndex = (bitmapIndex shl 6) + bitIndex
                    payloadWords[pagePayloadOffset + slotIndex * page.slotWords] =
                        page.freeHead.toLong()
                    page.freeHead = slotIndex
                    dead = dead and (dead - 1L)
                }
                bitmapIndex++
            }
            activePageIds[writeIndex++] = pageId
            readIndex++
        }

        var clearIndex = writeIndex
        while (clearIndex < previousActivePageCount) {
            activePageIds[clearIndex] = NO_PAGE_ID
            clearIndex++
        }
        check(emptyPageIndex == emptyPageEnd)
        activePageCount = writeIndex
        return liveSlotWords
    }

    private fun sweepDedicatedArrays() {
        var activeIndex = 0
        var activeEnd = dedicatedActiveCount
        var livePayloadWords = 0L
        while (activeIndex < activeEnd) {
            val dedicatedId = dedicatedIds[activeIndex]
            val words = checkNotNull(dedicatedPayloads[dedicatedId])
            if (words[0] and DEDICATED_MARK_BIT != 0L) {
                words[0] = words[0] and DEDICATED_MARK_BIT.inv()
                livePayloadWords += words.size
                activeIndex++
            } else {
                activeEnd--
                dedicatedIds[activeIndex] = dedicatedIds[activeEnd]
                dedicatedIds[activeEnd] = dedicatedId
                dedicatedPayloads[dedicatedId] = null
            }
        }
        dedicatedActiveCount = activeEnd
        dedicatedPayloadWords = livePayloadWords
    }

    private fun rebuildSurvivingAvailabilityChains() {
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val pageId = activePageIds[activeIndex]
            val page = checkNotNull(pages[pageId])
            if (pageHasAvailableSlot(page)) {
                if (descriptorKind(page.descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
                    val descriptor = checkNotNull(arrayDescriptor(page.descriptorKey))
                    val availablePageHeads = checkNotNull(descriptor.availablePageHeads)
                    val classIndex = arrayClassIndexForSlotWords(page.slotWords)
                    linkAvailableArrayPage(availablePageHeads, classIndex, page, pageId)
                } else {
                    linkAvailablePage(checkNotNull(fixedDescriptor(page.descriptorKey)), page, pageId)
                }
            }
            activeIndex++
        }
    }

    private fun abortMarking() {
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val page = checkNotNull(pages[activePageIds[activeIndex]])
            val activeBitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
            page.markBits.fill(0L, 0, activeBitmapWords)
            activeIndex++
        }
        var dedicatedIndex = 0
        while (dedicatedIndex < dedicatedActiveCount) {
            val words = checkNotNull(dedicatedPayloads[dedicatedIds[dedicatedIndex]])
            words[0] = words[0] and DEDICATED_MARK_BIT.inv()
            dedicatedIndex++
        }
        markWorklistSize = 0
        retainMarkWorklistAfterCycle()
        markWorklistPeakSize = 0
    }

    private fun retainMarkWorklistAfterCycle() {
        if (
            markWorklist.size > RETAINED_MARK_WORKLIST_CAPACITY &&
            markWorklistPeakSize <= RETAINED_MARK_WORKLIST_CAPACITY
        ) {
            markWorklist = EMPTY_MARK_WORKLIST
        }
    }

    private fun requireArrayDescriptor(descriptorKey: Int): ArrayDescriptor {
        require(descriptorKind(descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
            "descriptorKey does not identify an array descriptor"
        }
        val semanticId = descriptorSemanticId(descriptorKey)
        val descriptor = aggregateDescriptors.getOrNull(semanticId) as? ArrayDescriptor
        require(descriptor != null) {
            "descriptorKey does not identify a registered array descriptor"
        }
        return descriptor
    }

    private fun validateArrayLength(length: Int) {
        require(length in 0 until Int.MAX_VALUE) {
            "length must be non-negative and leave room for array metadata"
        }
    }

    private fun requireElementIndex(
        length: Int,
        index: Int,
    ) {
        require(index in 0 until length) { "array element index is out of bounds" }
    }

    private fun requireRange(
        total: Int,
        offset: Int,
        length: Int,
        rangeName: String,
    ) {
        require(
            offset >= 0 &&
                length >= 0 &&
                offset <= total &&
                length <= total - offset,
        ) { "$rangeName range is out of bounds" }
    }

    private fun requireDataRange(
        sourceSize: Int,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ) {
        require(elementByteWidth == 1 || elementByteWidth == 2 || elementByteWidth == 4 || elementByteWidth == 8) {
            "elementByteWidth must be 1, 2, 4, or 8"
        }
        require(sourceByteOffset >= 0 && sourceByteOffset <= sourceSize) {
            "source byte offset is out of bounds"
        }
        require(length >= 0) { "length must be non-negative" }
        val availableBytes = sourceSize.toLong() - sourceByteOffset
        require(length.toLong() * elementByteWidth <= availableBytes) {
            "source byte range is out of bounds"
        }
    }

    private fun allocateOrdinaryArrayFilled(
        descriptorKey: Int,
        descriptor: ArrayDescriptor,
        length: Int,
        value: Long,
    ): Long {
        val reservation = reserveOrdinaryArraySlot(descriptorKey, descriptor, length)
        val pageId = (reservation ushr Int.SIZE_BITS).toInt()
        val slotIndex = reservation.toInt()
        val page = checkNotNull(pages[pageId])
        val headerOffset = pagePayloadWordOffset(pageId) + slotIndex * page.slotWords
        payloadWords[headerOffset] = length.toLong()
        payloadWords.fill(value, headerOffset + 1, headerOffset + length + 1)
        return publishOrdinaryArray(pageId, page, slotIndex)
    }

    private fun allocateOrdinaryArrayFromElements(
        descriptorKey: Int,
        descriptor: ArrayDescriptor,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ): Long {
        val reservation = reserveOrdinaryArraySlot(descriptorKey, descriptor, length)
        val pageId = (reservation ushr Int.SIZE_BITS).toInt()
        val slotIndex = reservation.toInt()
        val page = checkNotNull(pages[pageId])
        val headerOffset = pagePayloadWordOffset(pageId) + slotIndex * page.slotWords
        payloadWords[headerOffset] = length.toLong()
        source.copyInto(
            destination = payloadWords,
            destinationOffset = headerOffset + 1,
            startIndex = sourceOffset,
            endIndex = sourceOffset + length,
        )
        return publishOrdinaryArray(pageId, page, slotIndex)
    }

    private fun allocateOrdinaryArrayFromData(
        descriptorKey: Int,
        descriptor: ArrayDescriptor,
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ): Long {
        val reservation = reserveOrdinaryArraySlot(descriptorKey, descriptor, length)
        val pageId = (reservation ushr Int.SIZE_BITS).toInt()
        val slotIndex = reservation.toInt()
        val page = checkNotNull(pages[pageId])
        val headerOffset = pagePayloadWordOffset(pageId) + slotIndex * page.slotWords
        payloadWords[headerOffset] = length.toLong()
        decodeDataInto(
            source,
            sourceByteOffset,
            length,
            elementByteWidth,
            payloadWords,
            headerOffset + 1,
        )
        return publishOrdinaryArray(pageId, page, slotIndex)
    }

    private fun reserveOrdinaryArraySlot(
        descriptorKey: Int,
        descriptor: ArrayDescriptor,
        length: Int,
    ): Long {
        val layout = ARRAY_LAYOUT_BY_LENGTH[length]
        val classIndex = layout and ARRAY_CLASS_INDEX_MASK
        val slotWords = layout ushr ARRAY_CLASS_INDEX_BITS
        val availablePageHeads = descriptor.availablePageHeads ?: IntArray(ARRAY_CLASS_COUNT)
        val pageId = if (availablePageHeads[classIndex] != NO_PAGE_ID) {
            availablePageHeads[classIndex]
        } else {
            commitArrayPage(
                descriptorKey,
                descriptor,
                availablePageHeads,
                classIndex,
                slotWords,
            )
        }

        val page = checkNotNull(pages[pageId])
        check(page.descriptorKey == descriptorKey && page.slotWords == slotWords)
        var slotIndex = page.bumpSlot
        if ((slotIndex + 1) * slotWords <= PAGE_WORDS) {
            page.bumpSlot = slotIndex + 1
        } else {
            slotIndex = page.freeHead
            check(slotIndex != NO_SLOT)
            page.freeHead = payloadWords[pagePayloadWordOffset(pageId) + slotIndex * slotWords].toInt()
        }
        if ((page.bumpSlot + 1) * slotWords > PAGE_WORDS && page.freeHead == NO_SLOT) {
            check(availablePageHeads[classIndex] == pageId)
            availablePageHeads[classIndex] = page.nextAvailablePageId
            page.nextAvailablePageId = NO_PAGE_ID
        }
        return (pageId.toLong() shl Int.SIZE_BITS) or (slotIndex.toLong() and DEDICATED_DESCRIPTOR_MASK)
    }

    private fun publishOrdinaryArray(
        pageId: Int,
        page: Page,
        slotIndex: Int,
    ): Long {
        val bitmapIndex = slotIndex ushr 6
        val allocationBit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        check(page.allocationBits[bitmapIndex] and allocationBit == 0L)
        allocatedSlotWords += page.slotWords
        page.allocationBits[bitmapIndex] = page.allocationBits[bitmapIndex] or allocationBit
        val wordOffset = slotIndex * page.slotWords
        val address = (pageId shl PAGE_SHIFT) or wordOffset
        return (address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
    }

    private fun commitArrayPage(
        descriptorKey: Int,
        descriptor: ArrayDescriptor,
        availablePageHeads: IntArray,
        classIndex: Int,
        slotWords: Int,
    ): Int {
        if (recycledPageCount == 0 && pageIdTop > maximumPageCount) {
            throw GuestHeapOutOfMemoryError("maximum ordinary page count exhausted")
        }
        val preparedActivePageIds = activePageIdsWithCapacity(activePageCount + 1)
        if (recycledPageCount > 0) {
            val recycledIndex = recycledPageCount - 1
            val pageId = recycledPageIds[recycledIndex]
            val page = checkNotNull(pages[pageId])
            page.retask(descriptorKey, slotWords)
            activePageIds = preparedActivePageIds
            descriptor.availablePageHeads = availablePageHeads
            recycledPageCount = recycledIndex
            linkAvailableArrayPage(availablePageHeads, classIndex, page, pageId)
            activePageIds[activePageCount++] = pageId
            return pageId
        }

        val pageId = pageIdTop
        val preparedPages = pageDirectoryWithCapacity(pageId + 1)
        val preparedPayloadWords = payloadWordsWithCapacity(pageId)
        val page = Page(
            allocationBits = LongArray(bitmapWords(slotWords, PAGE_WORDS)),
            markBits = LongArray(bitmapWords(slotWords, PAGE_WORDS)),
            descriptorKey = descriptorKey,
            slotWords = slotWords,
        )
        payloadWords = preparedPayloadWords
        pages = preparedPages
        activePageIds = preparedActivePageIds
        descriptor.availablePageHeads = availablePageHeads
        pages[pageId] = page
        linkAvailableArrayPage(availablePageHeads, classIndex, page, pageId)
        activePageIds[activePageCount++] = pageId
        pageIdTop = pageId + 1
        return pageId
    }

    private fun linkAvailableArrayPage(
        availablePageHeads: IntArray,
        classIndex: Int,
        page: Page,
        pageId: Int,
    ) {
        page.nextAvailablePageId = availablePageHeads[classIndex]
        availablePageHeads[classIndex] = pageId
    }

    private fun allocateDedicatedArrayFilled(
        descriptorKey: Int,
        length: Int,
        value: Long,
    ): Long {
        requireDedicatedCapacity(length + 1)
        val words = LongArray(length + 1)
        words[0] = descriptorKey.toLong() and DEDICATED_DESCRIPTOR_MASK
        words.fill(value, 1, words.size)
        return publishDedicatedArray(words)
    }

    private fun allocateDedicatedArrayFromElements(
        descriptorKey: Int,
        source: LongArray,
        sourceOffset: Int,
        length: Int,
    ): Long {
        requireDedicatedCapacity(length + 1)
        val words = LongArray(length + 1)
        words[0] = descriptorKey.toLong() and DEDICATED_DESCRIPTOR_MASK
        source.copyInto(words, 1, sourceOffset, sourceOffset + length)
        return publishDedicatedArray(words)
    }

    private fun allocateDedicatedArrayFromData(
        descriptorKey: Int,
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
    ): Long {
        requireDedicatedCapacity(length + 1)
        val words = LongArray(length + 1)
        words[0] = descriptorKey.toLong() and DEDICATED_DESCRIPTOR_MASK
        decodeDataInto(source, sourceByteOffset, length, elementByteWidth, words, 1)
        return publishDedicatedArray(words)
    }

    private fun requireDedicatedCapacity(requestedWords: Int) {
        val retainedWordLimit = maximumPageCount.toLong() * PAGE_WORDS
        if (payloadWords.size + dedicatedPayloadWords + requestedWords > retainedWordLimit) {
            throw GuestHeapOutOfMemoryError("retained guest payload capacity exhausted")
        }
    }

    private fun publishDedicatedArray(words: LongArray): Long {
        val issuedCount = dedicatedIdTop - 1
        val dedicatedId: Int
        if (dedicatedActiveCount < issuedCount) {
            val lastIssuedIndex = issuedCount - 1
            dedicatedId = dedicatedIds[lastIssuedIndex]
            dedicatedIds[lastIssuedIndex] = dedicatedIds[dedicatedActiveCount]
            dedicatedIds[dedicatedActiveCount] = dedicatedId
        } else {
            if (dedicatedIdTop > DEDICATED_ID_MASK) {
                throw GuestHeapOutOfMemoryError("maximum dedicated array ID exhausted")
            }
            dedicatedId = dedicatedIdTop
            val preparedPayloads = dedicatedPayloadDirectoryWithCapacity(dedicatedId + 1)
            val preparedIds = dedicatedIdsWithCapacity(issuedCount + 1)
            dedicatedPayloads = preparedPayloads
            dedicatedIds = preparedIds
            dedicatedIds[dedicatedActiveCount] = dedicatedId
            dedicatedIdTop = dedicatedId + 1
        }
        dedicatedPayloads[dedicatedId] = words
        dedicatedActiveCount++
        dedicatedPayloadWords += words.size
        allocatedSlotWords += words.size
        val address = DEDICATED_ADDRESS_BIT or dedicatedId
        return (address.toLong() shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
    }

    private fun dedicatedPayloadDirectoryWithCapacity(requiredCapacity: Int): Array<LongArray?> =
        if (requiredCapacity <= dedicatedPayloads.size) {
            dedicatedPayloads
        } else {
            dedicatedPayloads.copyOf(
                grownCapacity(dedicatedPayloads.size, requiredCapacity, DEDICATED_ID_MASK + 1),
            )
        }

    private fun dedicatedIdsWithCapacity(requiredCapacity: Int): IntArray =
        if (requiredCapacity <= dedicatedIds.size) {
            dedicatedIds
        } else {
            dedicatedIds.copyOf(
                grownPrimitiveCapacity(dedicatedIds.size, requiredCapacity, DEDICATED_ID_MASK),
            )
        }

    private fun decodeDataInto(
        source: UByteArray,
        sourceByteOffset: Int,
        length: Int,
        elementByteWidth: Int,
        destination: LongArray,
        destinationOffset: Int,
    ) {
        var sourceOffset = sourceByteOffset
        var destinationIndex = destinationOffset
        val destinationEnd = destinationOffset + length
        while (destinationIndex < destinationEnd) {
            destination[destinationIndex] = decodeDataWord(source, sourceOffset, elementByteWidth)
            sourceOffset += elementByteWidth
            destinationIndex++
        }
    }

    private fun decodeDataWord(
        source: UByteArray,
        offset: Int,
        elementByteWidth: Int,
    ): Long = when (elementByteWidth) {
        1 -> source[offset].toLong()
        2 ->
            (source[offset].toLong() or (source[offset + 1].toLong() shl 8))
                .toShort()
                .toLong()
        4 ->
            (
                source[offset].toLong() or
                    (source[offset + 1].toLong() shl 8) or
                    (source[offset + 2].toLong() shl 16) or
                    (source[offset + 3].toLong() shl 24)
            )
                .toInt()
                .toLong()
        else ->
            source[offset].toLong() or
                (source[offset + 1].toLong() shl 8) or
                (source[offset + 2].toLong() shl 16) or
                (source[offset + 3].toLong() shl 24) or
                (source[offset + 4].toLong() shl 32) or
                (source[offset + 5].toLong() shl 40) or
                (source[offset + 6].toLong() shl 48) or
                (source[offset + 7].toLong() shl 56)
    }

    private fun isDedicatedAddress(address: Int): Boolean = address and DEDICATED_ADDRESS_BIT != 0

    private fun dedicatedId(address: Int): Int = address and DEDICATED_ID_MASK

    private fun registerAggregateFixed(
        semanticId: Int,
        payloadWords: Int,
        referenceFieldIndices: IntArray,
        descriptorKind: Int,
    ): Int {
        validateSemanticId(semanticId)
        val normalizedReferenceFieldIndices =
            normalizedReferenceFieldIndices(payloadWords, referenceFieldIndices)
        val descriptorKey = descriptorKey(semanticId, descriptorKind)
        val existing = aggregateDescriptors.getOrNull(semanticId)
        if (existing != null) {
            require(
                existing is FixedDescriptor &&
                    existing.hasLayout(payloadWords, normalizedReferenceFieldIndices),
            ) { "semanticId $semanticId is already registered with a different layout" }
            return descriptorKey
        }

        ensureAggregateDescriptorCapacity(semanticId + 1)
        aggregateDescriptors[semanticId] = FixedDescriptor(
            payloadWords = payloadWords,
            referenceFieldIndices = normalizedReferenceFieldIndices,
        )
        return descriptorKey
    }

    private fun allocateFixed(
        descriptorKey: Int,
        descriptor: FixedDescriptor,
        source: LongArray,
        sourceOffset: Int = 0,
        referenceTag: Long,
    ): Long {
        val pageId = if (descriptor.availablePageHead != NO_PAGE_ID) {
            descriptor.availablePageHead
        } else {
            commitFixedPage(descriptorKey)
        }
        return allocateFixedOnAvailablePage(
            descriptorKey = descriptorKey,
            descriptor = descriptor,
            source = source,
            sourceOffset = sourceOffset,
            referenceTag = referenceTag,
            pageId = pageId,
        )
    }

    private fun allocateFixedOnAvailablePage(
        descriptorKey: Int,
        descriptor: FixedDescriptor,
        source: LongArray,
        sourceOffset: Int,
        referenceTag: Long,
        pageId: Int,
    ): Long {
        val page = checkNotNull(pages[pageId])
        check(page.descriptorKey == descriptorKey)
        val slotIndex = page.bumpSlot
        if ((slotIndex + 1) * page.slotWords > PAGE_WORDS) {
            return allocateFixedFromFreeList(
                descriptor = descriptor,
                source = source,
                sourceOffset = sourceOffset,
                referenceTag = referenceTag,
                pageId = pageId,
                page = page,
            )
        }
        page.bumpSlot = slotIndex + 1

        if (
            (page.bumpSlot + 1) * page.slotWords > PAGE_WORDS &&
            page.freeHead == NO_SLOT
        ) {
            unlinkAvailableHead(descriptor, page, pageId)
        }
        return initializeFixedSlot(
            source,
            sourceOffset,
            descriptor.payloadWords,
            referenceTag,
            pageId,
            page,
            slotIndex,
        )
    }

    private fun allocateFixedFromFreeList(
        descriptor: FixedDescriptor,
        source: LongArray,
        sourceOffset: Int,
        referenceTag: Long,
        pageId: Int,
        page: Page,
    ): Long {
        val slotIndex = page.freeHead
        check(slotIndex != NO_SLOT)
        page.freeHead = payloadWords[pagePayloadWordOffset(pageId) + slotIndex * page.slotWords].toInt()
        if (page.freeHead == NO_SLOT) unlinkAvailableHead(descriptor, page, pageId)
        return initializeFixedSlot(
            source,
            sourceOffset,
            descriptor.payloadWords,
            referenceTag,
            pageId,
            page,
            slotIndex,
        )
    }

    private fun unlinkAvailableHead(
        descriptor: FixedDescriptor,
        page: Page,
        pageId: Int,
    ) {
        check(descriptor.availablePageHead == pageId)
        descriptor.availablePageHead = page.nextAvailablePageId
        page.nextAvailablePageId = NO_PAGE_ID
    }

    private fun initializeFixedSlot(
        source: LongArray,
        sourceOffset: Int,
        payloadWordCount: Int,
        referenceTag: Long,
        pageId: Int,
        page: Page,
        slotIndex: Int,
    ): Long {
        val wordOffset = slotIndex * page.slotWords
        source.copyInto(
            destination = payloadWords,
            destinationOffset = pagePayloadWordOffset(pageId) + wordOffset,
            startIndex = sourceOffset,
            endIndex = sourceOffset + payloadWordCount,
        )
        val bitmapIndex = slotIndex ushr 6
        val allocationBit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        check(page.allocationBits[bitmapIndex] and allocationBit == 0L)
        allocatedSlotWords += page.slotWords
        page.allocationBits[bitmapIndex] = page.allocationBits[bitmapIndex] or allocationBit

        val address = (pageId shl PAGE_SHIFT) or wordOffset
        return (address.toLong() shl RV_SHIFT_BITS) or referenceTag
    }

    private fun normalizedReferenceFieldIndices(
        payloadWords: Int,
        referenceFieldIndices: IntArray,
    ): IntArray {
        require(payloadWords in 0..PAGE_WORDS) {
            "payloadWords must be between 0 and $PAGE_WORDS"
        }
        if (referenceFieldIndices.isEmpty()) return EMPTY_REFERENCE_FIELD_INDICES

        val normalized = referenceFieldIndices.copyOf()
        normalized.sort()
        var index = 0
        var previous = NO_SLOT
        while (index < normalized.size) {
            val fieldIndex = normalized[index]
            require(fieldIndex in 0 until payloadWords) {
                "reference field index $fieldIndex is outside payloadWords $payloadWords"
            }
            require(fieldIndex != previous) {
                "reference field index $fieldIndex is duplicated"
            }
            previous = fieldIndex
            index++
        }
        return normalized
    }

    private fun validateSemanticId(semanticId: Int) {
        require(semanticId in 0..MAXIMUM_SEMANTIC_ID) {
            "semanticId must be a non-negative 30-bit value"
        }
    }

    private fun descriptorKey(
        semanticId: Int,
        descriptorKind: Int,
    ): Int = (semanticId shl DESCRIPTOR_KIND_BITS) or descriptorKind

    private fun ensureAggregateDescriptorCapacity(requiredCapacity: Int) {
        if (requiredCapacity <= aggregateDescriptors.size) return
        aggregateDescriptors = aggregateDescriptors.copyOf(
            grownCapacity(aggregateDescriptors.size, requiredCapacity, MAXIMUM_SEMANTIC_ID + 1),
        )
    }

    private fun ensureExceptionDescriptorCapacity(requiredCapacity: Int) {
        if (requiredCapacity <= exceptionDescriptors.size) return
        exceptionDescriptors = exceptionDescriptors.copyOf(
            grownCapacity(exceptionDescriptors.size, requiredCapacity, MAXIMUM_SEMANTIC_ID + 1),
        )
    }

    private fun grownCapacity(
        currentCapacity: Int,
        requiredCapacity: Int,
        maximumCapacity: Int,
    ): Int {
        check(requiredCapacity in 0..maximumCapacity)
        var capacity = currentCapacity
        while (capacity < requiredCapacity) {
            val doubled = capacity.toLong() shl 1
            capacity = minOf(maximumCapacity.toLong(), doubled).toInt()
            if (capacity < requiredCapacity && capacity == maximumCapacity) {
                throw GuestHeapOutOfMemoryError("heap metadata capacity exhausted")
            }
        }
        return capacity
    }

    private fun descriptorKind(descriptorKey: Int): Int = descriptorKey and DESCRIPTOR_KIND_MASK

    private fun descriptorSemanticId(descriptorKey: Int): Int = descriptorKey ushr DESCRIPTOR_KIND_BITS

    private fun fixedDescriptor(descriptorKey: Int): FixedDescriptor? {
        val semanticId = descriptorSemanticId(descriptorKey)
        return when (descriptorKind(descriptorKey)) {
            STRUCT_DESCRIPTOR_KIND -> aggregateDescriptors.getOrNull(semanticId) as? FixedDescriptor
            EXCEPTION_DESCRIPTOR_KIND -> exceptionDescriptors.getOrNull(semanticId)
            else -> null
        }
    }

    private fun arrayDescriptor(descriptorKey: Int): ArrayDescriptor? {
        if (descriptorKind(descriptorKey) != ARRAY_DESCRIPTOR_KIND) return null
        return aggregateDescriptors.getOrNull(descriptorSemanticId(descriptorKey)) as? ArrayDescriptor
    }

    private fun descriptorExists(descriptorKey: Int): Boolean =
        fixedDescriptor(descriptorKey) != null || arrayDescriptor(descriptorKey) != null

    private fun commitFixedPage(descriptorKey: Int): Int {
        val descriptor = checkNotNull(fixedDescriptor(descriptorKey)) {
            "descriptorKey does not identify a registered fixed descriptor"
        }
        if (recycledPageCount == 0 && pageIdTop > maximumPageCount) {
            throw GuestHeapOutOfMemoryError("maximum logical page count exhausted")
        }
        val preparedActivePageIds = activePageIdsWithCapacity(activePageCount + 1)

        val slotWords = maxOf(1, descriptor.payloadWords)
        if (recycledPageCount > 0) {
            val recycledIndex = recycledPageCount - 1
            val pageId = recycledPageIds[recycledIndex]
            val page = checkNotNull(pages[pageId])
            page.retask(descriptorKey, slotWords)
            activePageIds = preparedActivePageIds
            recycledPageCount = recycledIndex
            linkAvailablePage(descriptor, page, pageId)
            activePageIds[activePageCount++] = pageId
            return pageId
        }

        val pageId = pageIdTop
        val preparedPages = pageDirectoryWithCapacity(pageId + 1)
        val preparedPayloadWords = payloadWordsWithCapacity(pageId)
        val page = Page(
            allocationBits = LongArray(bitmapWords(slotWords, PAGE_WORDS)),
            markBits = LongArray(bitmapWords(slotWords, PAGE_WORDS)),
            descriptorKey = descriptorKey,
            slotWords = slotWords,
        )

        payloadWords = preparedPayloadWords
        pages = preparedPages
        activePageIds = preparedActivePageIds
        pages[pageId] = page
        linkAvailablePage(descriptor, page, pageId)
        activePageIds[activePageCount++] = pageId
        pageIdTop = pageId + 1
        return pageId
    }

    private fun canCommitOrdinaryPage(): Boolean {
        if (pageIdTop > maximumPageCount) return false
        val requiredWords = pageIdTop.toLong() * PAGE_WORDS
        return requiredWords + dedicatedPayloadWords <= maximumPayloadWords()
    }

    private fun payloadWordsWithCapacity(requiredPageCount: Int): LongArray {
        val requiredWords = requiredPageCount.toLong() * PAGE_WORDS
        if (requiredWords <= payloadWords.size) return payloadWords

        val retainedWordLimit = maximumPageCount.toLong() * PAGE_WORDS
        val maximumWords = retainedWordLimit - dedicatedPayloadWords
        if (requiredWords > maximumWords) {
            throw GuestHeapOutOfMemoryError("retained guest payload capacity exhausted")
        }
        val maximumArenaPages = maximumWords / PAGE_WORDS
        var capacityPages = maxOf(1L, payloadWords.size.toLong() ushr PAGE_SHIFT)
        while (capacityPages * PAGE_WORDS < requiredWords) {
            capacityPages = minOf(maximumArenaPages, capacityPages shl 1)
            if (capacityPages * PAGE_WORDS < requiredWords && capacityPages == maximumArenaPages) {
                throw GuestHeapOutOfMemoryError("ordinary payload capacity exhausted")
            }
        }
        val capacityWords = capacityPages * PAGE_WORDS
        check(capacityWords in requiredWords..maximumWords)
        return payloadWords.copyOf(capacityWords.toInt())
    }

    private fun pageDirectoryWithCapacity(requiredCapacity: Int): Array<Page?> =
        if (requiredCapacity <= pages.size) {
            pages
        } else {
            pages.copyOf(grownCapacity(pages.size, requiredCapacity, maximumPageCount + 1))
        }

    private fun activePageIdsWithCapacity(requiredCapacity: Int): IntArray =
        if (requiredCapacity <= activePageIds.size) {
            activePageIds
        } else {
            activePageIds.copyOf(
                grownPrimitiveCapacity(activePageIds.size, requiredCapacity, maximumPageCount),
            )
        }

    private fun ensureRecycledPageCapacity(
        requiredCapacity: Int,
        maximumCapacity: Int = maximumPageCount,
    ) {
        if (requiredCapacity <= recycledPageIds.size) return
        if (requiredCapacity > maximumCapacity) {
            throw GuestHeapOutOfMemoryError("recycled page capacity exhausted")
        }
        recycledPageIds = recycledPageIds.copyOf(
            grownPrimitiveCapacity(recycledPageIds.size, requiredCapacity, maximumCapacity),
        )
    }

    private fun grownPrimitiveCapacity(
        currentCapacity: Int,
        requiredCapacity: Int,
        maximumCapacity: Int,
    ): Int {
        if (currentCapacity != 0) {
            return grownCapacity(currentCapacity, requiredCapacity, maximumCapacity)
        }
        return minOf(maximumCapacity, maxOf(4, requiredCapacity))
    }

    private fun linkAvailablePage(
        descriptor: FixedDescriptor,
        page: Page,
        pageId: Int,
    ) {
        page.nextAvailablePageId = descriptor.availablePageHead
        descriptor.availablePageHead = pageId
    }

    private fun recycleEmptyPage(pageId: Int) {
        check(pageId in 1 until pageIdTop)
        val page = checkNotNull(pages[pageId])
        check(page.descriptorKey != 0) { "page is not active" }
        check(!hasAllocatedSlots(page)) { "only an empty page can be recycled" }
        check(!hasMarkedSlots(page)) { "a recycled page cannot retain mark bits" }

        ensureRecycledPageCapacity(recycledPageCount + 1)
        val descriptorKey = page.descriptorKey
        check(descriptorExists(descriptorKey))
        val activeIndex = activePageIndex(pageId)
        check(activeIndex != NO_SLOT)

        activePageCount--
        activePageIds[activeIndex] = activePageIds[activePageCount]
        activePageIds[activePageCount] = NO_PAGE_ID
        clearAvailabilityHead(descriptorKey, page.slotWords)
        page.recycle()
        recycledPageIds[recycledPageCount++] = pageId
        rebuildAvailabilityChains()
    }

    private fun clearAvailabilityHead(
        descriptorKey: Int,
        slotWords: Int,
    ) {
        when (
            val descriptor = if (descriptorKind(descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
                arrayDescriptor(descriptorKey)
            } else {
                fixedDescriptor(descriptorKey)
            }
        ) {
            is FixedDescriptor -> descriptor.availablePageHead = NO_PAGE_ID
            is ArrayDescriptor -> {
                val classIndex = arrayClassIndexForSlotWords(slotWords)
                descriptor.availablePageHeads?.set(classIndex, NO_PAGE_ID)
            }
            else -> error("page descriptor is not registered")
        }
    }

    private fun activePageIndex(pageId: Int): Int {
        var index = 0
        while (index < activePageCount) {
            if (activePageIds[index] == pageId) return index
            index++
        }
        return NO_SLOT
    }

    private fun hasAllocatedSlots(page: Page): Boolean {
        val bitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
        var index = 0
        while (index < bitmapWords) {
            if (page.allocationBits[index] != 0L) return true
            index++
        }
        return false
    }

    private fun hasMarkedSlots(page: Page): Boolean {
        val bitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
        var index = 0
        while (index < bitmapWords) {
            if (page.markBits[index] != 0L) return true
            index++
        }
        return false
    }

    private fun checkMarksAreAllocated(
        page: Page,
        activeBitmapWords: Int,
    ) {
        var bitmapIndex = 0
        while (bitmapIndex < activeBitmapWords) {
            check(page.markBits[bitmapIndex] and page.allocationBits[bitmapIndex].inv() == 0L)
            bitmapIndex++
        }
    }

    private fun isMarkedAddressForInvariants(address: Int): Boolean {
        if (isDedicatedAddress(address)) {
            val dedicatedId = dedicatedId(address)
            if (dedicatedId == 0 || dedicatedId >= dedicatedIdTop) return false
            val words = dedicatedPayloads.getOrNull(dedicatedId) ?: return false
            return words[0] and DEDICATED_MARK_BIT != 0L
        }
        val pageId = address ushr PAGE_SHIFT
        if (pageId == NO_PAGE_ID || pageId >= pageIdTop) return false
        val page = pages[pageId] ?: return false
        val wordOffset = address and PAGE_MASK
        val slotIndex = wordOffset / page.slotWords
        if (slotIndex * page.slotWords != wordOffset) return false
        val slotCount = PAGE_WORDS / page.slotWords
        if (slotIndex >= slotCount) return false
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        return page.markBits[slotIndex ushr 6] and bit != 0L
    }

    private fun countAllocatedSlotWords(page: Page): Long {
        var allocatedSlots = 0L
        val bitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
        var index = 0
        while (index < bitmapWords) {
            allocatedSlots += page.allocationBits[index].countOneBits()
            index++
        }
        return allocatedSlots * page.slotWords
    }

    private fun bitmapWords(
        slotWords: Int,
        pageWords: Int,
    ): Int {
        val slotCount = pageWords / slotWords
        return (slotCount + Long.SIZE_BITS - 1) / Long.SIZE_BITS
    }

    private fun isAllocatedReference(rawReference: Long): Boolean {
        val referenceTag = rawReference and RV_TYPE_MASK
        val expectedDescriptorKind = when (referenceTag) {
            RV_TYPE_STRUCT -> STRUCT_DESCRIPTOR_KIND
            RV_TYPE_ARRAY -> ARRAY_DESCRIPTOR_KIND
            RV_TYPE_EXCEPTION -> EXCEPTION_DESCRIPTOR_KIND
            else -> return false
        }
        val logicalAddress = rawReference shr RV_SHIFT_BITS
        if (logicalAddress <= 0L || logicalAddress > Int.MAX_VALUE) return false

        val address = logicalAddress.toInt()
        if (isDedicatedAddress(address)) {
            if (expectedDescriptorKind != ARRAY_DESCRIPTOR_KIND) return false
            val dedicatedId = dedicatedId(address)
            if (dedicatedId == 0 || dedicatedId >= dedicatedIdTop) return false
            val words = dedicatedPayloads.getOrNull(dedicatedId) ?: return false
            val descriptorKey = words[0].toInt()
            if (descriptorKind(descriptorKey) != ARRAY_DESCRIPTOR_KIND) return false
            val semanticId = descriptorSemanticId(descriptorKey)
            return aggregateDescriptors.getOrNull(semanticId) is ArrayDescriptor
        }
        val pageId = address ushr PAGE_SHIFT
        if (pageId == NO_PAGE_ID || pageId >= pageIdTop) return false
        val page = pages[pageId] ?: return false
        if (page.descriptorKey == 0 || descriptorKind(page.descriptorKey) != expectedDescriptorKind) return false

        val wordOffset = address and PAGE_MASK
        val slotIndex = wordOffset / page.slotWords
        if (slotIndex * page.slotWords != wordOffset) return false
        val slotCount = PAGE_WORDS / page.slotWords
        if (slotIndex >= slotCount) return false
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        return page.allocationBits[slotIndex ushr 6] and bit != 0L
    }

    internal fun descriptorKeyForTesting(
        semanticId: Int,
        descriptorKind: Int,
    ): Int {
        validateSemanticId(semanticId)
        require(descriptorKind in STRUCT_DESCRIPTOR_KIND..EXCEPTION_DESCRIPTOR_KIND)
        return descriptorKey(semanticId, descriptorKind)
    }

    internal fun commitFixedPageForTesting(descriptorKey: Int): Int {
        return commitFixedPage(descriptorKey)
    }

    internal fun recycleEmptyPageForTesting(pageId: Int) {
        recycleEmptyPage(pageId)
    }

    internal fun slotCountForTesting(pageId: Int): Int {
        val page = requireNotNull(pages.getOrNull(pageId))
        return PAGE_WORDS / page.slotWords
    }

    internal fun setSlotAllocatedForTesting(
        pageId: Int,
        slotIndex: Int,
    ) {
        val page = requireNotNull(pages.getOrNull(pageId))
        require(page.descriptorKey != 0)
        val slotCount = PAGE_WORDS / page.slotWords
        require(slotIndex in 0 until slotCount)
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        val bitmapIndex = slotIndex ushr 6
        require(page.allocationBits[bitmapIndex] and bit == 0L)
        page.allocationBits[bitmapIndex] = page.allocationBits[bitmapIndex] or bit
        allocatedSlotWords += page.slotWords
        page.bumpSlot = maxOf(page.bumpSlot, slotIndex + 1)
        if (page.bumpSlot == slotCount) rebuildAvailabilityChains()
    }

    internal fun clearSlotAllocatedForTesting(
        pageId: Int,
        slotIndex: Int,
    ) {
        val page = requireNotNull(pages.getOrNull(pageId))
        val slotCount = PAGE_WORDS / page.slotWords
        require(slotIndex in 0 until slotCount)
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        val bitmapIndex = slotIndex ushr 6
        require(page.allocationBits[bitmapIndex] and bit != 0L)
        page.allocationBits[bitmapIndex] = page.allocationBits[bitmapIndex] and bit.inv()
        allocatedSlotWords -= page.slotWords
    }

    internal fun releaseStructForTesting(rawReference: Long) {
        require(
            rawReference and RV_TYPE_MASK == RV_TYPE_STRUCT &&
                isAllocatedReference(rawReference),
        )
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        val pageId = address ushr PAGE_SHIFT
        val page = requireNotNull(pages[pageId])
        val wordOffset = address and PAGE_MASK
        val slotIndex = wordOffset / page.slotWords
        val bitmapIndex = slotIndex ushr 6
        val allocationBit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        val wasAvailable = pageHasAvailableSlot(page)

        page.allocationBits[bitmapIndex] = page.allocationBits[bitmapIndex] and allocationBit.inv()
        allocatedSlotWords -= page.slotWords
        payloadWords[pagePayloadWordOffset(pageId) + wordOffset] = page.freeHead.toLong()
        page.freeHead = slotIndex
        if (!wasAvailable) {
            val descriptor = checkNotNull(fixedDescriptor(page.descriptorKey))
            linkAvailablePage(descriptor, page, pageId)
        }
    }

    internal fun releaseArrayForTesting(rawReference: Long) {
        require(
            rawReference and RV_TYPE_MASK == RV_TYPE_ARRAY &&
                isAllocatedReference(rawReference),
        )
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        if (isDedicatedAddress(address)) {
            val dedicatedId = dedicatedId(address)
            var activeIndex = 0
            while (dedicatedIds[activeIndex] != dedicatedId) {
                activeIndex++
                check(activeIndex < dedicatedActiveCount)
            }
            val words = checkNotNull(dedicatedPayloads[dedicatedId])
            val lastActiveIndex = dedicatedActiveCount - 1
            dedicatedIds[activeIndex] = dedicatedIds[lastActiveIndex]
            dedicatedIds[lastActiveIndex] = dedicatedId
            dedicatedActiveCount = lastActiveIndex
            dedicatedPayloads[dedicatedId] = null
            dedicatedPayloadWords -= words.size
            allocatedSlotWords -= words.size
            return
        }

        val pageId = address ushr PAGE_SHIFT
        val page = checkNotNull(pages[pageId])
        val wordOffset = address and PAGE_MASK
        val slotIndex = wordOffset / page.slotWords
        val bitmapIndex = slotIndex ushr 6
        val allocationBit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        val wasAvailable = pageHasAvailableSlot(page)
        page.allocationBits[bitmapIndex] = page.allocationBits[bitmapIndex] and allocationBit.inv()
        allocatedSlotWords -= page.slotWords
        payloadWords[pagePayloadWordOffset(pageId) + wordOffset] = page.freeHead.toLong()
        page.freeHead = slotIndex
        if (!wasAvailable) {
            val descriptor = checkNotNull(arrayDescriptor(page.descriptorKey))
            val availablePageHeads = checkNotNull(descriptor.availablePageHeads)
            val classIndex = arrayClassIndexForSlotWords(page.slotWords)
            linkAvailableArrayPage(availablePageHeads, classIndex, page, pageId)
        }
    }

    internal fun arraySlotWordsForTesting(rawReference: Long): Int {
        require(
            rawReference and RV_TYPE_MASK == RV_TYPE_ARRAY &&
                isAllocatedReference(rawReference),
        )
        val address = (rawReference ushr RV_SHIFT_BITS).toInt()
        return if (isDedicatedAddress(address)) {
            checkNotNull(dedicatedPayloads[dedicatedId(address)]).size
        } else {
            checkNotNull(pages[address ushr PAGE_SHIFT]).slotWords
        }
    }

    internal fun isDedicatedArrayForTesting(rawReference: Long): Boolean {
        require(rawReference and RV_TYPE_MASK == RV_TYPE_ARRAY)
        return isDedicatedAddress((rawReference ushr RV_SHIFT_BITS).toInt())
    }

    internal fun resetStructAllocationsForTesting(descriptorKey: Int) {
        require(descriptorKind(descriptorKey) == STRUCT_DESCRIPTOR_KIND)
        val descriptor = requireNotNull(fixedDescriptor(descriptorKey))
        descriptor.availablePageHead = NO_PAGE_ID
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val pageId = activePageIds[activeIndex]
            val page = checkNotNull(pages[pageId])
            if (page.descriptorKey == descriptorKey) {
                allocatedSlotWords -= countAllocatedSlotWords(page)
                page.allocationBits.fill(0L)
                page.markBits.fill(0L)
                page.bumpSlot = 0
                page.freeHead = NO_SLOT
                page.nextAvailablePageId = NO_PAGE_ID
                linkAvailablePage(descriptor, page, pageId)
            }
            activeIndex++
        }
    }

    internal fun rawReferenceForTesting(
        pageId: Int,
        slotIndex: Int,
    ): Long {
        val page = requireNotNull(pages.getOrNull(pageId))
        val slotCount = PAGE_WORDS / page.slotWords
        require(slotIndex in 0 until slotCount)
        val wordOffset = slotIndex * page.slotWords
        require(wordOffset in 0..PAGE_MASK)
        val address = (pageId shl PAGE_SHIFT) or wordOffset
        val tag = when (descriptorKind(page.descriptorKey)) {
            STRUCT_DESCRIPTOR_KIND -> RV_TYPE_STRUCT
            ARRAY_DESCRIPTOR_KIND -> RV_TYPE_ARRAY
            EXCEPTION_DESCRIPTOR_KIND -> RV_TYPE_EXCEPTION
            else -> error("page is not active")
        }
        return (address.toLong() shl RV_SHIFT_BITS) or tag
    }

    internal fun isAllocatedReferenceForTesting(rawReference: Long): Boolean =
        isAllocatedReference(rawReference)

    internal fun checkInvariants() {
        check(markWorklistSize in 0..markWorklist.size)
        check(markWorklistPeakSize in markWorklistSize..markWorklist.size)
        var worklistIndex = 0
        while (worklistIndex < markWorklistSize) {
            check(isMarkedAddressForInvariants(markWorklist[worklistIndex]))
            worklistIndex++
        }
        check(pages[0] == null)
        check(pageIdTop in 1..pages.size)
        check(payloadWords.size and PAGE_MASK == 0)
        check(payloadWords.size.toLong() <= maximumPageCount.toLong() * PAGE_WORDS)
        check((pageIdTop - 1).toLong() * PAGE_WORDS <= payloadWords.size)
        check(payloadWords.size + dedicatedPayloadWords <= maximumPageCount.toLong() * PAGE_WORDS)
        check(activePageCount in 0..activePageIds.size)
        check(recycledPageCount in 0..recycledPageIds.size)
        check(dedicatedPayloads[0] == null)
        check(dedicatedIdTop in 1..DEDICATED_ID_MASK + 1)
        check(dedicatedIdTop - 1 <= dedicatedIds.size)
        check(dedicatedActiveCount in 0 until dedicatedIdTop)

        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val pageId = activePageIds[activeIndex]
            check(pageId in 1 until pageIdTop)
            val page = checkNotNull(pages[pageId])
            check(page.descriptorKey != 0)
            check(descriptorExists(page.descriptorKey))
            check(page.slotWords in 1..PAGE_WORDS)
            val slotCount = PAGE_WORDS / page.slotWords
            check(page.bumpSlot in 0..slotCount)
            check(page.freeHead == NO_SLOT || page.freeHead in 0 until slotCount)
            val activeBitmapWords = bitmapWords(page.slotWords, PAGE_WORDS)
            check(page.allocationBits.size >= activeBitmapWords)
            check(page.markBits.size >= activeBitmapWords)
            check(!hasSetBitsAtOrAbove(page.allocationBits, activeBitmapWords, slotCount))
            check(!hasSetBitsAtOrAbove(page.markBits, activeBitmapWords, slotCount))
            checkMarksAreAllocated(page, activeBitmapWords)
            check(!hasAllocatedSlotAtOrAboveBump(page))
            checkFreeList(pageId, page)
            checkArrayPageLengths(pageId, page)
            val availabilityOccurrences = availabilityOccurrences(pageId, page)
            check(availabilityOccurrences == if (pageHasAvailableSlot(page)) 1 else 0)

            var duplicateIndex = activeIndex + 1
            while (duplicateIndex < activePageCount) {
                check(activePageIds[duplicateIndex] != pageId)
                duplicateIndex++
            }
            activeIndex++
        }

        var recycledIndex = 0
        while (recycledIndex < recycledPageCount) {
            val pageId = recycledPageIds[recycledIndex]
            check(pageId in 1 until pageIdTop)
            val page = checkNotNull(pages[pageId])
            check(page.descriptorKey == 0)
            check(page.slotWords in 1..PAGE_WORDS)
            check(activePageIndex(pageId) == NO_SLOT)
            check(!hasAnySetBits(page.allocationBits))
            check(!hasAnySetBits(page.markBits))
            check(page.bumpSlot == 0)
            check(page.freeHead == NO_SLOT)
            check(page.nextAvailablePageId == NO_PAGE_ID)

            var duplicateIndex = recycledIndex + 1
            while (duplicateIndex < recycledPageCount) {
                check(recycledPageIds[duplicateIndex] != pageId)
                duplicateIndex++
            }
            recycledIndex++
        }

        var pageId = 1
        while (pageId < pageIdTop) {
            val page = checkNotNull(pages[pageId])
            val isActive = activePageIndex(pageId) != NO_SLOT
            val isRecycled = recycledPageIndex(pageId) != NO_SLOT
            check(isActive != isRecycled)
            check(isActive == (page.descriptorKey != 0))
            pageId++
        }

        var derivedAllocatedSlotWords = 0L
        activeIndex = 0
        while (activeIndex < activePageCount) {
            derivedAllocatedSlotWords += countAllocatedSlotWords(
                checkNotNull(pages[activePageIds[activeIndex]]),
            )
            activeIndex++
        }
        var derivedDedicatedPayloadWords = 0L
        val seenDedicatedIds = BooleanArray(dedicatedIdTop)
        var dedicatedIndex = 0
        while (dedicatedIndex < dedicatedIdTop - 1) {
            val dedicatedId = dedicatedIds[dedicatedIndex]
            check(dedicatedId in 1 until dedicatedIdTop)
            check(!seenDedicatedIds[dedicatedId])
            seenDedicatedIds[dedicatedId] = true

            val words = dedicatedPayloads[dedicatedId]
            if (dedicatedIndex < dedicatedActiveCount) {
                checkNotNull(words)
                check(words.size > ARRAY_DEDICATED_CUTOFF)
                val metadata = words[0]
                check(metadata ushr (Int.SIZE_BITS + 1) == 0L)
                check(arrayDescriptor(metadata.toInt()) != null)
                derivedDedicatedPayloadWords += words.size
                derivedAllocatedSlotWords += words.size
            } else {
                check(words == null)
            }
            dedicatedIndex++
        }
        var dedicatedId = 1
        while (dedicatedId < dedicatedIdTop) {
            check(seenDedicatedIds[dedicatedId])
            dedicatedId++
        }
        check(dedicatedPayloadWords == derivedDedicatedPayloadWords)
        check(allocatedSlotWords == derivedAllocatedSlotWords)

        checkAvailabilityChains()
    }

    private fun recycledPageIndex(pageId: Int): Int {
        var index = 0
        while (index < recycledPageCount) {
            if (recycledPageIds[index] == pageId) return index
            index++
        }
        return NO_SLOT
    }

    private fun hasSetBitsAtOrAbove(
        bitmap: LongArray,
        activeBitmapWords: Int,
        bitCount: Int,
    ): Boolean {
        if (activeBitmapWords == 0) return false
        val usedBitsInLastWord = bitCount and (Long.SIZE_BITS - 1)
        if (usedBitsInLastWord != 0) {
            val unusedMask = (-1L) shl usedBitsInLastWord
            if (bitmap[activeBitmapWords - 1] and unusedMask != 0L) return true
        }
        var index = activeBitmapWords
        while (index < bitmap.size) {
            if (bitmap[index] != 0L) return true
            index++
        }
        return false
    }

    private fun hasAllocatedSlotAtOrAboveBump(page: Page): Boolean {
        val slotCount = PAGE_WORDS / page.slotWords
        var slotIndex = page.bumpSlot
        while (slotIndex < slotCount) {
            val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
            if (page.allocationBits[slotIndex ushr 6] and bit != 0L) return true
            slotIndex++
        }
        return false
    }

    private fun checkFreeList(
        pageId: Int,
        page: Page,
    ) {
        val pagePayloadWordOffset = pagePayloadWordOffset(pageId)
        var slotIndex = page.freeHead
        var visited = 0
        while (slotIndex != NO_SLOT) {
            check(slotIndex in 0 until page.bumpSlot)
            check(!slotIsAllocated(page, slotIndex))
            slotIndex = payloadWords[pagePayloadWordOffset + slotIndex * page.slotWords].toInt()
            visited++
            check(visited <= page.bumpSlot)
        }

        slotIndex = 0
        while (slotIndex < page.bumpSlot) {
            val expectedOccurrences = if (slotIsAllocated(page, slotIndex)) 0 else 1
            check(freeListOccurrences(pagePayloadWordOffset, page, slotIndex) == expectedOccurrences)
            slotIndex++
        }
    }

    private fun checkArrayPageLengths(
        pageId: Int,
        page: Page,
    ) {
        if (descriptorKind(page.descriptorKey) != ARRAY_DESCRIPTOR_KIND) return
        val pageBase = pagePayloadWordOffset(pageId)
        val slotCount = PAGE_WORDS / page.slotWords
        val expectedClassIndex = arrayClassIndexForSlotWords(page.slotWords)
        var slotIndex = 0
        while (slotIndex < slotCount) {
            if (slotIsAllocated(page, slotIndex)) {
                val length = payloadWords[pageBase + slotIndex * page.slotWords].toInt()
                check(length in 0 until ARRAY_DEDICATED_CUTOFF)
                val layout = ARRAY_LAYOUT_BY_LENGTH[length]
                check(layout and ARRAY_CLASS_INDEX_MASK == expectedClassIndex)
                check(layout ushr ARRAY_CLASS_INDEX_BITS == page.slotWords)
            }
            slotIndex++
        }
    }

    private fun freeListOccurrences(
        pagePayloadWordOffset: Int,
        page: Page,
        expectedSlotIndex: Int,
    ): Int {
        var slotIndex = page.freeHead
        var occurrences = 0
        var visited = 0
        while (slotIndex != NO_SLOT) {
            check(slotIndex in 0 until page.bumpSlot)
            if (slotIndex == expectedSlotIndex) occurrences++
            slotIndex = payloadWords[pagePayloadWordOffset + slotIndex * page.slotWords].toInt()
            visited++
            check(visited <= page.bumpSlot)
        }
        return occurrences
    }

    private fun slotIsAllocated(
        page: Page,
        slotIndex: Int,
    ): Boolean {
        val bit = 1L shl (slotIndex and (Long.SIZE_BITS - 1))
        return page.allocationBits[slotIndex ushr 6] and bit != 0L
    }

    private fun hasAnySetBits(bitmap: LongArray): Boolean {
        var index = 0
        while (index < bitmap.size) {
            if (bitmap[index] != 0L) return true
            index++
        }
        return false
    }

    private fun checkAvailabilityChains() {
        var semanticId = 0
        while (semanticId < aggregateDescriptors.size) {
            val descriptor = aggregateDescriptors[semanticId]
            if (descriptor is FixedDescriptor) {
                checkAvailabilityChain(descriptor, descriptorKey(semanticId, STRUCT_DESCRIPTOR_KIND))
            } else if (descriptor is ArrayDescriptor) {
                checkArrayAvailabilityChains(
                    descriptor,
                    descriptorKey(semanticId, ARRAY_DESCRIPTOR_KIND),
                )
            }
            semanticId++
        }
        semanticId = 0
        while (semanticId < exceptionDescriptors.size) {
            val descriptor = exceptionDescriptors[semanticId]
            if (descriptor != null) {
                checkAvailabilityChain(descriptor, descriptorKey(semanticId, EXCEPTION_DESCRIPTOR_KIND))
            }
            semanticId++
        }
    }

    private fun availabilityOccurrences(
        expectedPageId: Int,
        page: Page,
    ): Int {
        val descriptor = if (descriptorKind(page.descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
            checkNotNull(arrayDescriptor(page.descriptorKey))
        } else {
            checkNotNull(fixedDescriptor(page.descriptorKey))
        }
        var pageId = when (descriptor) {
            is FixedDescriptor -> descriptor.availablePageHead
            is ArrayDescriptor -> {
                val classIndex = arrayClassIndexForSlotWords(page.slotWords)
                checkNotNull(descriptor.availablePageHeads)[classIndex]
            }
        }
        var occurrences = 0
        var visited = 0
        while (pageId != NO_PAGE_ID) {
            check(pageId in 1 until pageIdTop)
            if (pageId == expectedPageId) occurrences++
            pageId = checkNotNull(pages[pageId]).nextAvailablePageId
            visited++
            check(visited <= activePageCount)
        }
        return occurrences
    }

    private fun checkAvailabilityChain(
        descriptor: FixedDescriptor,
        expectedDescriptorKey: Int,
    ) {
        var pageId = descriptor.availablePageHead
        var visited = 0
        while (pageId != NO_PAGE_ID) {
            check(pageId in 1 until pageIdTop)
            val page = checkNotNull(pages[pageId])
            check(page.descriptorKey == expectedDescriptorKey)
            check(pageHasAvailableSlot(page))
            pageId = page.nextAvailablePageId
            visited++
            check(visited <= activePageCount)
        }
    }

    private fun checkArrayAvailabilityChains(
        descriptor: ArrayDescriptor,
        expectedDescriptorKey: Int,
    ) {
        val availablePageHeads = descriptor.availablePageHeads ?: return
        check(availablePageHeads.size == ARRAY_CLASS_COUNT)
        var classIndex = 0
        while (classIndex < availablePageHeads.size) {
            var pageId = availablePageHeads[classIndex]
            var visited = 0
            while (pageId != NO_PAGE_ID) {
                check(pageId in 1 until pageIdTop)
                val page = checkNotNull(pages[pageId])
                check(page.descriptorKey == expectedDescriptorKey)
                check(arrayClassIndexForSlotWords(page.slotWords) == classIndex)
                check(pageHasAvailableSlot(page))
                pageId = page.nextAvailablePageId
                visited++
                check(visited <= activePageCount)
            }
            classIndex++
        }
    }

    private fun rebuildAvailabilityChains() {
        var activeIndex = 0
        while (activeIndex < activePageCount) {
            val pageId = activePageIds[activeIndex]
            val page = checkNotNull(pages[pageId])
            clearAvailabilityHead(page.descriptorKey, page.slotWords)
            page.nextAvailablePageId = NO_PAGE_ID
            activeIndex++
        }

        activeIndex = 0
        while (activeIndex < activePageCount) {
            val pageId = activePageIds[activeIndex]
            val page = checkNotNull(pages[pageId])
            if (pageHasAvailableSlot(page)) {
                if (descriptorKind(page.descriptorKey) == ARRAY_DESCRIPTOR_KIND) {
                    val descriptor = checkNotNull(arrayDescriptor(page.descriptorKey))
                    val availablePageHeads = checkNotNull(descriptor.availablePageHeads)
                    val classIndex = arrayClassIndexForSlotWords(page.slotWords)
                    linkAvailableArrayPage(availablePageHeads, classIndex, page, pageId)
                } else {
                    val descriptor = checkNotNull(fixedDescriptor(page.descriptorKey))
                    linkAvailablePage(descriptor, page, pageId)
                }
            }
            activeIndex++
        }
    }

    private fun pageHasAvailableSlot(page: Page): Boolean =
        page.bumpSlot < PAGE_WORDS / page.slotWords || page.freeHead != NO_SLOT

    private fun arrayClassIndexForSlotWords(slotWords: Int): Int {
        val capacity = slotWords - 1
        check(capacity in 0 until ARRAY_DEDICATED_CUTOFF)
        val layout = ARRAY_LAYOUT_BY_LENGTH[capacity]
        check(layout ushr ARRAY_CLASS_INDEX_BITS == slotWords)
        return layout and ARRAY_CLASS_INDEX_MASK
    }

    private fun pagePayloadWordOffset(pageId: Int): Int = (pageId - 1) shl PAGE_SHIFT

    private sealed interface AggregateDescriptor

    private class FixedDescriptor(
        val payloadWords: Int,
        val referenceFieldIndices: IntArray,
        var availablePageHead: Int = NO_PAGE_ID,
    ) : AggregateDescriptor {
        fun hasLayout(
            payloadWords: Int,
            referenceFieldIndices: IntArray,
        ): Boolean =
            this.payloadWords == payloadWords &&
                this.referenceFieldIndices.contentEquals(referenceFieldIndices)
    }

    private class ArrayDescriptor(
        val elementsMayContainReferences: Boolean,
        var availablePageHeads: IntArray? = null,
    ) : AggregateDescriptor

    private class Page(
        var allocationBits: LongArray,
        var markBits: LongArray,
        var descriptorKey: Int,
        var slotWords: Int,
        var bumpSlot: Int = 0,
        var freeHead: Int = NO_SLOT,
        var nextAvailablePageId: Int = NO_PAGE_ID,
    ) {
        fun retask(
            descriptorKey: Int,
            slotWords: Int,
        ) {
            check(this.descriptorKey == 0)
            val bitmapWords = (PAGE_WORDS / slotWords + Long.SIZE_BITS - 1) / Long.SIZE_BITS
            val newAllocationBits =
                if (allocationBits.size >= bitmapWords) allocationBits else LongArray(bitmapWords)
            val newMarkBits = if (markBits.size >= bitmapWords) markBits else LongArray(bitmapWords)

            allocationBits = newAllocationBits
            markBits = newMarkBits
            this.descriptorKey = descriptorKey
            this.slotWords = slotWords
            bumpSlot = 0
            freeHead = NO_SLOT
            nextAvailablePageId = NO_PAGE_ID
        }

        fun recycle() {
            descriptorKey = 0
            bumpSlot = 0
            freeHead = NO_SLOT
            nextAvailablePageId = NO_PAGE_ID
        }
    }
}
