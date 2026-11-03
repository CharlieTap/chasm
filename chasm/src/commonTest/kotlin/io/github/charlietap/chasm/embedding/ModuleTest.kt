@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.decoder.ModuleDecoder
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._config
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import io.github.charlietap.chasm.fixture.ast.module.module as moduleFixture

class ModuleTest {

    @Test
    fun `can create a module using bytes`() {

        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()

        val actual = module(bytes)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    @Test
    fun `can create a module using a stream`() {

        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()
        val reader = FakeSourceReader(bytes)

        val actual = module(reader)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    @Test
    fun `passes bytes and config to the module decoder`() {
        val bytes = byteArrayOf(0x01, 0x02)
        val config = moduleConfig()
        val decoded = moduleFixture()
        val decoder = ModuleDecoder<ByteArray> { actualConfig, actualBytes ->
            assertEquals(config, actualConfig)
            assertContentEquals(bytes, actualBytes)
            Ok(decoded)
        }

        val actual = assertIs<ChasmResult.Success<Module>>(
            module(bytes, config, decoder),
        ).result

        assertEquals(config, actual._config())
        assertEquals(decoded, actual._module())
    }

    @Test
    fun `passes a source reader and config to the module decoder`() {
        val source = FakeSourceReader()
        val config = moduleConfig()
        val decoded = moduleFixture()
        val decoder = ModuleDecoder<SourceReader> { actualConfig, actualSource ->
            assertEquals(config, actualConfig)
            assertSame(source, actualSource)
            Ok(decoded)
        }

        val actual = assertIs<ChasmResult.Success<Module>>(
            module(source, config, decoder),
        ).result

        assertEquals(config, actual._config())
        assertEquals(decoded, actual._module())
    }

    private companion object {
        const val FILE_DIR = "embedding/"
    }
}
