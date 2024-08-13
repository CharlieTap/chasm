package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

class ArrayCopyTest {

    @Test
    fun `can run the array_get_nth 1 test from the array_copy gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_copy.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(0)),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 2 test from the array_copy gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_copy.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(5)),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 3 test from the array_copy gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_copy.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(11)),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_copy test from the array_copy gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_copy.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_copy",
            arguments = listOf(i32(0), i32(0), i32(2)),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_copy_overlap_test-1 test from the array_copy gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_copy.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_copy_overlap_test-1",
            arguments = emptyList(),
        )

        val expected = emptyList<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
