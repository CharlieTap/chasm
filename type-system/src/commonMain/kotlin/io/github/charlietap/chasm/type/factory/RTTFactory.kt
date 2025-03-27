package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

typealias RTTFactory = (DefinedType, MutableMap<DefinedType, RTT>) -> RTT

inline fun RTTFactory(
    type: DefinedType,
    cache: Map<DefinedType, RTT>,
): RTT {
    val superTypes = mutableListOf<RTT>()
    var superType = type.parent
    while (superType != null) {
        superTypes.add(cache[superType]!!)
        superType = superType.parent
    }
    return RTT(superTypes)
}
