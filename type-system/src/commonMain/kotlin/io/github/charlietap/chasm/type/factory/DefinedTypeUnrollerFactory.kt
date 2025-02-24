package io.github.charlietap.chasm.type.factory

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

typealias UnrollCache = HashMap<DefinedType, SubType>
typealias DefinedTypeUnrollerFactory = (UnrollCache) -> DefinedTypeUnroller

fun DefinedTypeUnrollerFactory(
    cache: UnrollCache,
): DefinedTypeUnroller = { definedType ->
    cache.getOrPut(definedType) { DefinedTypeUnroller(definedType) }
}
