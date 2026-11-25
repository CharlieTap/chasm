package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.type.ReferenceType

internal data class ResolvedReferenceType(
    val referenceType: ReferenceType,
    val nullable: Boolean,
    val nullWord: Long,
    val acceptedTags: Int,
    val runtimeType: Int,
)

internal class ReferenceSlots(
    val valueCount: Int,
    val valueIndices: IntArray,
    val types: Array<ResolvedReferenceType>,
) {

    val isEmpty: Boolean
        get() = valueIndices.isEmpty()
}

internal const val NO_RUNTIME_TYPE = -1
