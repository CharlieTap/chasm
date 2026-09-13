package io.github.charlietap.chasm.config

/**
 * Configures the allocation of WebAssembly linear memory.
 *
 * @property prefault asks supported backends to make newly exposed
 * linear-memory pages resident during construction and growth. The JVM
 * mapped-memory backend honors this setting. Android and Kotlin/Native
 * currently ignore it because their array-backed memories do not expose an
 * equivalent operation.
 */
data class LinearMemoryConfig(
    val prefault: Boolean = false,
)
