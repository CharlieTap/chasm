package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.config.ModuleConfig

class CompileConfig private constructor(
    val packageName: String,
    val target: CompileTarget,
    val meterFuel: Boolean,
    val interruptible: Boolean,
    val moduleConfig: ModuleConfig,
    val maxMethodBytes: Int,
    val functionMaxMethodBytes: Map<Int, Int>,
) {

    class Builder(
        private val packageName: String,
    ) {
        private var target = CompileTarget.JVM
        private var meterFuel = false
        private var interruptible = false
        private var moduleConfig = ModuleConfig()
        private var maxMethodBytes: Int? = null
        private val functionMaxMethodBytes = LinkedHashMap<Int, Int>()

        fun target(
            target: CompileTarget,
        ) = apply { this.target = target }

        fun maxMethodBytes(
            maxMethodBytes: Int,
        ) = apply { this.maxMethodBytes = maxMethodBytes }

        fun maxMethodBytes(
            function: Int,
            maxMethodBytes: Int,
        ) = apply { functionMaxMethodBytes[function] = maxMethodBytes }

        fun meterFuel(
            meterFuel: Boolean,
        ) = apply { this.meterFuel = meterFuel }

        fun interruptible(
            interruptible: Boolean,
        ) = apply { this.interruptible = interruptible }

        fun moduleConfig(
            moduleConfig: ModuleConfig,
        ) = apply { this.moduleConfig = moduleConfig }

        fun build(): CompileConfig = CompileConfig(
            packageName = packageName,
            target = target,
            meterFuel = meterFuel,
            interruptible = interruptible,
            moduleConfig = moduleConfig,
            maxMethodBytes = maxMethodBytes ?: target.maxMethodBytes,
            functionMaxMethodBytes = functionMaxMethodBytes.toMap(),
        )
    }
}

private val CompileTarget.maxMethodBytes: Int
    get() = when (this) {
        CompileTarget.JVM -> 8_000
        CompileTarget.ANDROID -> 12_000
    }

fun CompileConfig(
    packageName: String,
    configure: CompileConfig.Builder.() -> Unit = {},
): CompileConfig =
    CompileConfig.Builder(packageName).apply(configure).build()

enum class CompileTarget {
    JVM,
    ANDROID,
}
