package io.github.charlietap.corpus.plugin.task

import io.github.charlietap.corpus.lib.report.CorpusReport
import io.github.charlietap.corpus.plugin.report.BaselineNames
import io.github.charlietap.corpus.plugin.report.BaselineReportCheck
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Copies a measurement")
abstract class RecordCorpusBaselineTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val report: RegularFileProperty

    @get:Input
    abstract val warmups: Property<Int>

    @get:Input
    abstract val rounds: Property<Int>

    @get:Input
    abstract val phase: Property<String>

    @get:Input
    abstract val environmentEngines: SetProperty<String>

    @get:Internal
    abstract val baselineDirectory: DirectoryProperty

    @TaskAction
    fun record() {

        val text = report.get().asFile.readText()
        val recorded = Json { ignoreUnknownKeys = true }.decodeFromString(CorpusReport.serializer(), text)
        val check = BaselineReportCheck(
            baselineTask = path,
            warmups = warmups.get(),
            rounds = rounds.get(),
            phase = phase.get(),
            environmentEngines = environmentEngines.get(),
        )

        check.check(recorded)
        baselineDirectory.file(BaselineNames().name(recorded.environment)).get().asFile.writeText(text)
    }
}
