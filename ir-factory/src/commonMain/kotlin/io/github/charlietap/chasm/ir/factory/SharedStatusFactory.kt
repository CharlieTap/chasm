package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.SharedStatus
import io.github.charlietap.chasm.ir.type.SharedStatus as IRSharedStatus

internal inline fun SharedStatusFactory(
    sharedStatus: SharedStatus,
): IRSharedStatus {
    return when (sharedStatus) {
        SharedStatus.Shared -> IRSharedStatus.Shared
        SharedStatus.Unshared -> IRSharedStatus.Unshared
    }
}
