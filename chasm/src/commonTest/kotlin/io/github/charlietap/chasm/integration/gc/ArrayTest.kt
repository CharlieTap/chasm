package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import io.github.charlietap.chasm.embedding.fixture.publicF32 as f32
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

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
            arguments = listOf(i32(0)),
        )

        val expected = listOf(f32(0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get from the array_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get",
            arguments = listOf(i32(0), f32(7f)),
        )

        val expected = listOf(f32(7f))

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

        val expected = listOf(i32(3))

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
            arguments = listOf(i32(0)),
        )

        val expected = listOf(f32(1f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get from the array_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get",
            arguments = listOf(i32(1), f32(7f)),
        )

        val expected = listOf(f32(7f))

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

        val expected = listOf(i32(2))

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
            arguments = listOf(i32(2)),
        )

        val expected = listOf(i32(255))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_s from the array_3 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_3.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(2)),
        )

        val expected = listOf(i32(-1))

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

        val expected = listOf(i32(3))

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
        assertTrue(result.result.first() is Value.Reference.Array)
    }

    @Test
    fun `can run get 1 from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get",
            arguments = listOf(i32(0), i32(0)),
        )

        val expected = listOf(i32(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get 2 from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get",
            arguments = listOf(i32(1), i32(0)),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get from the array_4 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "array_4.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get",
            arguments = listOf(i32(0), i32(1), i32(1)),
        )

        val expected = listOf(i32(2))

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

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
