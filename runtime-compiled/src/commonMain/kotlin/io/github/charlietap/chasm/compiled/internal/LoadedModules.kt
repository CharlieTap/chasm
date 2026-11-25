package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import java.util.jar.JarFile

internal data class OpenedInput(
    val loader: ClassLoader,
    val resources: ModuleResources,
    val jar: JarFile? = null,
)

internal class LoadedModules(
    private val parent: ParentClasses,
    private val code: RuntimeLoadedCode,
    private val reader: CompiledModuleReader,
) {

    fun shadowed(className: String): ChasmResult.Error<ModuleError.Malformed>? {

        if (!parent.defines(className)) {
            return null
        }

        return malformed("$className is already defined by the runtime's class loader")
    }

    fun module(
        opened: OpenedInput,
        className: String,
    ): ChasmResult<Module, ModuleError> {

        val result = load(opened, className)
        if (result is ChasmResult.Error) {
            opened.jar?.close()
        }

        return result
    }

    private fun load(
        opened: OpenedInput,
        className: String,
    ): ChasmResult<Module, ModuleError> {

        val compiled = when (val loaded = code.compiled(opened.loader, className)) {
            is ChasmResult.Success -> loaded.result
            is ChasmResult.Error -> return loaded
        }
        if (compiled.javaClass.classLoader !== opened.loader) {
            return malformed("$className was not defined by the input")
        }

        return reader.read(compiled, opened.resources, artifact = opened.loader)
    }
}
