package io.github.charlietap.chasm.config

/**
 * Configures the allocation of WebAssembly linear memory.
 *
 * @property prefault asks supported backends to make newly exposed
 * linear-memory pages resident during construction and growth. The JVM
 * and Kotlin/Native mapped-memory backends honor this setting. Android
 * currently ignores it because its array-backed memory does not expose an
 * equivalent operation.
 */
data class LinearMemoryConfig(
    val prefault: Boolean = false,
)
