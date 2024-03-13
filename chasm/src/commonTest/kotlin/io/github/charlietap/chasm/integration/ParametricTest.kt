package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ParametricTest {

    // wasm-interp drop.wasm -r drop
    @Test
    fun `can run a drop instruction and return a correct result`() {

        val result = testRunner(
            fileName = "drop.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp select.wasm -r select -a i32:117 -a i32:118 -a i32:1
    @Test
    fun `can run a select instruction and return a correct result`() {

        val result = testRunner(
            fileName = "select.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(
                NumberValue.I32(117),
                NumberValue.I32(118),
                NumberValue.I32(1),
            ),
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/parametric/"
    }
}
