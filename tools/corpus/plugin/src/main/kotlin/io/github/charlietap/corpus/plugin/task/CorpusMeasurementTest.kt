package io.github.charlietap.corpus.plugin.task

import org.gradle.api.tasks.testing.Test
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "A baseline measurement must always run fresh")
abstract class CorpusMeasurementTest : Test() {
    init {
        outputs.upToDateWhen { false }
    }
}
