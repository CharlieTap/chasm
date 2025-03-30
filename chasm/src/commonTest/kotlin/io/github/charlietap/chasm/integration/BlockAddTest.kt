package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BlockAddTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() {

        val config = RuntimeConfig(bytecodeFusion = false)
        val store = store()

        val result = testRunner(
            store = store,
            fileName = "blockadd.wasm",
            fileDirectory = FILE_DIR,
            config = config,
            arguments = listOf(
                i32(2),
                i32(2),
            ),
        )

        assertIs<ChasmResult.Success<List<ExecutionValue>>>(result)
        assertEquals(listOf(i32(4)), result.result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
