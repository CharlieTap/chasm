package io.github.charlietap.chasm.aot

import java.nio.file.Path
import java.util.Collections

interface ClassOutput {

    fun classFile(
        className: String,
        bytes: ByteArray,
    )

    fun resource(
        path: String,
        bytes: ByteArray,
    )

    companion object {

        fun directory(path: Path): ClassOutput {
            return DirectoryClassOutput(path)
        }

        fun jar(path: Path): ClassOutput {
            return JarClassOutput(path)
        }

        fun memory(): MemoryClassOutput {
            return MemoryClassOutput()
        }
    }
}

class MemoryClassOutput internal constructor() : ClassOutput {

    private val committedClassFiles = LinkedHashMap<String, ByteArray>()
    private val committedResources = LinkedHashMap<String, ByteArray>()

    internal val staging: StagedClassOutput = Staging()

    val classFiles: Map<String, ByteArray> = Collections.unmodifiableMap(committedClassFiles)

    val resources: Map<String, ByteArray> = Collections.unmodifiableMap(committedResources)

    override fun classFile(
        className: String,
        bytes: ByteArray,
    ) {
        staging.classFile(className, bytes)
    }

    override fun resource(
        path: String,
        bytes: ByteArray,
    ) {
        staging.resource(path, bytes)
    }

    private inner class Staging : StagedClassOutput {

        private val classFiles = LinkedHashMap<String, ByteArray>()
        private val resources = LinkedHashMap<String, ByteArray>()

        override fun classFile(
            className: String,
            bytes: ByteArray,
        ) {
            classFiles[className] = bytes
        }

        override fun resource(
            path: String,
            bytes: ByteArray,
        ) {
            resources[path] = bytes
        }

        override fun commit() {

            committedClassFiles.putAll(classFiles)
            committedResources.putAll(resources)

            discard()
        }

        override fun discard() {
            classFiles.clear()
            resources.clear()
        }
    }
}
