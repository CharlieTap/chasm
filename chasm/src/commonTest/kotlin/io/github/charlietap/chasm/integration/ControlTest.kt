package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ControlTest {

    // wasm-interp loops_and_labels.wasm -r loops_and_labels
    @Test
    fun `can run a wasm file with multiple control blocks and return a correct result`() {

        val result = testRunner(
            fileName = "loops_and_labels.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(10))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp nested_function.wasm -r nested_function -a i32:9
    @Test
    fun `can run a wasm file with nested functions and a return a correct result`() {

        val result = testRunner(
            fileName = "nested_function.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(9)),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp nested_return.wasm -r nested_return -a i32:4
    @Test
    fun `can run a wasm file with nested return statements and return a correct result`() {

        val result = testRunner(
            fileName = "nested_return.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(4)),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/control/"
    }
}
