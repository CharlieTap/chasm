package io.github.charlietap.chasm.benchmark

public enum class BenchmarkMode {
    /**
     * Requests user-interactive QoS on Darwin.
     *
     * This is suitable for long-running work, but remains a scheduler preference.
     */
    PREFER_FASTEST,

    /**
     * Requests Darwin's time-constraint policy for a short trial.
     *
     * Continuous CPU use eventually triggers the kernel's realtime failsafe. Call
     * [BenchmarkStabilizer.checkpoint] between trials and keep individual trials short.
     */
    SHORT_REALTIME,
}
