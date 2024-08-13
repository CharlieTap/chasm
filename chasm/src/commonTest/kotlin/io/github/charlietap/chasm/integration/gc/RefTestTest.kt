package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

class RefTestTest {

    @Test
    fun `can run the ref_test_null_data 0 test from the ref_test_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 6 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_data 7 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_data",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 6 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_any 7 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_any",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 6 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_eq 7 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_eq",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 6 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_i31 7 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_i31",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 6 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_struct 7 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_struct",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 6 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_array 7 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_array",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_func 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_func",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_func 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_func",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_func 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_func",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_func 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_func",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_func 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_func",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_func 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_func",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_extern 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_extern",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_extern 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_extern",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_extern 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_extern",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_extern 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_extern",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_extern 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_extern",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_null_extern 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_null_extern",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_extern 0 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_extern",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_extern 1 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_extern",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_extern 2 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_extern",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_extern 3 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_extern",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_extern 4 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_extern",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_test_extern 5 test from the ref_test_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_test_extern",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-sub test from the ref_test_2 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-sub",
            arguments = listOf(),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-canon test from the ref_test_2 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_test_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-canon",
            arguments = listOf(),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
