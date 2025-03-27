package io.github.charlietap.chasm.type

import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

data class DefinedType(
    val recursiveType: RecursiveType,
    val recursiveTypeIndex: Int,
    val typeIndex: Int = -1,
) {
    val asSubType by lazy {
        DefinedTypeUnroller(this)
    }

    val parent by lazy {
        (asSubType.superTypes.firstOrNull() as? ConcreteHeapType.Defined)?.definedType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DefinedType

        if (recursiveTypeIndex != other.recursiveTypeIndex) return false
        if (recursiveType != other.recursiveType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recursiveTypeIndex
        result = 31 * result + recursiveType.hashCode()
        return result
    }
}
