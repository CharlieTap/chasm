package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.ClassInput
import io.github.charlietap.chasm.compiled.DirectoryClassInput
import io.github.charlietap.chasm.compiled.JarClassInput
import io.github.charlietap.chasm.compiled.MemoryClassInput
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile

internal class ClassInputOpener(
    private val parent: ClassLoader,
) {

    fun open(
        input: ClassInput,
        className: String,
    ): ChasmResult<OpenedInput, ModuleError> {
        return when (input) {
            is MemoryClassInput -> opened(className, MemoryClassSource(input.classFiles), MemoryResources(input.resources))
            is DirectoryClassInput -> directory(input.path, className)
            is JarClassInput -> jar(input.path, className)
        }
    }

    private fun directory(
        path: Path,
        className: String,
    ): ChasmResult<OpenedInput, ModuleError> {

        if (!Files.isDirectory(path) || !Files.isReadable(path)) {
            return unreadable(path)
        }

        return opened(className, DirectoryClassSource(path), DirectoryResources(path))
    }

    private fun jar(
        path: Path,
        className: String,
    ): ChasmResult<OpenedInput, ModuleError> {

        val jar = try {
            JarFile(path.toFile())
        } catch (_: IOException) {
            return unreadable(path)
        }

        return opened(className, JarClasses(jar), JarResources(jar), jar)
    }

    private fun opened(
        className: String,
        classes: ClassSource,
        resources: ModuleResources,
        jar: JarFile? = null,
    ): ChasmResult<OpenedInput, ModuleError> {
        return ChasmResult.Success(OpenedInput(ArtifactClassLoader(className, classes, parent), resources, jar))
    }

    private fun unreadable(path: Path): ChasmResult.Error<ModuleError.Malformed> {
        return malformed("$path cannot be read")
    }
}
