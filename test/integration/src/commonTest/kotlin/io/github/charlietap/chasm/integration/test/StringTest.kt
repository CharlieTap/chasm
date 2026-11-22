package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.memory.readNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.memory.readUtf8String
import io.github.charlietap.chasm.embedding.memory.writeUtf8String
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StringTest {

    @Test
    fun `can instantiate a module and read a ut8 encoded string from its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        val expected = "hello world"
        val actual = readUtf8String(
            memory = memory,
            pointer = 1,
            stringLengthInBytes = expected.encodeToByteArray().size,
        )

        assertEquals(expected, actual, engine.name())
    }

    @Test
    fun `can instantiate a module and write a ut8 encoded string to its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        writeUtf8String(
            memory = memory,
            pointer = 1 + "hello ".encodeToByteArray().size,
            string = "hello",
        )

        val expected = "hello hello"
        val actual = readUtf8String(
            memory = memory,
            pointer = 1,
            stringLengthInBytes = expected.encodeToByteArray().size,
        )

        assertEquals(expected, actual, engine.name())
    }

    @Test
    fun `can instantiate a module and read a null terminated ut8 encoded string from its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        val expected = "hello world"
        val actual = readNullTerminatedUtf8String(
            memory = memory,
            pointer = 1,
        )

        assertEquals(expected, actual, engine.name())
    }

    private fun TestRunner.memory(): Memory {
        return assertNotNull(exports().expect("exports").memory("memory"))
    }

    private companion object {
        const val FIXTURE = "string"
    }
}
