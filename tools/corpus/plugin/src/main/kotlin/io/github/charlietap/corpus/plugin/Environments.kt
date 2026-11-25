package io.github.charlietap.corpus.plugin

import io.github.charlietap.corpus.plugin.task.CompareCorpusReportsTask
import io.github.charlietap.corpus.plugin.task.GenerateCorpusReportTask
import io.github.charlietap.corpus.plugin.task.RecordCorpusBaselineTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.register

class Environments(
    private val project: Project,
    private val extension: WasmCorpusPluginExtension,
    private val runProperties: RunProperties,
) {

    fun register(
        name: String,
        engines: Set<String>,
        test: Any,
        environment: Provider<RegularFile>,
        runFile: Provider<RegularFile>,
        fixturesIndex: Provider<RegularFile>,
    ) {

        val suffix = suffix(name)
        val reportName = runProperties.label.map { label -> "$name-$label.json" }.orElse("$name.json")
        val reportFile = extension.corpusReportsDirectory.zip(reportName) { directory, file -> directory.file(file) }
        val root = project.layout.settingsDirectory

        val reportTask = project.tasks.register<GenerateCorpusReportTask>("corpusReport$suffix") {
            group = GROUP
            description = "Aggregates the $name corpus measurements into a report"

            measurements.from(extension.corpusMeasurementsDirectory.dir(name))
            environmentEngines.set(engines)
            this.fixturesIndex.set(fixturesIndex)
            this.runFile.set(runFile)
            this.environment.set(environment)
            outputFile.set(reportFile)
            mustRunAfter(test)

            val recorded = this.environment
            onlyIf("the $name run recorded its environment") { recorded.get().asFile.exists() }
        }

        project.tasks.register("corpus$suffix") {
            group = GROUP
            description = "Runs the wasm-corpus on $name and writes its report"

            dependsOn(test)
        }

        project.tasks.register<RecordCorpusBaselineTask>("updateCorpusBaseline$suffix") {
            group = GROUP
            description = "Runs the wasm-corpus on $name and records the report as its baseline"

            dependsOn(test)
            report.set(reportTask.flatMap { task -> task.outputFile })
            warmups.set(extension.baselineWarmups)
            rounds.set(extension.baselineRounds)
            phase.set(extension.phase.map { phase -> phase.name.lowercase() })
            environmentEngines.set(engines)
            baselineDirectory.set(extension.corpusBaselineDirectory)
        }

        project.tasks.register<CompareCorpusReportsTask>("corpusCompare$suffix") {
            group = GROUP
            description = "Compares $name corpus reports, by default the latest report against its baseline"

            val requestedBefore = runProperties.before.map { paths -> paths.map { path -> root.file(path) } }
            val requestedAfter = runProperties.after.map { paths -> paths.map { path -> root.file(path) } }

            before.from(requestedBefore.orElse(emptyList()))
            after.from(requestedAfter.orElse(reportFile.map { file -> listOf(file) }))
            baselineDirectory.set(extension.corpusBaselineDirectory)
            noisyTargets.set(extension.noisyTargets)
        }
    }

    fun suffix(name: String): String {
        return if (name == JVM) "" else name.replaceFirstChar(Char::uppercase)
    }
}
