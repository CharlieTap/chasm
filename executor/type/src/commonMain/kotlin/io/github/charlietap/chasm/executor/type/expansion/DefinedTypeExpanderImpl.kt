package io.github.charlietap.chasm.executor.type.expansion

import io.github.charlietap.chasm.ast.type.CompositeType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.type.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.executor.type.rolling.DefinedTypeUnrollerImpl

fun DefinedTypeExpanderImpl(
    definedType: DefinedType,
): CompositeType =
    DefinedTypeExpanderImpl(
        definedType = definedType,
        definedTypeUnroller = ::DefinedTypeUnrollerImpl,
    )

internal fun DefinedTypeExpanderImpl(
    definedType: DefinedType,
    definedTypeUnroller: DefinedTypeUnroller,
): CompositeType = definedTypeUnroller(definedType).compositeType
