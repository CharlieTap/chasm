package io.github.charlietap.chasm.compiled

import java.nio.file.Path

sealed interface ClassInput {

    companion object {

        fun jar(path: Path): ClassInput {
            return JarClassInput(path)
        }

        fun directory(path: Path): ClassInput {
            return DirectoryClassInput(path)
        }

        fun memory(
            classFiles: Map<String, ByteArray>,
            resources: Map<String, ByteArray> = emptyMap(),
        ): ClassInput {
            return MemoryClassInput(
                classFiles = classFiles.mapValues { entry -> entry.value.copyOf() },
                resources = resources.mapValues { entry -> entry.value.copyOf() },
            )
        }
    }
}

internal data class JarClassInput(
    val path: Path,
) : ClassInput

internal data class DirectoryClassInput(
    val path: Path,
) : ClassInput

internal data class MemoryClassInput(
    val classFiles: Map<String, ByteArray>,
    val resources: Map<String, ByteArray>,
) : ClassInput
