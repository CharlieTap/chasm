package io.github.charlietap.chasm.compiled

import java.nio.file.Path

sealed interface DexInput {

    companion object {

        /**
         * Loads one compiled module from the jar at [path]: its `classes.dex`, any `classesN.dex` and the module's
         * resources (for a module compiled as `com.example.Guest`, `com/example/Guest.chasmdata` when it has data
         * segments and `com/example/Guest.sites` when it was compiled with stack traces), all from the same file.
         *
         * A jar holds exactly one module. Android 14 and later only load DEX from read-only files, so a writable jar is
         * rejected there; the jars `DexOutput.jar` writes are already read-only.
         *
         * Do not rewrite the jar while a module loaded from it is alive. To replace a module, write the new jar to a
         * temporary path and rename it over the old one, or write it to a new path and load that.
         */
        fun jar(path: Path): DexInput {
            return JarDexInput(path)
        }

        /**
         * Loads one compiled module from the directory at [path]: `classes.dex`, `classes2.dex`, `classes3.dex` and so
         * on up to the first missing number, plus the module's resources (for a module compiled as
         * `com.example.Guest`, `com/example/Guest.chasmdata` when it has data segments and `com/example/Guest.sites`
         * when it was compiled with stack traces).
         *
         * DEX file names cannot carry the module's name, so a directory holds exactly one module. Only that contiguous
         * run of `classes*.dex` files is loaded; any other DEX file in the directory is ignored rather than mixed into
         * the module. Android 14 and later only load DEX from read-only files, so a writable DEX file is rejected
         * there; the files `DexOutput.directory` writes are already read-only.
         *
         * Do not change the directory while a module loaded from it is alive. To replace a module, compile it into a
         * new directory (for example one named after the Wasm module's hash and the compile settings), load it from
         * there, and delete the old directory only once no module loaded from it is still in use.
         */
        fun directory(path: Path): DexInput {
            return DirectoryDexInput(path)
        }

        fun memory(
            dexFiles: List<ByteArray>,
            resources: Map<String, ByteArray> = emptyMap(),
        ): DexInput {
            return MemoryDexInput(
                dexFiles = dexFiles.map { bytes -> bytes.copyOf() },
                resources = resources.mapValues { entry -> entry.value.copyOf() },
            )
        }
    }
}

internal data class JarDexInput(
    val path: Path,
) : DexInput

internal data class DirectoryDexInput(
    val path: Path,
) : DexInput

internal data class MemoryDexInput(
    val dexFiles: List<ByteArray>,
    val resources: Map<String, ByteArray>,
) : DexInput
