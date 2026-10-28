package io.github.charlietap.chasm.tools.compilerbaseline

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompilerBaselineWriterTest {

    @Test
    fun `writes generated baseline to supplied output`() {
        val baseline = CompilerBaseline(
            schemaVersion = 3,
            modules = listOf(
                CompilerBaselineModule(
                    name = "fixture",
                    functions = listOf(
                        CompilerBaselineFunction(
                            index = 3,
                            name = "add",
                            frameSlots = 4,
                            activationHeaderSlot = 2,
                            localInitialization = JsonPrimitive("none"),
                            unlinkedCheckpoints = listOf(0),
                            instructions = listOf(
                                CompilerBaselineInstruction(
                                    offset = 0,
                                    position = 1,
                                    dispatcher = "I32AddSsDispatcher",
                                    fields = JsonObject(mapOf("left_slot" to JsonPrimitive(0))),
                                ),
                                CompilerBaselineInstruction(offset = 1, position = 2, dispatcher = "UnreachableDispatcher"),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val writer = CompilerBaselineWriter(
            generator = CompilerBaselineGenerator { baseline },
            json = compilerBaselineJson,
        )
        val output = StringBuilder()

        writer.write(CompilerBaselineFixture("fixture", byteArrayOf()), output)

        assertEquals(baseline, compilerBaselineJson.decodeFromString(output.toString()))
        assertTrue("\"schema_version\": 3" in output)
        assertTrue("\"unlinked_checkpoints\"" in output)
        assertTrue("fields" !in output.toString().substringAfter("UnreachableDispatcher"))
        assertEquals('\n', output.last())
    }
}
