package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

class BrOnCastTest {

    @Test
    fun `can run the br_on_null 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(6))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-sub test from the br_on_cast_2 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-sub",
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-canon test from the br_on_cast_2 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-canon",
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
