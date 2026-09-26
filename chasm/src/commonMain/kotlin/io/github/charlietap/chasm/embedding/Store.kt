package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.store.Fuel
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

fun store(): Store = Store(InternalStore())

fun store(config: StoreConfig): Store = Store(InternalStore(fuel = Fuel(metered = config.meterFuel)))
