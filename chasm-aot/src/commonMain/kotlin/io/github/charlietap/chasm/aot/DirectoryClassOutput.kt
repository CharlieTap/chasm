package io.github.charlietap.chasm.aot

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal class DirectoryClassOutput(
    private val root: Path,
) : StagedClassOutput {

    private val staged = LinkedHashMap<Path, Path>()

    override fun classFile(
        className: String,
        bytes: ByteArray,
    ) {
        Files.write(stage("${className.replace('.', '/')}.class"), bytes)
    }

    override fun resource(
        path: String,
        bytes: ByteArray,
    ) {
        Files.write(stage(path), bytes)
    }

    override fun commit() {

        staged.forEach { (file, temporary) ->
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING)
        }

        staged.clear()
    }

    override fun discard() {

        staged.values.forEach { temporary ->
            val _ = runCatching { Files.deleteIfExists(temporary) }
        }

        staged.clear()
    }

    private fun stage(path: String): Path {

        val file = root.resolve(path)
        Files.createDirectories(file.parent)

        val temporary = Files.createTempFile(file.parent, ".${file.fileName}-", ".tmp")
        staged[file] = temporary

        return temporary
    }
}
