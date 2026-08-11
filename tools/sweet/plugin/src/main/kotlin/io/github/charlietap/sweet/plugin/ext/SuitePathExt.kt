package io.github.charlietap.sweet.plugin.ext

import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.plugin.PhaseLimit

internal data class GeneratedTestLocation(
    val sourceRelativeWastPath: String,
    val packageName: String,
    val className: String,
    val outputRelativePath: String,
)

internal fun generatedTestLocation(
    sourceName: String,
    testPackageName: String,
    scriptRelativePath: String,
): GeneratedTestLocation {
    val normalizedPath = scriptRelativePath.normalizedSuitePath()
    val pathSegments = normalizedPath.split('/').filter(String::isNotEmpty)

    require(pathSegments.size >= 2 && pathSegments.last().endsWith(".json")) {
        "Prepared script path must include its source directory: $scriptRelativePath"
    }

    val scriptName = pathSegments.last().removeSuffix(".json")
    val sourcePathSegments = pathSegments.dropLast(1)
    require(sourcePathSegments.last() == scriptName) {
        "Prepared script must be named after its source: $scriptRelativePath"
    }

    val sourceRelativeWastPath = sourcePathSegments.joinToString("/") + ".wast"
    val packageSegments = listOf(testPackageName, sourceName.toPackageSegment()) +
        sourcePathSegments.dropLast(1).map(String::toPackageSegment)
    val packageName = packageSegments.joinToString(".")
    val className = scriptName.toClassNameSegment() + "Test"
    val outputRelativePath = packageName.replace('.', '/') + "/$className.kt"

    return GeneratedTestLocation(
        sourceRelativeWastPath = sourceRelativeWastPath,
        packageName = packageName,
        className = className,
        outputRelativePath = outputRelativePath,
    )
}

internal fun resolvePhaseSupport(
    sourceRelativePath: String,
    defaultPhaseSupport: SemanticPhase,
    phaseLimits: List<PhaseLimit>,
): SemanticPhase {
    return phaseLimits
        .asSequence()
        .filter { limit ->
            limit.patterns.any { pattern -> sourceRelativePath.matchesSuitePattern(pattern) }
        }
        .map(PhaseLimit::phaseSupport)
        .plus(defaultPhaseSupport)
        .minBy(SemanticPhase::ordinal)
}

internal fun String.matchesSuitePattern(pattern: String): Boolean {
    val normalizedPath = normalizedSuitePath()
    val normalizedPattern = pattern.normalizedSuitePath()
    return suitePatternRegex(normalizedPattern).matches(normalizedPath)
}

internal fun String.normalizedSuitePath(): String {
    return replace('\\', '/')
        .removePrefix("./")
        .trimStart('/')
        .replace(REPEATED_SEPARATOR, "/")
}

private fun suitePatternRegex(pattern: String): Regex {
    val regex = StringBuilder("^")
    var index = 0

    while (index < pattern.length) {
        when (val character = pattern[index]) {
            '*' -> {
                val isDoubleWildcard = index + 1 < pattern.length && pattern[index + 1] == '*'
                if (isDoubleWildcard) {
                    val isDirectoryWildcard = index + 2 < pattern.length && pattern[index + 2] == '/'
                    if (isDirectoryWildcard) {
                        regex.append("(?:.*/)?")
                        index += 3
                    } else {
                        regex.append(".*")
                        index += 2
                    }
                } else {
                    regex.append("[^/]*")
                    index++
                }
            }
            '?' -> {
                regex.append("[^/]")
                index++
            }
            else -> {
                if (character in REGEX_META_CHARACTERS) {
                    regex.append('\\')
                }
                regex.append(character)
                index++
            }
        }
    }

    return Regex(regex.append('$').toString())
}

private const val REGEX_META_CHARACTERS = ".()[]{}+$^|\\"
private val REPEATED_SEPARATOR = Regex("/+")
