package io.github.charlietap.chasm.tools.compilerbaseline

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class CompilerBaselineWriterTest {

    @Test
    fun `writes generated baseline to supplied output`() {
        val baseline = CompilerBaseline(
            schemaVersion = 1,
            modules = listOf(CompilerBaselineModule("fixture", emptyList())),
        )
        val writer = CompilerBaselineWriter(
            generator = CompilerBaselineGenerator { baseline },
            json = Json { prettyPrint = true },
        )
        val output = StringBuilder()

        writer.write(CompilerBaselineFixture("fixture", byteArrayOf()), output)

        assertEquals(baseline, Json.decodeFromString(output.toString()))
        assertEquals('\n', output.last())
    }
}
