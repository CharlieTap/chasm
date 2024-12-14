package io.github.charlietap.sweet.plugin.ext

fun String.snakeCaseToPascalCase(): String {
    return split("[_-]".toRegex()).joinToString("") { it.capitalized() }.capitalized()
}

fun String.capitalized() = toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
