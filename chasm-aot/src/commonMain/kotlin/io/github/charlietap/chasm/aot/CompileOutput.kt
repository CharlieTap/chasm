package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.MemoryClassLoader
import java.nio.file.Path

/** Where [compile] puts a module's class files and resources. */
sealed interface CompileOutput {

    /** Keeps compiled modules in memory. Not safe to use from several threads at once. */
    class Memory : CompileOutput {

        internal val classFiles = LinkedHashMap<String, ByteArray>()
        internal val resourceFiles = LinkedHashMap<String, ByteArray>()

        // Keyed by internal class name, such as com/example/parser/WasmModule.
        val classes: Map<String, ByteArray>
            get() = classFiles

        val resources: Map<String, ByteArray>
            get() = resourceFiles

        /** Loads the compiled module on this JVM, in a class loader of its own. */
        @ExperimentalChasmApi
        @OptIn(InternalChasmApi::class)
        fun load(
            artifact: CompiledModuleArtifact,
        ): CompiledModule =
            MemoryClassLoader(classFiles.copied(), resourceFiles.copied(), CompiledModule::class.java.classLoader).module(artifact.moduleClassName)

        /** The loader reads its bytes lazily, so it keeps copies callers cannot change underneath it. */
        private fun Map<String, ByteArray>.copied(): Map<String, ByteArray> = mapValues { (_, bytes) -> bytes.copyOf() }
    }

    /** Writes the class files and resources under [path], laid out as a classes directory on a classpath. */
    class Directory(
        val path: Path,
    ) : CompileOutput

    /**
     * Writes the module into the jar at [path], continuing in `name-2.jar`, `name-3.jar` and so on beside it past the
     * entries one jar holds. Replaces the jars an earlier compilation left there.
     */
    class Jar(
        val path: Path,
    ) : CompileOutput
}
