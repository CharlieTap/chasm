package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

class I31Test {

    @Test
    fun `can run the new test from the i31_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "new",
            arguments = listOf(i32(1)),
        )

        assertIs<ChasmResult<ReferenceValue.I31, ChasmError>>(result)
    }

    @Test
    fun `can run the get_u 0 test from the i31_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(0)),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u 1 test from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(100)),
        )

        val expected = listOf(i32(100))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u test 2 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(-1)),
        )

        val expected = listOf(i32(0x7fff_ffff))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u test 3 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(0x3fff_ffff)),
        )

        val expected = listOf(i32(0x3fff_ffff))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u test 4 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(0x4000_0000)),
        )

        val expected = listOf(i32(0x4000_0000))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u test 5 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(0x7fff_ffff)),
        )

        val expected = listOf(i32(0x7fff_ffff))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u test 6 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(0xaaaa_aaaa.toInt())),
        )

        val expected = listOf(i32(0x2aaa_aaaa))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_u test 7 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_u",
            arguments = listOf(i32(0xcaaa_aaaa.toInt())),
        )

        val expected = listOf(i32(0x4aaa_aaaa))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(0)),
        )

        val expected = listOf(i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 2 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(100)),
        )

        val expected = listOf(i32(100))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 3 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(-1)),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 4 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(0x3FFF_FFFF)),
        )

        val expected = listOf(i32(0x3FFF_FFFF))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 5 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(0x4000_0000)),
        )

        val expected = listOf(i32(-0x4000_0000))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 6 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(0x7FFF_FFFF)),
        )

        val expected = listOf(i32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 7 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(0xAAAA_AAAA.toInt())),
        )

        val expected = listOf(i32(0x2AAA_AAAA))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_s test 8 from the i31_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_s",
            arguments = listOf(i32(0xCAAA_AAAA.toInt())),
        )

        val expected = listOf(i32(0xCAAA_AAAA.toInt()))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the get_globals test from the i31_1 gc test spec and return correct results`() {
        val result = testRunner(
            fileName = "i31_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_globals",
        )

        val expected = listOf(i32(2), i32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
