package io.github.charlietap.chasm.cir

data class CirModule(
    val schemaVersion: Int = SCHEMA_VERSION,
    val functions: List<CirFunction>,
) {
    companion object {
        const val SCHEMA_VERSION = 2
    }
}
