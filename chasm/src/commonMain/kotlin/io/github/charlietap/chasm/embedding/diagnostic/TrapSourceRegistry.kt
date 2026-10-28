package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class TrapSourceRegistry {

    private val sources = ArrayList<TrapSource>()

    fun register(
        source: TrapSource?,
    ) {
        sources.add(source ?: return)
    }

    fun unregister(instance: ModuleInstance) {
        sources.removeAll { source -> source.instance === instance }
    }

    fun clear() {
        sources.clear()
    }

    fun find(instance: ModuleInstance): TrapSource? = sources.firstOrNull { source -> source.instance === instance }
}
