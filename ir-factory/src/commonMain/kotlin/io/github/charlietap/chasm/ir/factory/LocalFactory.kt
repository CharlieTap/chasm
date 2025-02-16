package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.LocalIndex
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ir.module.Index.LocalIndex as IRLocalIndex
import io.github.charlietap.chasm.ir.module.Local as IRLocal
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType

internal fun LocalFactory(
    local: Local,
): IRLocal = LocalFactory(
    local = local,
    localIndexFactory = ::LocalIndexFactory,
    valueTypeFactory = ::ValueTypeFactory,
)

internal inline fun LocalFactory(
    local: Local,
    localIndexFactory: IRFactory<LocalIndex, IRLocalIndex>,
    valueTypeFactory: IRFactory<ValueType, IRValueType>,
): IRLocal {
    return IRLocal(
        idx = localIndexFactory(local.idx),
        type = valueTypeFactory(local.type),
    )
}
