package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ir.type.Limits as IRLimits
import io.github.charlietap.chasm.ir.type.ReferenceType as IRReferenceType
import io.github.charlietap.chasm.ir.type.TableType as IRTableType

internal fun TableTypeFactory(
    tableType: TableType,
): IRTableType = TableTypeFactory(
    tableType = tableType,
    referenceTypeFactory = ::ReferenceTypeFactory,
    limitsFactory = ::LimitsFactory,
)

internal inline fun TableTypeFactory(
    tableType: TableType,
    referenceTypeFactory: IRFactory<ReferenceType, IRReferenceType>,
    limitsFactory: IRFactory<Limits, IRLimits>,
): IRTableType {
    return IRTableType(
        referenceType = referenceTypeFactory(tableType.referenceType),
        limits = limitsFactory(tableType.limits),
    )
}
