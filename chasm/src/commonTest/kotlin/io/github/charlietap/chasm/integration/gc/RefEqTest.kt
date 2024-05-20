package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class RefEqTest {

    @Test
    fun `can run the ref_eq 0 test from the ref_eq gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(0)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 1 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(1)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 2 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(2)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 3 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(3)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 4 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(4)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 5 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(5)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 6 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(6)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 7 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(7)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 8 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(0), i32(8)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 9 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(0)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 10 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(1)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 11 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(2)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 12 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(3)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 13 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(4)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 14 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(5)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 15 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(6)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 16 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(7)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 17 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(1), i32(8)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 18 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(0)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 19 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(1)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 20 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(2)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 21 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(3)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 22 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(4)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 23 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(5)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 24 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(6)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 25 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(7)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_eq 26 test from the ref_eq gc test spec return a correct result`() {
        val result = testRunner(
            fileName = "ref_eq.wasm",
            fileDirectory = FILE_DIR,
            functionName = "eq",
            arguments = listOf(i32(2), i32(8)),
            setupFunctions = listOf("init" to emptyList()),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
