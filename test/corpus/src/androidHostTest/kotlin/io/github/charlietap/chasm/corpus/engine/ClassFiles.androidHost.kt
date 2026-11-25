package io.github.charlietap.chasm.corpus.engine

import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.jvm.CompileTarget
import io.github.charlietap.chasm.jvm.MemoryClassOutput
import java.io.ByteArrayInputStream
import java.io.InputStream

internal actual val compileTarget: CompileTarget = CompileTarget.ART

internal actual fun loadClassFiles(
    output: MemoryClassOutput,
    className: String,
): ChasmResult<Module, ModuleError> {
    return module(MemoryClassLoader(output), className)
}

private class MemoryClassLoader(
    private val output: MemoryClassOutput,
) : ClassLoader(MemoryClassLoader::class.java.classLoader) {

    override fun findClass(name: String): Class<*> {

        val bytes = output.classFiles[name] ?: throw ClassNotFoundException(name)

        return defineClass(name, bytes, 0, bytes.size)
    }

    override fun getResourceAsStream(name: String): InputStream? {
        return output.resources[name]?.let(::ByteArrayInputStream) ?: super.getResourceAsStream(name)
    }
}
