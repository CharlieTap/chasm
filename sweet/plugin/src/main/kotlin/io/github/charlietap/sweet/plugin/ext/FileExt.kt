package io.github.charlietap.sweet.plugin.ext

import java.io.File

fun File.backtrackCollectingDirectoriesUntil(predicate: (File) -> Boolean): List<String> {
    val directories = mutableListOf<String>()
    var file = this
    while(!predicate(file)) {
        file = file.parentFile
        directories += (file.name)
    }
    return directories
}
