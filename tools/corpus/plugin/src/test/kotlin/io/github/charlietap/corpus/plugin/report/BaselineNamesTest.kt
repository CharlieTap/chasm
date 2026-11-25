package io.github.charlietap.corpus.plugin.report

import io.github.charlietap.corpus.lib.report.CorpusEnvironment
import io.github.charlietap.corpus.lib.report.CorpusMachine
import io.github.charlietap.corpus.lib.report.CorpusRuntime
import kotlin.test.Test
import kotlin.test.assertEquals

class BaselineNamesTest {

    @Test
    fun `a host environment is recorded under its own name`() {

        assertEquals("jvm.json", BaselineNames().name(environment("jvm", "Mac17,9")))
        assertEquals("androidHost.json", BaselineNames().name(environment("androidHost", "Mac17,9")))
    }

    @Test
    fun `a device is recorded under the model in its report`() {

        assertEquals("android-SM-S721B.json", BaselineNames().name(environment("androidDevice", "SM-S721B")))
    }

    @Test
    fun `a device model with spaces is recorded without them`() {

        assertEquals("android-Pixel-8-Pro.json", BaselineNames().name(environment("androidDevice", "Pixel 8 Pro")))
    }

    private fun environment(
        name: String,
        model: String,
    ): CorpusEnvironment {
        return CorpusEnvironment(
            name = name,
            machine = CorpusMachine("Android", "17", "arm64-v8a", model, null, 10, null),
            runtime = CorpusRuntime("ART", "unknown", null),
            conditions = emptyMap(),
        )
    }
}
