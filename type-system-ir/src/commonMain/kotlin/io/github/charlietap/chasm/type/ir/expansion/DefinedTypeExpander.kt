package io.github.charlietap.chasm.type.ir.expansion

import io.github.charlietap.chasm.ir.type.CompositeType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeUnroller

typealias DefinedTypeExpander = (DefinedType) -> CompositeType

fun DefinedTypeExpander(
    definedType: DefinedType,
): CompositeType =
    DefinedTypeExpander(
        definedType = definedType,
        definedTypeUnroller = ::DefinedTypeUnroller,
    )

internal fun DefinedTypeExpander(
    definedType: DefinedType,
    definedTypeUnroller: DefinedTypeUnroller,
): CompositeType = definedTypeUnroller(definedType).compositeType
