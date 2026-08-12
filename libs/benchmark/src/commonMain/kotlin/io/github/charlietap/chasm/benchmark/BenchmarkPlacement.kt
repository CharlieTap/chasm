package io.github.charlietap.chasm.benchmark

public data class CpuSample(
    public val cpuId: Int?,
    public val isFastest: Boolean?,
)

public data class TrialPlacement(
    public val start: CpuSample,
    public val end: CpuSample,
    public val isSupported: Boolean = true,
) {
    public val isValid: Boolean
        get() = !isSupported || start.isFastest == true && end.isFastest == true
}

public data class PolicyApplication(
    public val mode: BenchmarkMode,
    public val isApplied: Boolean,
    public val nativeErrorCode: Int? = null,
    public val message: String? = null,
    public val isSupported: Boolean = true,
) {
    /** Whether the benchmark can proceed, including when placement is an unsupported no-op. */
    public val canProceed: Boolean
        get() = !isSupported || isApplied
}
