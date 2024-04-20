package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ArrayTest {

    @Test
    fun `can run the new test from the array_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "new",
        )

        assertIs<ChasmResult<ReferenceValue.Array, ChasmError>>(result)
    }

    @Test
    fun `can run get from the array_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get",
            arguments = listOf(NumberValue.I32(0)),
        )

        val expected = listOf(NumberValue.F32(0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get from the array_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get",
            arguments = listOf(NumberValue.I32(0), NumberValue.F32(7f)),
        )

        val expected = listOf(NumberValue.F32(7f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run len from the array_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "len",
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the new test from the array_2 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "new",
        )

        assertIs<ChasmResult<ReferenceValue.Array, ChasmError>>(result)
    }

    @Test
    fun `can run get from the array_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get",
            arguments = listOf(NumberValue.I32(0)),
        )

        val expected = listOf(NumberValue.F32(1f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get from the array_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get",
            arguments = listOf(NumberValue.I32(1), NumberValue.F32(7f)),
        )

        val expected = listOf(NumberValue.F32(7f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run len from the array_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "len",
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the new test from the array_3 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_3.wasm",
            fileDirectory = FILE_DIR,
            functionName = "new",
        )

        assertIs<ChasmResult<ReferenceValue.Array, ChasmError>>(result)
    }

    @Test
    fun `can run get_u from the array_3 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_3.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(NumberValue.I32(2)),
        )

        val expected = listOf(NumberValue.I32(255))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_s from the array_3 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_3.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(NumberValue.I32(2)),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run len from the array_3 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_3.wasm",
            fileDirectory = FILE_DIR,
            functionName = "len",
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the new test from the array_4 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "new",
        )

        assertTrue(result is ChasmResult.Success)
        assertTrue(result.result.first() is ReferenceValue.Array)
    }

    @Test
    fun `can run get 1 from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get",
            arguments = listOf(NumberValue.I32(0), NumberValue.I32(0)),
        )

        val expected = listOf(NumberValue.I32(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get 2 from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get",
            arguments = listOf(NumberValue.I32(1), NumberValue.I32(0)),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get",
            arguments = listOf(NumberValue.I32(0), NumberValue.I32(1), NumberValue.I32(1)),
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run len from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "len",
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
