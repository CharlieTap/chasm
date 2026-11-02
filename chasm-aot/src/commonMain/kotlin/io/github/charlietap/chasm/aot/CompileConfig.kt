package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig

class CompileConfig private constructor(
    val packageName: String,
    val target: CompileTarget,
    val stacktraces: Boolean,
    val storeConfig: StoreConfig,
    val initialFuel: Long?,
    val moduleConfig: ModuleConfig,
    val maxMethodBytes: Int,
    val functionMaxMethodBytes: Map<Int, Int>,
) {

    class Builder(
        private val packageName: String,
    ) {
        private var target = CompileTarget.JVM
        private var stacktraces = false
        private var storeConfig = StoreConfig()
        private var initialFuel: Long? = null
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

        fun stacktraces(
            stacktraces: Boolean,
        ) = apply { this.stacktraces = stacktraces }

        fun store(
            config: StoreConfig,
        ) = apply { this.storeConfig = config }

        fun initialFuel(
            units: Long,
        ) = apply { this.initialFuel = units }

        fun moduleConfig(
            moduleConfig: ModuleConfig,
        ) = apply { this.moduleConfig = moduleConfig }

        fun build(): CompileConfig = CompileConfig(
            packageName = packageName,
            target = target,
            stacktraces = stacktraces,
            storeConfig = storeConfig,
            initialFuel = initialFuel,
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
