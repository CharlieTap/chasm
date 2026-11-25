package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import java.io.ByteArrayInputStream
import java.io.InputStream

internal actual fun loadClassFiles(
    classFiles: Map<String, ByteArray>,
    resources: Map<String, ByteArray>,
    className: String,
): ChasmResult<Module, ModuleError> {
    return module(MemoryClassLoader(classFiles, resources), className)
}

private class MemoryClassLoader(
    private val classFiles: Map<String, ByteArray>,
    private val resources: Map<String, ByteArray>,
) : ClassLoader(MemoryClassLoader::class.java.classLoader) {

    override fun findClass(name: String): Class<*> {

        val bytes = classFiles[name] ?: throw ClassNotFoundException(name)

        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun getResourceAsStream(name: String): InputStream? {
        return resources[name]?.let(::ByteArrayInputStream) ?: super.getResourceAsStream(name)
    }
}
