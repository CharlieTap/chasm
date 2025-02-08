package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.memory.readNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.memory.readUtf8String
import io.github.charlietap.chasm.embedding.memory.writeUtf8String
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.shapes.map
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest {

    @Test
    fun `can instantiate a module and read a ut8 encoded string from its memory`() {

        val byteStream = Resource(FILE_DIR + "string.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

        val store = publicStore()
        val memory = module(reader)
            .flatMap { module ->
                instance(store, module, emptyList())
            }.map { instance ->
                instance.exports.first { it.name == "memory" }.value
            }.getOrNull() as Memory

        val expected = "hello world"
        val actual = readUtf8String(
            store = store,
            memory = memory,
            pointer = 1,
            stringLengthInBytes = expected.encodeToByteArray().size,
        )

        assertEquals(ChasmResult.Success(expected), actual)
    }

    @Test
    fun `can instantiate a module and write a ut8 encoded string to its memory`() {

        val byteStream = Resource(FILE_DIR + "string.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

        val store = publicStore()
        val memory = module(reader)
            .flatMap { module ->
                instance(store, module, emptyList())
            }.map { instance ->
                instance.exports.first { it.name == "memory" }.value
            }.getOrNull() as Memory

        writeUtf8String(
            store = store,
            memory = memory,
            pointer = 1 + "hello ".encodeToByteArray().size,
            string = "hello",
        )

        val expected = "hello hello"
        val actual = readUtf8String(
            store = store,
            memory = memory,
            pointer = 1,
            stringLengthInBytes = expected.encodeToByteArray().size,
        )

        println(actual.getOrNull())
        assertEquals(ChasmResult.Success(expected), actual)
    }

    @Test
    fun `can instantiate a module and read a null terminated ut8 encoded string from its memory`() {

        val byteStream = Resource(FILE_DIR + "string.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

        val store = publicStore()
        val memory = module(reader)
            .flatMap { module ->
                instance(store, module, emptyList())
            }.map { instance ->
                instance.exports.first { it.name == "memory" }.value
            }.getOrNull() as Memory

        val expected = "hello world"
        val actual = readNullTerminatedUtf8String(
            store = store,
            memory = memory,
            pointer = 1,
        )

        assertEquals(ChasmResult.Success(expected), actual)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
