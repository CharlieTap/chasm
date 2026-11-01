package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BytesTest {

    @Test
    fun `can instantiate a module and read bytes from its memory`() = integrationTest(FIXTURE) {

        val runner = runner()
        val memory = runner.memory()

        val string = "hello world"

        val expected = string.encodeToByteArray()
        val buffer = ByteArray(expected.size)

        val actual = runner.readBytes(
            memory = memory,
            buffer = buffer,
            memoryPointer = 1,
            bytesToRead = expected.size,
        )

        assertContentEquals(expected, actual.getOrNull(), engine.name())
    }

    private fun TestRunner.memory(): Memory =
        exports().expect("exports").first { export -> export.name == "memory" }.value as Memory

    private companion object {
        const val FIXTURE = "string"
    }
}
