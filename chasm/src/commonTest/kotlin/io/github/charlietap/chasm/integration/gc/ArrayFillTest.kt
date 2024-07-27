package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayFillTest {

    @Test
    fun `can run the array_fill test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_fill",
            arguments = listOf(i32(12), i32(0), i32(0)),
        )

        val expected = emptyList<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 1 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(0)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(12), i32(0), i32(0)),
            ),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 2 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(5)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(12), i32(0), i32(0)),
            ),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 3 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(11)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(12), i32(0), i32(0)),
            ),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_fill 1 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_fill",
            arguments = listOf(i32(2), i32(11), i32(2)),
        )

        val expected = emptyList<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 4 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(1)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(2), i32(11), i32(2)),
            ),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 5 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(2)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(2), i32(11), i32(2)),
            ),
        )

        val expected = listOf(i32(11))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 6 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(3)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(2), i32(11), i32(2)),
            ),
        )

        val expected = listOf(i32(11))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the array_get_nth 7 test from the array_fill gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "array_fill.wasm",
            fileDirectory = FILE_DIR,
            functionName = "array_get_nth",
            arguments = listOf(i32(4)),
            setupFunctions = listOf(
                "array_fill" to listOf(i32(2), i32(11), i32(2)),
            ),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
