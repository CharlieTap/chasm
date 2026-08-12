package io.github.charlietap.chasm.runtime.type

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import kotlin.jvm.JvmInline

private const val STANDALONE_GROUP = -1

@JvmInline
value class RTT(val value: Int)

@JvmInline
value class ReferenceTypeTest private constructor(
    private val encoding: Int,
) {
    val nullable: Boolean
        get() = encoding and NULLABLE_MASK != 0

    val isDefined: Boolean
        get() = encoding and DEFINED_MASK != 0

    val rtt: RTT
        get() = RTT(encoding and PAYLOAD_MASK)

    val acceptedReferenceTags: Int
        get() = encoding and PAYLOAD_MASK

    companion object {
        fun from(type: ReferenceType, runtimeTypes: RuntimeTypeMap): ReferenceTypeTest {
            val nullable = type is ReferenceType.RefNull
            val payload = when (val heapType = type.heapType) {
                AbstractHeapType.Func -> FUNCTION_TAG
                AbstractHeapType.NoFunc -> NO_TAGS
                AbstractHeapType.Extern -> EXTERN_TAG
                AbstractHeapType.NoExtern -> NO_TAGS
                AbstractHeapType.Exception -> EXCEPTION_TAG
                AbstractHeapType.NoException -> NO_TAGS
                AbstractHeapType.Any -> I31_TAG or STRUCT_TAG or ARRAY_TAG or HOST_TAG
                AbstractHeapType.Eq -> I31_TAG or STRUCT_TAG or ARRAY_TAG
                AbstractHeapType.Struct -> STRUCT_TAG
                AbstractHeapType.Array -> ARRAY_TAG
                AbstractHeapType.I31 -> I31_TAG
                AbstractHeapType.None -> NO_TAGS
                is AbstractHeapType.Bottom -> NO_TAGS
                is ConcreteHeapType.TypeIndex -> DEFINED_MASK or runtimeTypes[heapType.index].value
                is ConcreteHeapType.Defined -> DEFINED_MASK or runtimeTypes[heapType.definedType.typeIndex].value
                is ConcreteHeapType.RecursiveTypeIndex -> error("instruction type is not closed")
            }
            return ReferenceTypeTest(payload or if (nullable) NULLABLE_MASK else 0)
        }

        private const val NULLABLE_MASK = Int.MIN_VALUE
        private const val DEFINED_MASK = 1 shl 30
        private const val PAYLOAD_MASK = DEFINED_MASK - 1

        // Bit positions mirror the compact reference tags used by runtime-core.
        private const val NO_TAGS = 0
        private const val I31_TAG = 1 shl 2
        private const val STRUCT_TAG = 1 shl 3
        private const val ARRAY_TAG = 1 shl 4
        private const val FUNCTION_TAG = 1 shl 5
        private const val HOST_TAG = 1 shl 6
        private const val EXCEPTION_TAG = 1 shl 7
        private const val EXTERN_TAG = 1 shl 8
    }
}

class RuntimeTypeMap internal constructor(
    private val ids: IntArray,
) {
    val size: Int
        get() = ids.size

    operator fun get(index: Int): RTT = RTT(ids[index])

    companion object {
        val Empty = RuntimeTypeMap(IntArray(0))
    }
}

class RuntimeTypeRegistry {
    private val groups = mutableMapOf<RecGroupLookup, IntArray>()
    private val displays = mutableListOf<IntArray>()

    fun register(types: List<DefinedType>): RuntimeTypeMap {
        val runtimeTypes = IntArray(types.size)
        val keyEncoder = RecGroupKeyEncoder(this)
        var groupStart = 0

        while (groupStart < types.size) {
            val group = types[groupStart].recursiveType
            val groupSize = group.subTypes.size
            check(groupSize > 0 && groupStart + groupSize <= types.size)
            repeat(groupSize) { offset ->
                val type = types[groupStart + offset]
                check(type.recursiveType === group)
                check(type.recursiveTypeIndex == offset)
                check(type.typeIndex == groupStart + offset)
            }

            val key = keyEncoder.reset(
                groupStart = groupStart,
                group = group,
                runtimeTypes = runtimeTypes,
            )
            val groupTypes = groups[key] ?: registerGroup(group, key.storedKey(), groupStart, runtimeTypes)
            groupTypes.copyInto(runtimeTypes, destinationOffset = groupStart)
            groupStart += groupSize
        }

        return RuntimeTypeMap(runtimeTypes)
    }

    fun register(type: DefinedType): RTT =
        RTT(registerStandaloneGroup(type.recursiveType)[type.recursiveTypeIndex])

    fun matches(actual: RTT, expected: RTT): Boolean {
        if (actual == expected) return true
        val actualDisplay = displays.getOrNull(actual.value) ?: return false
        val expectedDepth = displays.getOrNull(expected.value)?.size ?: return false
        return expectedDepth < actualDisplay.size && actualDisplay[expectedDepth] == expected.value
    }

    private fun registerGroup(
        group: RecursiveType,
        key: StoredRecGroupKey,
        groupStart: Int,
        runtimeTypes: IntArray?,
    ): IntArray {
        val firstId = displays.size
        check(firstId.toLong() + group.subTypes.lastIndex <= MAX_RUNTIME_TYPE_ID)
        val ids = IntArray(group.subTypes.size) { offset -> firstId + offset }
        val groupDisplays = ArrayList<IntArray>(group.subTypes.size)

        for (index in group.subTypes.indices) {
            val subType = group.subTypes[index]
            check(subType.superTypes.size <= 1)
            val parent = subType.superTypes.firstOrNull()?.let { type ->
                if (groupStart == STANDALONE_GROUP) {
                    resolveStandaloneType(type, group, ids)
                } else {
                    resolveModuleType(type, groupStart, group, checkNotNull(runtimeTypes), ids)
                }
            }
            val display = if (parent == null) {
                EMPTY_DISPLAY
            } else {
                check(parent < firstId + groupDisplays.size) { "supertype must precede its subtype" }
                val parentDisplay = if (parent < firstId) {
                    displays[parent]
                } else {
                    groupDisplays[parent - firstId]
                }
                parentDisplay + parent
            }
            groupDisplays += display
        }

        displays += groupDisplays
        groups[key] = ids
        return ids
    }

    private fun registerStandaloneGroup(group: RecursiveType): IntArray {
        val key = RecGroupKeyEncoder(this).reset(
            groupStart = STANDALONE_GROUP,
            group = group,
            runtimeTypes = null,
        )
        return groups[key] ?: registerGroup(group, key.storedKey(), STANDALONE_GROUP, null)
    }

    private fun resolveModuleType(
        type: HeapType,
        groupStart: Int,
        group: RecursiveType,
        runtimeTypes: IntArray,
        groupTypes: IntArray,
    ): Int = when (type) {
        is ConcreteHeapType.RecursiveTypeIndex -> groupTypes[type.index]
        is ConcreteHeapType.TypeIndex -> when (type.index) {
            in groupStart until groupStart + groupTypes.size -> groupTypes[type.index - groupStart]
            else -> runtimeTypes[type.index]
        }
        is ConcreteHeapType.Defined -> when {
            type.definedType.recursiveType === group -> groupTypes[type.definedType.recursiveTypeIndex]
            else -> runtimeTypes[type.definedType.typeIndex]
        }
        is AbstractHeapType -> error("defined type has an abstract supertype")
    }

    private fun resolveStandaloneType(
        type: HeapType,
        group: RecursiveType,
        groupTypes: IntArray,
    ): Int = when (type) {
        is ConcreteHeapType.RecursiveTypeIndex -> groupTypes[type.index]
        is ConcreteHeapType.Defined -> if (type.definedType.recursiveType === group) {
            groupTypes[type.definedType.recursiveTypeIndex]
        } else {
            register(type.definedType).value
        }
        is ConcreteHeapType.TypeIndex -> error("standalone type contains an unresolved type index: ${type.index}")
        is AbstractHeapType -> error("defined type has an abstract supertype")
    }

    private companion object {
        const val MAX_RUNTIME_TYPE_ID = (1 shl 30) - 1
        val EMPTY_DISPLAY = IntArray(0)
    }
}

internal sealed interface RecGroupLookup

internal class StoredRecGroupKey(
    val words: IntArray,
) : RecGroupLookup {
    private val hashCode = words.contentHashCode()

    override fun equals(other: Any?): Boolean =
        this === other || when (other) {
            is StoredRecGroupKey -> words.contentEquals(other.words)
            is RecGroupKeyEncoder -> other.matches(words)
            else -> false
        }

    override fun hashCode(): Int = hashCode
}

private class RecGroupKeyEncoder(
    private val registry: RuntimeTypeRegistry,
) : RecGroupLookup {
    private var groupStart = STANDALONE_GROUP
    private lateinit var group: RecursiveType
    private var runtimeTypes: IntArray? = null
    private var mode = HASH
    private var hash = 1
    private var expected = EMPTY_WORDS
    private var cursor = 0
    private var matched = true
    private var output: IntArrayBuilder? = null
    private var hashCode = 0

    override fun equals(other: Any?): Boolean =
        this === other || other is StoredRecGroupKey && matches(other.words)

    override fun hashCode(): Int = hashCode

    fun reset(
        groupStart: Int,
        group: RecursiveType,
        runtimeTypes: IntArray?,
    ): RecGroupKeyEncoder {
        this.groupStart = groupStart
        this.group = group
        this.runtimeTypes = runtimeTypes
        hashCode = calculateHash()
        return this
    }

    fun storedKey(): StoredRecGroupKey {
        mode = BUILD
        output = IntArrayBuilder()
        encode()
        return StoredRecGroupKey(checkNotNull(output).toArray())
    }

    fun matches(words: IntArray): Boolean {
        mode = MATCH
        expected = words
        cursor = 0
        matched = true
        encode()
        return matched && cursor == words.size
    }

    private fun calculateHash(): Int {
        mode = HASH
        hash = 1
        encode()
        return hash
    }

    private fun encode() {
        add(Tag.GROUP, group.subTypes.size)
        for (index in group.subTypes.indices) subType(group.subTypes[index])
    }

    private fun subType(type: SubType) {
        add(Tag.SUBTYPE, if (type is SubType.Final) Tag.FINAL else Tag.OPEN)
        add(type.superTypes.size)
        for (index in type.superTypes.indices) heapType(type.superTypes[index])
        compositeType(type.compositeType)
    }

    private fun compositeType(type: CompositeType) {
        when (type) {
            is CompositeType.Function -> functionType(type.functionType)
            is CompositeType.Struct -> structType(type.structType)
            is CompositeType.Array -> arrayType(type.arrayType)
        }
    }

    private fun functionType(type: FunctionType) {
        add(Tag.FUNCTION)
        resultType(type.params)
        resultType(type.results)
    }

    private fun structType(type: StructType) {
        add(Tag.STRUCT, type.fields.size)
        for (index in type.fields.indices) fieldType(type.fields[index])
    }

    private fun arrayType(type: ArrayType) {
        add(Tag.ARRAY)
        fieldType(type.fieldType)
    }

    private fun resultType(type: ResultType) {
        add(type.types.size)
        for (index in type.types.indices) valueType(type.types[index])
    }

    private fun fieldType(type: FieldType) {
        storageType(type.storageType)
        add(type.mutability.ordinal)
    }

    private fun storageType(type: StorageType) {
        when (type) {
            is StorageType.Value -> {
                add(Tag.VALUE)
                valueType(type.type)
            }
            is StorageType.Packed -> add(
                when (type.type) {
                    PackedType.I8 -> Tag.I8
                    PackedType.I16 -> Tag.I16
                },
            )
        }
    }

    private fun valueType(type: ValueType) {
        when (type) {
            is ValueType.Number -> add(
                when (type.numberType) {
                    NumberType.I32 -> Tag.I32
                    NumberType.I64 -> Tag.I64
                    NumberType.F32 -> Tag.F32
                    NumberType.F64 -> Tag.F64
                },
            )
            is ValueType.Vector -> add(Tag.V128)
            is ValueType.Reference -> referenceType(type.referenceType)
            is ValueType.Bottom -> add(Tag.BOTTOM)
        }
    }

    private fun referenceType(type: ReferenceType) {
        add(if (type is ReferenceType.Ref) Tag.REF else Tag.REF_NULL)
        heapType(type.heapType)
    }

    private fun heapType(type: HeapType) {
        when (type) {
            AbstractHeapType.Func -> add(Tag.FUNC)
            AbstractHeapType.NoFunc -> add(Tag.NO_FUNC)
            AbstractHeapType.Extern -> add(Tag.EXTERN)
            AbstractHeapType.NoExtern -> add(Tag.NO_EXTERN)
            AbstractHeapType.Exception -> add(Tag.EXCEPTION)
            AbstractHeapType.NoException -> add(Tag.NO_EXCEPTION)
            AbstractHeapType.Any -> add(Tag.ANY)
            AbstractHeapType.Eq -> add(Tag.EQ)
            AbstractHeapType.Struct -> add(Tag.STRUCT_HEAP)
            AbstractHeapType.Array -> add(Tag.ARRAY_HEAP)
            AbstractHeapType.I31 -> add(Tag.I31)
            AbstractHeapType.None -> add(Tag.NONE)
            is AbstractHeapType.Bottom -> add(Tag.BOTTOM)
            is ConcreteHeapType.RecursiveTypeIndex -> add(Tag.INTERNAL, type.index)
            is ConcreteHeapType.TypeIndex -> typeIndex(type.index)
            is ConcreteHeapType.Defined -> definedType(type.definedType)
        }
    }

    private fun typeIndex(index: Int) {
        if (groupStart >= 0 && index >= groupStart && index < groupStart + group.subTypes.size) {
            add(Tag.INTERNAL, index - groupStart)
        } else {
            add(Tag.EXTERNAL, runtimeTypes?.get(index) ?: error("standalone type contains an unresolved type index: $index"))
        }
    }

    private fun definedType(type: DefinedType) {
        if (type.recursiveType === group) {
            add(Tag.INTERNAL, type.recursiveTypeIndex)
        } else {
            add(Tag.EXTERNAL, runtimeTypes?.get(type.typeIndex) ?: registry.register(type).value)
        }
    }

    private fun add(first: Int, second: Int) {
        add(first)
        add(second)
    }

    private fun add(value: Int) {
        when (mode) {
            HASH -> hash = 31 * hash + value
            MATCH -> {
                if (cursor >= expected.size || expected[cursor] != value) matched = false
                cursor++
            }
            BUILD -> checkNotNull(output).add(value)
        }
    }

    private companion object {
        const val HASH = 0
        const val MATCH = 1
        const val BUILD = 2
        val EMPTY_WORDS = IntArray(0)
    }
}

private class IntArrayBuilder(initialCapacity: Int = 16) {
    private var values = IntArray(initialCapacity)
    private var size = 0

    fun add(value: Int) {
        ensureCapacity(1)
        values[size++] = value
    }

    fun add(first: Int, second: Int) {
        ensureCapacity(2)
        values[size++] = first
        values[size++] = second
    }

    fun toArray(): IntArray = values.copyOf(size)

    private fun ensureCapacity(additional: Int) {
        if (size + additional <= values.size) return
        values = values.copyOf(maxOf(values.size * 2, size + additional))
    }
}

private object Tag {
    const val GROUP = 1
    const val SUBTYPE = 2
    const val FINAL = 3
    const val OPEN = 4
    const val FUNCTION = 5
    const val STRUCT = 6
    const val ARRAY = 7
    const val VALUE = 8
    const val I8 = 9
    const val I16 = 10
    const val I32 = 11
    const val I64 = 12
    const val F32 = 13
    const val F64 = 14
    const val V128 = 15
    const val REF = 16
    const val REF_NULL = 17
    const val FUNC = 18
    const val NO_FUNC = 19
    const val EXTERN = 20
    const val NO_EXTERN = 21
    const val EXCEPTION = 22
    const val NO_EXCEPTION = 23
    const val ANY = 24
    const val EQ = 25
    const val STRUCT_HEAP = 26
    const val ARRAY_HEAP = 27
    const val I31 = 28
    const val NONE = 29
    const val BOTTOM = 30
    const val INTERNAL = 31
    const val EXTERNAL = 32
}
