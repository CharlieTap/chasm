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
            schemaVersion = 2,
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
                            instructions = listOf(
                                CompilerBaselineInstruction(
                                    offset = 0,
                                    tag = "numeric.i32.add.ss",
                                    immediates = JsonObject(mapOf("left_slot" to JsonPrimitive(0))),
                                ),
                                CompilerBaselineInstruction(offset = 1, tag = "admin.fuel_check"),
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
        assertTrue("\"schema_version\": 2" in output)
        assertTrue("immediates" !in output.toString().substringAfter("admin.fuel_check"))
        assertEquals('\n', output.last())
    }
}
