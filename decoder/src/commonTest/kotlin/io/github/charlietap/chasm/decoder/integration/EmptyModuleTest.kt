package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.module.module
import kotlin.test.Test
import kotlin.test.assertEquals

class EmptyModuleTest {

    @Test
    fun `can decode an empty module`() {

        val byteStream = Resource("src/commonTest/resources/empty.wasm").readBytes()

        val reader = FakeSourceReader(byteStream)

        val expected = Ok(
            module(),
        )

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
