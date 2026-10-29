package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.cir.ReferenceType

sealed interface Resource {

    data object Instance : Resource

    data object Unsafe : Resource

    data object ByteArrayBase : Resource

    data class Memory(
        val index: Int,
    ) : Resource

    data class Table(
        val index: Int,
    ) : Resource

    data class Global(
        val index: Int,
    ) : Resource

    data class Function(
        val index: Int,
    ) : Resource

    data class Element(
        val index: Int,
    ) : Resource

    data class Data(
        val index: Int,
    ) : Resource

    data class TypeTest(
        val type: ReferenceType,
    ) : Resource

    data class ExceptionTable(
        val instructionCount: Int,
        val interfaceSlotCount: Int,
        val resultCount: Int,
        val regions: List<TryRegion>,
    ) : Resource {
        private val hash = run {
            var result = instructionCount
            result = 31 * result + interfaceSlotCount
            result = 31 * result + resultCount
            31 * result + regions.hashCode()
        }

        override fun hashCode(): Int = hash
    }

    class Longs(
        val values: LongArray,
    ) : Resource {
        private val hash = values.contentHashCode()

        override fun equals(
            other: Any?,
        ): Boolean = other === this || other is Longs && other.hash == hash && other.values.contentEquals(values)

        override fun hashCode(): Int = hash

        override fun toString(): String = "Longs(${values.joinToString()})"
    }

    data class FunctionReference(
        val index: Int,
    ) : Resource

    data class TagAddress(
        val index: Int,
    ) : Resource

    data class RuntimeType(
        val typeIndex: Int,
    ) : Resource
}

val Resource.isValue: Boolean
    get() = this is Resource.FunctionReference || this is Resource.TagAddress || this is Resource.RuntimeType
