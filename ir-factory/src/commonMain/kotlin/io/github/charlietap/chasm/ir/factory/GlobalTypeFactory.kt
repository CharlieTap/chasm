package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ir.type.GlobalType as IRGlobalType
import io.github.charlietap.chasm.ir.type.Mutability as IRMutability
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType

internal fun GlobalTypeFactory(
    globalType: GlobalType,
): IRGlobalType = GlobalTypeFactory(
    globalType = globalType,
    valueTypeFactory = ::ValueTypeFactory,
    mutabilityFactory = ::MutabilityFactory,
)

internal inline fun GlobalTypeFactory(
    globalType: GlobalType,
    valueTypeFactory: IRFactory<ValueType, IRValueType>,
    mutabilityFactory: IRFactory<Mutability, IRMutability>,
): IRGlobalType {
    return IRGlobalType(
        valueType = valueTypeFactory(globalType.valueType),
        mutability = mutabilityFactory(globalType.mutability),
    )
}
