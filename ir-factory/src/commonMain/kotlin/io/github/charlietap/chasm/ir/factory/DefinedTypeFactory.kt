package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ir.type.DefinedType as IRDefinedType
import io.github.charlietap.chasm.ir.type.RecursiveType as IRRecursiveType

internal fun DefinedTypeFactory(
    definedType: DefinedType,
): IRDefinedType = DefinedTypeFactory(
    definedType = definedType,
    recursiveTypeFactory = ::RecursiveTypeFactory,
)

internal inline fun DefinedTypeFactory(
    definedType: DefinedType,
    recursiveTypeFactory: IRFactory<RecursiveType, IRRecursiveType>,
): IRDefinedType {
    return IRDefinedType(
        recursiveType = recursiveTypeFactory(definedType.recursiveType),
        recursiveTypeIndex = definedType.recursiveTypeIndex,
    )
}
