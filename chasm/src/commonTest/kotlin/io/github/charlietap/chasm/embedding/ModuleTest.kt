@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.fixture.config.moduleConfig
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ModuleTest {

    @Test
    fun `can create a module using bytes`() = runTest {

        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()

        val actual = module(bytes)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    @Test
    fun `can create a module using a stream`() = runTest {

        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()
        val reader = FakeSourceReader(bytes)

        val actual = module(reader)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    @Test
    fun `a module decoded from bytes keeps its config`() = runTest {
        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()
        val config = moduleConfig(decodeNameSection = true)

        val actual = assertIs<ChasmResult.Success<Module>>(
            module(bytes, config),
        ).result

        assertEquals(config, assertIs<DecodedModule>(actual._payload()).config)
    }

    @Test
    fun `a module decoded from a source reader keeps its config`() = runTest {
        val bytes = Resource(FILE_DIR + "empty.wasm").readBytes()
        val config = moduleConfig(decodeNameSection = true)

        val actual = assertIs<ChasmResult.Success<Module>>(
            module(FakeSourceReader(bytes), config),
        ).result

        assertEquals(config, assertIs<DecodedModule>(actual._payload()).config)
    }

    private companion object {
        const val FILE_DIR = "embedding/"
    }
}
