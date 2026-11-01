package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest {

    @Test
    fun `can instantiate a module and read a ut8 encoded string from its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        val expected = "hello world"
        val actual = runner.readUtf8String(
            memory = memory,
            pointer = 1,
            stringLengthInBytes = expected.encodeToByteArray().size,
        )

        assertEquals(ChasmResult.Success(expected), actual, engine.name())
    }

    @Test
    fun `can instantiate a module and write a ut8 encoded string to its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        runner.writeUtf8String(
            memory = memory,
            pointer = 1 + "hello ".encodeToByteArray().size,
            string = "hello",
        )

        val expected = "hello hello"
        val actual = runner.readUtf8String(
            memory = memory,
            pointer = 1,
            stringLengthInBytes = expected.encodeToByteArray().size,
        )

        assertEquals(ChasmResult.Success(expected), actual, engine.name())
    }

    @Test
    fun `can instantiate a module and read a null terminated ut8 encoded string from its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        val expected = "hello world"
        val actual = runner.readNullTerminatedUtf8String(
            memory = memory,
            pointer = 1,
        )

        assertEquals(ChasmResult.Success(expected), actual, engine.name())
    }

    private fun TestRunner.memory(): Memory =
        exports().expect("exports").first { export -> export.name == "memory" }.value as Memory

    private companion object {
        const val FIXTURE = "string"
    }
}
