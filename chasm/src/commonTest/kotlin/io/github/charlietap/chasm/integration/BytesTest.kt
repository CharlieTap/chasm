package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BytesTest {

    @Test
    fun `can instantiate a module and read bytes from its memory`() {

        val byteStream = Resource(FILE_DIR + "string.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

        val store = publicStore()
        val memory = module(reader)
            .flatMap { module ->
                instance(store, module, emptyList())
            }.map { instance ->
                instance.exports.first { it.name == "memory" }.value
            }.getOrNull() as Memory

        val string = "hello world"

        val expected = string.encodeToByteArray()
        val buffer = ByteArray(expected.size)

        val actual = readBytes(
            store = store,
            memory = memory,
            buffer = buffer,
            memoryPointer = 1,
            bytesToRead = expected.size,
        )

        assertContentEquals(expected, actual.getOrNull())
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
