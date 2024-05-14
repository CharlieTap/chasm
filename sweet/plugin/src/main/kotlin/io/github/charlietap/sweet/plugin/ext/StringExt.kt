package io.github.charlietap.sweet.plugin.ext

import org.gradle.configurationcache.extensions.capitalized

fun String.snakeCaseToPascalCase(): String {
    return split("[_-]".toRegex()).joinToString("") { it.capitalized() }.capitalized()
}
