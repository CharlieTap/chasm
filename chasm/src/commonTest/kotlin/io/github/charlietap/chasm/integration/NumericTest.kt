package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class NumericTest {

    // wasm-interp multiply.wasm -r multiply -a i32:16 -a i32:4
    @Test
    fun `can run a wasm file with the multiply instruction and return a correct result`() {

        val result = testRunner(
            fileName = "multiply.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(16), NumberValue.I32(4)),
        )

        val expected = listOf(NumberValue.I32(4))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/numeric/"
    }
}
