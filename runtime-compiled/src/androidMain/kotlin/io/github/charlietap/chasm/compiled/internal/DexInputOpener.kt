package io.github.charlietap.chasm.compiled.internal

import dalvik.system.InMemoryDexClassLoader
import dalvik.system.PathClassLoader
import io.github.charlietap.chasm.compiled.DexInput
import io.github.charlietap.chasm.compiled.DirectoryDexInput
import io.github.charlietap.chasm.compiled.JarDexInput
import io.github.charlietap.chasm.compiled.MemoryDexInput
import io.github.charlietap.chasm.compiled.format.dexFileName
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.jar.JarFile

internal class DexInputOpener(
    private val parent: ClassLoader,
    private val sdk: Int,
) {

    fun open(input: DexInput): ChasmResult<OpenedInput, ModuleError> {
        return when (input) {
            is MemoryDexInput -> ChasmResult.Success(memory(input))
            is DirectoryDexInput -> directory(input.path)
            is JarDexInput -> jar(input.path)
        }
    }

    private fun memory(input: MemoryDexInput): OpenedInput {

        val buffers = Array(input.dexFiles.size) { index -> ByteBuffer.wrap(input.dexFiles[index]) }

        return OpenedInput(InMemoryDexClassLoader(buffers, parent), MemoryResources(input.resources))
    }

    private fun directory(path: Path): ChasmResult<OpenedInput, ModuleError> {

        val files = dexFiles(path.toFile())
        if (files.isEmpty()) {
            return malformed("$path holds no ${dexFileName(0)}")
        }
        val writable = writable(files)
        if (writable != null) {
            return malformed(writableMessage(writable))
        }

        val loader = PathClassLoader(files.joinToString(File.pathSeparator) { file -> file.path }, parent)

        return ChasmResult.Success(OpenedInput(loader, DirectoryResources(path)))
    }

    private fun dexFiles(directory: File): List<File> {

        val files = ArrayList<File>()

        while (true) {
            val file = File(directory, dexFileName(files.size))
            if (!file.isFile) {
                return files
            }
            files += file
        }
    }

    private fun jar(path: Path): ChasmResult<OpenedInput, ModuleError> {

        val file = path.toFile()
        if (!file.canRead()) {
            return unreadable(path)
        }
        val writable = writable(listOf(file))
        if (writable != null) {
            return malformed(writableMessage(writable))
        }

        val jar = try {
            JarFile(file)
        } catch (_: IOException) {
            return unreadable(path)
        }

        return ChasmResult.Success(OpenedInput(PathClassLoader(file.path, parent), JarResources(jar), jar))
    }

    private fun writable(files: List<File>): File? {
        return if (sdk >= READ_ONLY_DEX_SDK) {
            files.firstOrNull { file -> file.canWrite() }
        } else {
            null
        }
    }

    private fun writableMessage(file: File): String {
        return "$file is writable; Android 14 and later load DEX only from read-only files"
    }

    private fun unreadable(path: Path): ChasmResult.Error<ModuleError.Malformed> {
        return malformed("$path cannot be read")
    }

    private companion object {
        const val READ_ONLY_DEX_SDK = 34
    }
}
