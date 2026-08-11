package io.github.charlietap.sweet.plugin.ext

import java.io.File

internal fun File.relativeSuitePath(root: File): String {
    val rootPath = root.toPath().toAbsolutePath().normalize()
    val filePath = toPath().toAbsolutePath().normalize()
    val relativePath = rootPath.relativize(filePath)

    require(!relativePath.startsWith("..")) {
        "File $filePath is outside suite root $rootPath"
    }

    return relativePath.joinToString("/") { segment -> segment.toString() }
}

internal fun File.deleteAndPruneEmptyParents(root: File) {
    delete()

    var directory = parentFile
    while (directory != null && directory != root && directory.listFiles()?.isEmpty() == true) {
        directory.delete()
        directory = directory.parentFile
    }
}
