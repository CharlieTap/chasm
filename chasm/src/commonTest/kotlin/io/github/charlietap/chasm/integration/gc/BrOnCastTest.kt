package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class BrOnCastTest {

    @Test
    fun `can run the br_on_null 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(NumberValue.I32(0)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_null 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_null",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(NumberValue.I32(0)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_i31 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_i31",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(NumberValue.I32(0)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(6))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_struct 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_struct",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(NumberValue.I32(0)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the br_on_array 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "br_on_array",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 0 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(NumberValue.I32(0)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 1 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(NumberValue.I32(1)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 2 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(NumberValue.I32(2)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 3 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(NumberValue.I32(3)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the null-diff 4 test from the br_on_cast_1 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "null-diff",
            arguments = listOf(NumberValue.I32(4)),
            setupFunctions = listOf("init" to listOf(ReferenceValue.Extern(ReferenceValue.Host(Address.Host(0))))),
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-sub test from the br_on_cast_2 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-sub",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-canon test from the br_on_cast_2 gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "br_on_cast_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-canon",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
