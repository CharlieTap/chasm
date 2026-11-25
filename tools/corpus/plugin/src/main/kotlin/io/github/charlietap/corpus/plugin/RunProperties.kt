package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.lib.CorpusPhase
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

class RunProperties(
    providers: ProviderFactory,
    extension: WasmCorpusPluginExtension,
    requestedTasks: List<String>,
) {

    private val baselineRequested = requestedTasks.any { task ->
        task.substringAfterLast(':').startsWith(BASELINE_TASK_PREFIX)
    }

    val phase: Provider<CorpusPhase> = providers.gradleProperty(PROP_PHASE)
        .map { value -> phase(value) }
        .orElse(extension.phase)

    val targets: Provider<List<String>> = providers.gradleProperty(PROP_TARGETS)
        .map { value -> commaSeparated(value) }
        .orElse(extension.targets)

    val engines: Provider<Set<String>> = providers.gradleProperty(PROP_ENGINES)
        .map { value -> commaSeparated(value).toSet() }
        .orElse(extension.engines)

    val warmups: Provider<Int> = providers.gradleProperty(PROP_WARMUPS)
        .map { value -> count(PROP_WARMUPS, value) }
        .orElse(if (baselineRequested) extension.baselineWarmups else extension.warmups)

    val rounds: Provider<Int> = providers.gradleProperty(PROP_ROUNDS)
        .map { value -> count(PROP_ROUNDS, value) }
        .orElse(if (baselineRequested) extension.baselineRounds else extension.rounds)
        .zip(warmups) { roundCount, warmupCount -> rounds(roundCount, warmupCount) }

    val pin: Provider<Boolean> = providers.gradleProperty(PROP_PIN)
        .map { value -> flag(PROP_PIN, value) }
        .orElse(extension.pin)

    val label: Provider<String> = providers.gradleProperty(PROP_LABEL)

    val before: Provider<List<String>> = providers.gradleProperty(PROP_BEFORE).map { value -> commaSeparated(value) }

    val after: Provider<List<String>> = providers.gradleProperty(PROP_AFTER).map { value -> commaSeparated(value) }

    private fun commaSeparated(value: String): List<String> {
        return value.split(",")
            .map(String::trim)
            .filter(String::isNotEmpty)
    }

    private fun count(
        property: String,
        value: String,
    ): Int {

        val count = value.trim().toIntOrNull()

        return requireNotNull(count?.takeIf { parsed -> parsed >= 0 }) {
            "Invalid $property value '$value'. Expected a count of zero or more"
        }
    }

    private fun flag(
        property: String,
        value: String,
    ): Boolean {

        val enabled = when (value.trim().lowercase()) {
            "", "true" -> true
            "false" -> false
            else -> null
        }

        return requireNotNull(enabled) {
            "Invalid $property value '$value'. Expected true or false"
        }
    }

    private fun rounds(
        roundCount: Int,
        warmupCount: Int,
    ): Int {

        require(warmupCount == 0 || roundCount > 0) {
            "$warmupCount warm-ups need at least one round; set $PROP_ROUNDS as well as $PROP_WARMUPS"
        }

        return roundCount
    }

    private fun phase(value: String): CorpusPhase {

        val phase = CorpusPhase.entries.firstOrNull { entry ->
            entry.name.equals(value.trim(), ignoreCase = true)
        }

        return requireNotNull(phase) {
            "Invalid $PROP_PHASE value '$value'. Expected one of: ${
                CorpusPhase.entries.joinToString { entry -> entry.name.lowercase() }
            }"
        }
    }

    private companion object {
        const val PROP_PHASE = "wasmCorpus.phase"
        const val PROP_TARGETS = "wasmCorpus.targets"
        const val PROP_ENGINES = "wasmCorpus.engines"
        const val PROP_WARMUPS = "wasmCorpus.warmups"
        const val PROP_ROUNDS = "wasmCorpus.rounds"
        const val PROP_PIN = "wasmCorpus.pin"
        const val PROP_LABEL = "wasmCorpus.label"
        const val PROP_BEFORE = "wasmCorpus.before"
        const val PROP_AFTER = "wasmCorpus.after"
        const val BASELINE_TASK_PREFIX = "updateCorpusBaseline"
    }
}
