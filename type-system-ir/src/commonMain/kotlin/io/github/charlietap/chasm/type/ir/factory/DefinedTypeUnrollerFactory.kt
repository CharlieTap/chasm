package io.github.charlietap.chasm.type.ir.factory

import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.SubType
import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeUnroller
import kotlin.collections.getOrPut

typealias UnrollCache = HashMap<DefinedType, SubType>
typealias DefinedTypeUnrollerFactory = (UnrollCache) -> DefinedTypeUnroller

fun DefinedTypeUnrollerFactory(
    cache: UnrollCache,
): DefinedTypeUnroller = { definedType ->
    cache.getOrPut(definedType) { DefinedTypeUnroller(definedType) }
}
