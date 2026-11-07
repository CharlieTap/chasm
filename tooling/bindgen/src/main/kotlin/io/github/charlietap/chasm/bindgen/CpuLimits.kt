package io.github.charlietap.chasm.bindgen

sealed interface CpuLimits {
    class Unmetered : CpuLimits

    data class Metered(
        val fuel: Fuel,
    ) : CpuLimits
}
