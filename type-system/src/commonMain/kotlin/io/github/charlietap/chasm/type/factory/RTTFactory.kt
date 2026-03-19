package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

typealias RTTFactory = (DefinedType, MutableMap<DefinedType, RTT>) -> RTT

inline fun RTTFactory(
    type: DefinedType,
    cache: MutableMap<DefinedType, RTT>,
): RTT = cache.getOrPut(type) {
    RTT(type, cache)
}
