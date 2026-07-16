package io.github.charlietap.chasm.wasi.p2

data class WasiP2CommandConfig(
    val maxRandomBytes: Int = DEFAULT_MAX_RANDOM_BYTES,
    val writePermitBytes: Int = DEFAULT_WRITE_PERMIT_BYTES,
) {
    init {
        require(maxRandomBytes >= 0) { "WASI P2 maximum random byte count must be non-negative" }
        require(writePermitBytes >= 0) { "WASI P2 write permit must be non-negative" }
    }
}

private const val DEFAULT_MAX_RANDOM_BYTES = 64 * 1024
private const val DEFAULT_WRITE_PERMIT_BYTES = 4 * 1024
