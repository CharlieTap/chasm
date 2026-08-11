package io.github.charlietap.sweet.plugin.ext

internal fun String.toTaskNameSegment(): String {
    val words = split(NON_ALPHANUMERIC)
        .filter(String::isNotEmpty)

    val value = words.joinToString("") { word ->
        word.replaceFirstChar { character -> character.titlecase() }
    }

    return value.ifEmpty { "Source" }
}

internal fun String.toClassNameSegment(): String {
    val value = toTaskNameSegment()
    return if (value.first().isDigit()) "_$value" else value
}

internal fun String.toPackageSegment(): String {
    val snakeCase = replace(CAMEL_CASE_BOUNDARY, "$1_$2")
    val value = snakeCase
        .lowercase()
        .replace(NON_ALPHANUMERIC, "_")
        .trim('_')

    return when {
        value.isEmpty() -> "source"
        value.first().isDigit() -> "_$value"
        else -> value
    }
}

private val CAMEL_CASE_BOUNDARY = Regex("([a-z0-9])([A-Z])")
private val NON_ALPHANUMERIC = Regex("[^A-Za-z0-9]+")
