package io.github.charlietap.chasm.config

data class Config(
    val moduleConfig: ModuleConfig,
    val runtimeConfig: RuntimeConfig,
) {
    companion object {
        fun default() = Config(
            moduleConfig = ModuleConfig.default(),
            runtimeConfig = RuntimeConfig.default(),
        )
    }
}
