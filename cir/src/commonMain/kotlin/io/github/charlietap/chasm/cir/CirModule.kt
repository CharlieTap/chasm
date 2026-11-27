package io.github.charlietap.chasm.cir

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
data class CirModule(
    val schemaVersion: Int = SCHEMA_VERSION,
    val functions: List<CirFunction>,
) {
    companion object {
        const val SCHEMA_VERSION = 3
    }
}
