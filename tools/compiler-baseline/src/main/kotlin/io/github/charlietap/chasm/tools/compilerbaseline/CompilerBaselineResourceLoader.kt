package io.github.charlietap.chasm.tools.compilerbaseline

class CompilerBaselineResourceLoader(
    private val classLoader: ClassLoader,
) {
    fun read(path: String): ByteArray = checkNotNull(classLoader.getResourceAsStream(path)) {
        "compiler baseline resource does not exist: $path"
    }.use { stream -> stream.readBytes() }
}
