package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.error.InstantiationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class Memory64Test {

    @Test
    fun `can decode a wasm binary containing memory64 opcodes`() {

        val bytes = Resource(FILE_DIR + "memory64.wasm").readBytes()
        val module = module(bytes).getOrNull()

        assertNotNull(module)
    }

    @Test
    fun `returns an unsupported memory 64 module error when executing`() {

        val store = Store(store())
        val actual = testRunner(
            fileName = "memory64.wasm",
            fileDirectory = FILE_DIR,
            store = store,
        )

        val expected = ChasmError.ExecutionError(InstantiationError.UnsupportedMemory64Module.toString())
        assertEquals(ChasmResult.Error(expected), actual)
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
