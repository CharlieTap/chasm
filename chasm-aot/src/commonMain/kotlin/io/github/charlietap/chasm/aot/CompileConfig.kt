package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.config.RuntimeConfig

data class CompileConfig(
    val className: String,
    val target: CompileTarget = CompileTarget.JVM,
    val runtime: RuntimeConfig = RuntimeConfig(),
)

enum class CompileTarget {
    JVM,
    ANDROID,
}
