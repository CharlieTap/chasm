package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.HeapType
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

class ExternTest {

    @Test
    fun `can run the internalize 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "internalize",
            arguments = listOf(Value.Reference.Extern(Value.Reference.Host(1))),
        )

        val expected = listOf(Value.Reference.Host(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the internalize 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "internalize",
            arguments = listOf(Value.Reference.Null(HeapType.Extern)),
        )

        val expected = listOf(Value.Reference.Null(HeapType.Any))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize",
            arguments = listOf(Value.Reference.Host(2)),
        )

        val expected = listOf(Value.Reference.Extern(Value.Reference.Host(2)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize",
            arguments = listOf(Value.Reference.Null(HeapType.Any)),
        )

        val expected = listOf(Value.Reference.Null(HeapType.Extern))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-i 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(i32(0)),
        )

        val expected = listOf(Value.Reference.Null(HeapType.Extern))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-i 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 3 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 4 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 5 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 6 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(i32(5)),
        )

        val expected = listOf(Value.Reference.Null(HeapType.Extern))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-ii 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(i32(0)),
        )

        val expected = listOf(Value.Reference.Null(HeapType.Any))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-ii 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.I31, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-ii 3 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.Struct, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-ii 4 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        assertIs<ChasmResult<Value.Reference.Array, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-ii 5 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf(Value.Reference.Host(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-ii 6 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(i32(5)),
        )

        val expected = listOf(Value.Reference.Null(HeapType.Any))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
