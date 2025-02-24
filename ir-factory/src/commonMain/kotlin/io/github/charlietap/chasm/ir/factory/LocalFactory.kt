package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.LocalIndex
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ir.module.Index.LocalIndex as IRLocalIndex
import io.github.charlietap.chasm.ir.module.Local as IRLocal

internal fun LocalFactory(
    local: Local,
): IRLocal = LocalFactory(
    local = local,
    localIndexFactory = ::LocalIndexFactory,
)

internal inline fun LocalFactory(
    local: Local,
    localIndexFactory: IRFactory<LocalIndex, IRLocalIndex>,
): IRLocal {
    return IRLocal(
        idx = localIndexFactory(local.idx),
        type = local.type,
    )
}
