package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ir.module.Index.TypeIndex as IRTypeIndex
import io.github.charlietap.chasm.ir.module.Type as IRType

internal fun TypeFactory(
    type: Type,
): IRType = TypeFactory(
    type = type,
    typeIndexFactory = ::TypeIndexFactory,
)

internal inline fun TypeFactory(
    type: Type,
    typeIndexFactory: IRFactory<TypeIndex, IRTypeIndex>,
): IRType {
    return IRType(
        idx = typeIndexFactory(type.idx),
        recursiveType = type.recursiveType,
    )
}
