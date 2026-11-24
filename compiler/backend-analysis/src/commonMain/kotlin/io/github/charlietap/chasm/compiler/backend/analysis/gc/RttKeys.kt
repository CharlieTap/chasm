package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import java.security.MessageDigest
import java.util.IdentityHashMap

class RttKeys(
    private val types: List<DefinedType>,
) {
    private val groups = IdentityHashMap<RecursiveType, String>()

    operator fun get(index: Int): String {
        return key(types[index])
    }

    fun key(type: DefinedType): String {
        return groupKey(type.recursiveType) + "_" + type.recursiveTypeIndex
    }

    fun groupKey(group: RecursiveType): String {

        groups[group]?.let { key -> return key }

        val digest = MessageDigest.getInstance("SHA-256")
        GroupEncoder(digest, group).encode()
        val key = digest.digest().copyOf(KEY_BYTES).joinToString("") { byte -> HEX[byte.toInt() shr 4 and 0xF].toString() + HEX[byte.toInt() and 0xF] }
        groups[group] = key

        return key
    }

    private fun resolve(type: HeapType): DefinedType {
        return when (type) {
            is ConcreteHeapType.TypeIndex -> types[type.index]
            is ConcreteHeapType.Defined -> type.definedType
            else -> error("$type does not name a defined type")
        }
    }

    private inner class GroupEncoder(
        private val digest: MessageDigest,
        private val group: RecursiveType,
    ) {
        private val word = ByteArray(4)

        fun encode() {

            add(FORMAT_VERSION)
            add(group.subTypes.size)

            for (subType in group.subTypes) {
                add(if (subType is SubType.Final) FINAL else OPEN)
                add(subType.superTypes.size)
                subType.superTypes.forEach(::heapType)
                compositeType(subType.compositeType)
            }
        }

        private fun compositeType(type: CompositeType) {
            when (type) {
                is CompositeType.Function -> {
                    add(FUNCTION)
                    resultType(type.functionType.params)
                    resultType(type.functionType.results)
                }
                is CompositeType.Struct -> {
                    add(STRUCT)
                    add(type.structType.fields.size)
                    type.structType.fields.forEach(::fieldType)
                }
                is CompositeType.Array -> {
                    add(ARRAY)
                    fieldType(type.arrayType.fieldType)
                }
            }
        }

        private fun resultType(type: ResultType) {
            add(type.types.size)
            type.types.forEach(::valueType)
        }

        private fun fieldType(type: FieldType) {

            when (val storage = type.storageType) {
                is StorageType.Packed -> add(if (storage.type == PackedType.I8) I8 else I16)
                is StorageType.Value -> valueType(storage.type)
            }

            add(type.mutability.ordinal)
        }

        private fun valueType(type: ValueType) {
            when (type) {
                is ValueType.Number -> add(
                    when (type.numberType) {
                        NumberType.I32 -> I32
                        NumberType.I64 -> I64
                        NumberType.F32 -> F32
                        NumberType.F64 -> F64
                    },
                )
                is ValueType.Vector -> add(V128)
                is ValueType.Reference -> {
                    add(if (type.referenceType is ReferenceType.Ref) REF else REF_NULL)
                    heapType(type.referenceType.heapType)
                }
                is ValueType.Bottom -> add(BOTTOM)
            }
        }

        private fun heapType(type: HeapType) {
            when (type) {
                is AbstractHeapType -> add(ABSTRACT, abstractCode(type))
                is ConcreteHeapType.RecursiveTypeIndex -> add(INTERNAL, type.index)
                else -> {

                    val defined = resolve(type)
                    if (defined.recursiveType === group) {
                        return add(INTERNAL, defined.recursiveTypeIndex)
                    }

                    add(EXTERNAL, defined.recursiveTypeIndex)
                    digest.update(groupKey(defined.recursiveType).encodeToByteArray())
                }
            }
        }

        private fun abstractCode(type: AbstractHeapType): Int {
            return when (type) {
                AbstractHeapType.Func -> 1
                AbstractHeapType.NoFunc -> 2
                AbstractHeapType.Extern -> 3
                AbstractHeapType.NoExtern -> 4
                AbstractHeapType.Exception -> 5
                AbstractHeapType.NoException -> 6
                AbstractHeapType.Any -> 7
                AbstractHeapType.Eq -> 8
                AbstractHeapType.Struct -> 9
                AbstractHeapType.Array -> 10
                AbstractHeapType.I31 -> 11
                AbstractHeapType.None -> 12
                is AbstractHeapType.Bottom -> 13
            }
        }

        private fun add(
            first: Int,
            second: Int,
        ) {
            add(first)
            add(second)
        }

        private fun add(value: Int) {
            word[0] = (value ushr 24).toByte()
            word[1] = (value ushr 16).toByte()
            word[2] = (value ushr 8).toByte()
            word[3] = value.toByte()
            digest.update(word)
        }
    }

    private companion object {
        const val FORMAT_VERSION = 2
        const val KEY_BYTES = 16
        const val HEX = "0123456789abcdef"

        const val FINAL = 1
        const val OPEN = 2
        const val FUNCTION = 3
        const val STRUCT = 4
        const val ARRAY = 5
        const val I8 = 6
        const val I16 = 7
        const val I32 = 8
        const val I64 = 9
        const val F32 = 10
        const val F64 = 11
        const val V128 = 12
        const val REF = 13
        const val REF_NULL = 14
        const val BOTTOM = 15
        const val ABSTRACT = 16
        const val INTERNAL = 17
        const val EXTERNAL = 18
    }
}
