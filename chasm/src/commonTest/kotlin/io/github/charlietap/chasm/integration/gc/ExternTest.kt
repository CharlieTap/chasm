package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ExternTest {

    @Test
    fun `can run the internalize 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "internalize",
            arguments = listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(1)))),
        )

        val expected = listOf(ReferenceValue.Host(Address.Host(1)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the internalize 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "internalize",
            arguments = listOf(ReferenceValue.Null(AbstractHeapType.Extern)),
        )

        val expected = listOf(ReferenceValue.Null(AbstractHeapType.Any))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize",
            arguments = listOf(ReferenceValue.Host(Address.Host(2))),
        )

        val expected = listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(2))))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize",
            arguments = listOf(ReferenceValue.Null(AbstractHeapType.Any)),
        )

        val expected = listOf(ReferenceValue.Null(AbstractHeapType.Extern))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-i 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(NumberValue.I32(0)),
        )

        val expected = listOf(ReferenceValue.Null(AbstractHeapType.Extern))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-i 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 3 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 4 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 5 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.Extern, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-i 6 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-i",
            arguments = listOf(NumberValue.I32(5)),
        )

        val expected = listOf(ReferenceValue.Null(AbstractHeapType.Extern))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-ii 1 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(NumberValue.I32(0)),
        )

        val expected = listOf(ReferenceValue.Null(AbstractHeapType.Any))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-ii 2 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.I31, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-ii 3 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.Struct, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-ii 4 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        assertIs<ChasmResult<ReferenceValue.Array, ChasmError>>(result)
    }

    @Test
    fun `can run the externalize-ii 5 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(ReferenceValue.Host(Address.Host(0)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the externalize-ii 6 test from the extern gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "extern.wasm",
            fileDirectory = FILE_DIR,
            functionName = "externalize-ii",
            arguments = listOf(NumberValue.I32(5)),
        )

        val expected = listOf(ReferenceValue.Null(AbstractHeapType.Any))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
