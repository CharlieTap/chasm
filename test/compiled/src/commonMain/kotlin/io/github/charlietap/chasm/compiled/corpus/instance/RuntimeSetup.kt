package io.github.charlietap.chasm.compiled.corpus.instance

import at.released.weh.host.EmbedderHost
import io.github.charlietap.chasm.compiled.corpus.imports.ImportResolution
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.type.FunctionType

internal class RuntimeSetup(
    val instance: Instance,
    val exports: Exports,
    val imports: ImportResolution,
) {
    fun memory(
        name: String,
    ): Memory? {
        exports.all.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Memory
        }
        if (name == "memory") {
            exports.all.firstOrNull { export -> export.value is Memory }?.value?.let { value ->
                return value as Memory
            }
        }
        return imports.memories[name]
    }

    fun global(
        name: String,
    ): Global? {
        exports.all.firstOrNull { export -> export.name == name }?.value?.let { value ->
            return value as? Global
        }
        return imports.globals[name]
    }

    fun functionType(
        name: String,
    ): FunctionType? {
        return exports.function(name)?.type
    }

    fun close() {
        imports.hosts.forEach(EmbedderHost::close)
    }
}
