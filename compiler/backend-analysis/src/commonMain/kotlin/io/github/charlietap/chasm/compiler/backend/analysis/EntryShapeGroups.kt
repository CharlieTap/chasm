package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.compiler.backend.abi.EntryShape

data class EntryShapeGroup<F>(
    val shape: EntryShape,
    val members: List<F>,
)

inline fun <F> entryShapeGroups(
    functions: List<F>,
    limit: Int,
    shape: (F) -> EntryShape,
): List<EntryShapeGroup<F>> {
    return entryShapeGroups(functions, limit, shape) { }
}

inline fun <F, P> entryShapeGroups(
    functions: List<F>,
    limit: Int,
    shape: (F) -> EntryShape,
    partition: (F) -> P,
): List<EntryShapeGroup<F>> {

    val membersByShapeAndPartition = LinkedHashMap<Pair<EntryShape, P>, MutableList<F>>()
    for (function in functions) {
        membersByShapeAndPartition.getOrPut(shape(function) to partition(function), ::ArrayList) += function
    }

    val groups = ArrayList<EntryShapeGroup<F>>()
    for ((shapeAndPartition, members) in membersByShapeAndPartition) {
        val groupShape = shapeAndPartition.first
        for (chunk in members.chunked(limit)) {
            groups += EntryShapeGroup(groupShape, chunk)
        }
    }

    return groups
}
