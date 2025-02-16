package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.SharedStatus
import io.github.charlietap.chasm.ir.type.Limits as IRLimits
import io.github.charlietap.chasm.ir.type.MemoryType as IRMemoryType
import io.github.charlietap.chasm.ir.type.SharedStatus as IRSharedStatus

internal fun MemoryTypeFactory(
    memoryType: MemoryType,
): IRMemoryType = MemoryTypeFactory(
    memoryType = memoryType,
    limitsFactory = ::LimitsFactory,
    sharedStatusFactory = ::SharedStatusFactory,
)

internal inline fun MemoryTypeFactory(
    memoryType: MemoryType,
    limitsFactory: IRFactory<Limits, IRLimits>,
    sharedStatusFactory: IRFactory<SharedStatus, IRSharedStatus>,
): IRMemoryType {
    return IRMemoryType(
        limits = limitsFactory(memoryType.limits),
        shared = sharedStatusFactory(memoryType.shared),
    )
}
