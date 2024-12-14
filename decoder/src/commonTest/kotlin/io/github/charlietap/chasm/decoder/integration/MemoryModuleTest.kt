package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.type.limits
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryModuleTest {

    @Test
    fun `can decode a memory module section`() {

        val byteStream = Resource("src/commonTest/resources/memory.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

        val expectedMemory = Memory(
            idx = Index.MemoryIndex(0u),
            type = memoryType(
                limits = limits(1u, 2u),
            ),
        )

        val expected = Ok(
            module(
                version = Version.One,
                types = emptyList(),
                imports = emptyList(),
                functions = emptyList(),
                tables = emptyList(),
                memories = listOf(expectedMemory),
                globals = emptyList(),
                exports = emptyList(),
                startFunction = null,
                elementSegments = emptyList(),
                dataSegments = emptyList(),
                customs = emptyList(),
            ),
        )

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
