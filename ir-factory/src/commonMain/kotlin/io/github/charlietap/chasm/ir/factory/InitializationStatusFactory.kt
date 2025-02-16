package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.InitializationStatus
import io.github.charlietap.chasm.ir.type.InitializationStatus as IRInitializationStatus

internal inline fun InitializationStatusFactory(
    initializationStatus: InitializationStatus,
): IRInitializationStatus {
    return when (initializationStatus) {
        InitializationStatus.SET -> IRInitializationStatus.SET
        InitializationStatus.UNSET -> IRInitializationStatus.UNSET
    }
}
