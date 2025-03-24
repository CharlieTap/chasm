package io.github.charlietap.chasm.type.expansion

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

typealias DefinedTypeExpander = (DefinedType, DefinedTypeUnroller) -> CompositeType

fun DefinedTypeExpander(
    definedType: DefinedType,
    definedTypeUnroller: DefinedTypeUnroller,
): CompositeType = definedTypeUnroller(definedType).compositeType
