package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class VariableTest {

    // wasm-interp local_get.wasm -r local_get -a i32:117
    @Test
    fun `can run a local_get instruction and return a correct result`() {

        val result = testRunner(
            fileName = "local_get.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(117)),
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp local_set.wasm -r local_set
    @Test
    fun `can run a local_set instruction and return a correct result`() {

        val result = testRunner(
            fileName = "local_set.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp local_tee.wasm -r local_tee
    @Test
    fun `can run a local_tee instruction and return a correct result`() {

        val result = testRunner(
            fileName = "local_tee.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(234))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp global_get.wasm -r global_get
    @Test
    fun `can run a global_get instruction and return a correct result`() {

        val result = testRunner(
            fileName = "global_get.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp global_set.wasm -r global_set
    @Test
    fun `can run a global_set instruction and return a correct result`() {

        val result = testRunner(
            fileName = "global_set.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/variable/"
    }
}
