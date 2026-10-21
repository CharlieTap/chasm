package io.github.charlietap.chasm.embedding.ext

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig

@Suppress("DEPRECATION")
internal fun RuntimeConfig.withStoreSettings(store: StoreConfig): RuntimeConfig {
    if (store.gcStrategy == null && store.gcThreshold == null) return this
    return copy(
        gcStrategy = store.gcStrategy ?: gcStrategy,
        gcThreshold = store.gcThreshold ?: gcThreshold,
    )
}
