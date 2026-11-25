package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.measurement.Measurement
import io.github.charlietap.corpus.lib.report.CorpusConfiguration
import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.plugin.report.CorpusReportFactory
import io.github.charlietap.corpus.plugin.report.CorpusReportSummary
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Aggregates a measurement")
abstract class GenerateCorpusReportTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val measurements: ConfigurableFileCollection

    @get:Input
    abstract val environmentEngines: SetProperty<String>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val fixturesIndex: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val runFile: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val environment: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {

        val json = Json { ignoreUnknownKeys = true }
        val measured = measurements.asFileTree.matching { include("*/*.json") }.files.map { file ->
            json.decodeFromString(Measurement.serializer(), file.readText())
        }
        val fixtures = json.parseToJsonElement(fixturesIndex.get().asFile.readText()).jsonArray.map { element ->
            element.jsonObject.getValue("name").jsonPrimitive.content
        }

        val report = CorpusReportFactory().create(
            environment = json.decodeFromString(CorpusEnvironment.serializer(), environment.get().asFile.readText()),
            environmentEngines = environmentEngines.get(),
            configuration = json.decodeFromString(CorpusConfiguration.serializer(), runFile.get().asFile.readText()),
            fixtures = fixtures,
            measurements = measured,
        )

        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(Json { prettyPrint = true }.encodeToString(CorpusReport.serializer(), report))
        logger.lifecycle(CorpusReportSummary().describe(report, file))
    }
}
