package io.github.charlietap.chasm.embedding.diagnostic

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal class TrapSourceRegistry {

    private val sources = ArrayList<TrapSource>()

    fun register(instance: ModuleInstance, module: Module) {
        sources.add(TrapSource(instance, module) ?: return)
    }

    fun unregister(instance: ModuleInstance) {
        sources.removeAll { source -> source.instance === instance }
    }

    fun clear() {
        sources.clear()
    }

    fun find(instance: ModuleInstance): TrapSource? = sources.firstOrNull { source -> source.instance === instance }
}

internal class TrapSource private constructor(
    val instance: ModuleInstance,
    val binary: ByteArray,
    val config: ModuleConfig,
) {
    companion object {
        operator fun invoke(instance: ModuleInstance, module: Module): TrapSource? {
            val binary = module.binary ?: return null
            return TrapSource(instance, binary, module.config)
        }
    }
}
