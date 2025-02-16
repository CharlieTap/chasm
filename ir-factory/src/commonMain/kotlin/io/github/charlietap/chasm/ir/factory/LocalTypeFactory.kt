package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.InitializationStatus
import io.github.charlietap.chasm.ast.type.LocalType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.ir.type.InitializationStatus as IRInitializationStatus
import io.github.charlietap.chasm.ir.type.LocalType as IRLocalType
import io.github.charlietap.chasm.ir.type.ValueType as IRValueType

internal fun LocalTypeFactory(
    localType: LocalType,
): IRLocalType = LocalTypeFactory(
    localType = localType,
    initializationStatusFactory = ::InitializationStatusFactory,
    valueTypeFactory = ::ValueTypeFactory,
)

internal inline fun LocalTypeFactory(
    localType: LocalType,
    initializationStatusFactory: IRFactory<InitializationStatus, IRInitializationStatus>,
    valueTypeFactory: IRFactory<ValueType, IRValueType>,
): IRLocalType {
    return IRLocalType(
        status = initializationStatusFactory(localType.status),
        type = valueTypeFactory(localType.type),
    )
}
