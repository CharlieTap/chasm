package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.config.StoreConfig

@ExperimentalChasmApi
class CompiledModuleMetadata(
    val capabilities: Capabilities,
    val storeConfig: StoreConfig,
    val initialFuel: Long?,
    val runtimeAbi: Int,
)

@ExperimentalChasmApi
data class Capabilities(
    val meterFuel: Boolean,
    val interruptible: Boolean,
    val stacktraces: Boolean,
)
