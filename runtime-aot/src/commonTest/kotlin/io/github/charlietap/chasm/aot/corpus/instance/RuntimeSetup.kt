@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.corpus.instance

import at.released.weh.host.EmbedderHost
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.corpus.imports.ImportResolution
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.FunctionType

internal class RuntimeSetup(
    val store: Store,
    val instance: Instance,
    val imports: ImportResolution,
) {
    fun memory(
        name: String,
    ): Memory? {
        instance.exports.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Memory
        }
        if (name == "memory") {
            instance.exports.firstOrNull { export -> export.value is Memory }?.value?.let { value ->
                return value as Memory
            }
        }
        return imports.memories[name]
    }

    fun global(
        name: String,
    ): Global? {
        instance.exports.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Global
        }
        return imports.globals[name]
    }

    fun functionType(
        name: String,
    ): FunctionType? {
        val address = instance.instance.exports
            .firstOrNull { export -> export.name.name == name }
            ?.value
            ?.let { value -> (value as? ExternalValue.Function)?.address }
            ?: return null

        return store.store.functions.getOrNull(address.address)?.functionType
    }

    fun close() {
        imports.hosts.forEach(EmbedderHost::close)
    }
}
