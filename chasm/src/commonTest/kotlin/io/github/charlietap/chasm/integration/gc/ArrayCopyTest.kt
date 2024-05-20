package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

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

        val expected = listOf<ExecutionValue>()

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

        val expected = emptyList<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
