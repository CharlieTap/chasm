package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.readBorrowedMemory
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.integration.writeBorrowedMemory
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull

class BorrowedMemoryTest {

    @Test
    fun `borrowed memory storage holds the data the module declares`() = integrationTest(FIXTURE) {

        val memory = assertNotNull(runner().exports().expect("exports").memory("memory"))
        val expected = "hello world".encodeToByteArray()

        assertContentEquals(expected, readBorrowedMemory(memory, 1, expected.size), engine.name())
    }

    @Test
    fun `writes through borrowed memory storage are visible to memory reads`() = integrationTest(FIXTURE) {

        val memory = assertNotNull(runner().exports().expect("exports").memory("memory"))
        val written = "borrowed".encodeToByteArray()

        writeBorrowedMemory(memory, 64, written)

        assertContentEquals(written, readBytes(memory, ByteArray(written.size), 64, written.size), engine.name())
    }

    @Test
    fun `borrowed memory storage sees writes made through memory calls`() = integrationTest(FIXTURE) {

        val memory = assertNotNull(runner().exports().expect("exports").memory("memory"))
        val written = "visible".encodeToByteArray()

        writeBytes(memory, 128, written)

        assertContentEquals(written, readBorrowedMemory(memory, 128, written.size), engine.name())
    }

    private companion object {
        const val FIXTURE = "string"
    }
}
