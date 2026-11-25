package io.github.charlietap.chasm.compiled.internal

import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile

internal interface ClassSource {

    fun classFile(className: String): ByteArray?
}

internal class MemoryClassSource(
    private val classFiles: Map<String, ByteArray>,
) : ClassSource {

    override fun classFile(className: String): ByteArray? {
        return classFiles[className]
    }
}

internal class DirectoryClassSource(
    private val directory: Path,
) : ClassSource {

    override fun classFile(className: String): ByteArray? {

        val file = directory.resolve(classFilePath(className))

        return if (Files.isRegularFile(file)) {
            Files.readAllBytes(file)
        } else {
            null
        }
    }
}

internal class JarClasses(
    private val jar: JarFile,
) : ClassSource {

    override fun classFile(className: String): ByteArray? {

        val entry = jar.getJarEntry(classFilePath(className)) ?: return null

        return jar.getInputStream(entry).use { stream -> stream.readBytes() }
    }
}

private fun classFilePath(className: String): String {
    return className.replace('.', '/') + ".class"
}
