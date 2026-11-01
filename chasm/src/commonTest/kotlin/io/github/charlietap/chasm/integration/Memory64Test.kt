package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import kotlin.test.Test
import kotlin.test.assertNotNull

class Memory64Test {

    @Test
    fun `can decode a wasm binary containing memory64 opcodes`() {

        val bytes = Resource(FILE_DIR + "memory64.wasm").readBytes()
        val module = module(bytes).getOrNull()

        assertNotNull(module)
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
