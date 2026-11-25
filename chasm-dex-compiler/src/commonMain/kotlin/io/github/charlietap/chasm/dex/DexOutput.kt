package io.github.charlietap.chasm.dex

import java.nio.file.Path
import java.util.Collections

interface DexOutput {

    fun dexFile(
        name: String,
        bytes: ByteArray,
    )

    fun resource(
        path: String,
        bytes: ByteArray,
    )

    companion object {

        /**
         * Writes one compiled module into the directory at [path]: `classes.dex`, `classes2.dex` and so on, plus the
         * module's resources (for a module compiled as `com.example.Guest`, `com/example/Guest.chasmdata` when it has
         * data segments and `com/example/Guest.sites` when it was compiled with stack traces). Load it on the device
         * with `module(DexInput.directory(path), className)`.
         *
         * The directory must be missing or empty, otherwise the compile fails at [CompileError.Stage.OUTPUT]. Android
         * only loads DEX files named `classes.dex`, `classes2.dex`, ..., so the file names cannot say which module they
         * belong to. In a directory that already held a compile, a newer module with two DEX files would be loaded
         * together with the older module's `classes3.dex`, and clearing the directory first could delete files that a
         * module loaded from it still uses. One directory therefore holds exactly one module, from one compile.
         *
         * Every file is made read-only before it is written, because Android 14 and later only load DEX from
         * read-only files. Nothing is written when the compile fails, and if writing itself fails part way, the files
         * and directories it created are removed again.
         *
         * To replace a module, compile it into a new directory, load the module from there, and delete the old
         * directory only once no module, store or instance loaded from it is still in use. A directory named after
         * the Wasm module and the compile settings works as a cache:
         *
         * ```kotlin
         * val directory = cache.resolve("$wasmHash-$settingsName-$appVersion")
         * if (!Files.isDirectory(directory)) {
         *     val staging = Files.createTempDirectory(cache, "compiling")
         *     compile(wasm, DexOutput.directory(staging), config).expect("compiles")
         *     Files.move(staging, directory, StandardCopyOption.ATOMIC_MOVE)
         * }
         * val module = module(DexInput.directory(directory), config.className).expect("loads")
         * ```
         *
         * Here `wasmHash` identifies the Wasm bytes, `settingsName` the [CompileConfig] they were compiled with, and
         * `appVersion` changes whenever the app ships another Chasm version. Compiling into a fresh staging directory
         * and renaming it means a crash mid-compile never leaves a half-written module under the cached name.
         */
        fun directory(path: Path): DexOutput {
            return DirectoryDexOutput(path)
        }

        /**
         * Writes one compiled module into the jar at [path]: `classes.dex`, `classes2.dex` and so on, plus the module's
         * resources (for a module compiled as `com.example.Guest`, `com/example/Guest.chasmdata` when it has data
         * segments and `com/example/Guest.sites` when it was compiled with stack traces). Load it on the device with
         * `module(DexInput.jar(path), className)`.
         *
         * The jar is written to a temporary file beside [path], made read-only before it is written (Android 14 and
         * later only load DEX from read-only files), and renamed over [path] once the compile has succeeded. A jar
         * therefore always holds exactly one module from one compile, and a failed compile leaves an existing jar at
         * [path] untouched.
         *
         * To replace a module, compile it to the same path, which swaps the new jar in by rename, or to a new path,
         * deleting the old jar only once no module, store or instance loaded from it is still in use. The caching
         * example on [directory] works the same way with a jar named after the Wasm module and the compile settings.
         */
        fun jar(path: Path): DexOutput {
            return JarDexOutput(path)
        }

        fun memory(): MemoryDexOutput {
            return MemoryDexOutput()
        }
    }
}

class MemoryDexOutput internal constructor() : DexOutput {

    private val writtenDexFiles = ArrayList<ByteArray>()
    private val writtenResources = LinkedHashMap<String, ByteArray>()

    val dexFiles: List<ByteArray> = Collections.unmodifiableList(writtenDexFiles)

    val resources: Map<String, ByteArray> = Collections.unmodifiableMap(writtenResources)

    override fun dexFile(
        name: String,
        bytes: ByteArray,
    ) {
        writtenDexFiles += bytes
    }

    override fun resource(
        path: String,
        bytes: ByteArray,
    ) {
        writtenResources[path] = bytes
    }
}
