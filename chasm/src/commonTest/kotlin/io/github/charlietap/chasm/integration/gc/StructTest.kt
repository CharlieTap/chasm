package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.value.f32
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class StructTest {

    @Test
    fun `can run the new test from the struct_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "new",
        )

        assertIs<ChasmResult<ReferenceValue.Struct, ChasmError>>(result)
    }

    @Test
    fun `can run get_0_0 from the struct_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_0_0",
            arguments = listOf(),
        )

        val expected = listOf(f32(0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_vec_0 from the struct_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_vec_0",
            arguments = listOf(),
        )

        val expected = listOf(f32(0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_0_y from the struct_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_0_y",
            arguments = listOf(),
        )

        val expected = listOf(f32(0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_vec_y from the struct_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_vec_y",
            arguments = listOf(),
        )

        val expected = listOf(f32(0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get_y from the struct_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get_y",
            arguments = listOf(f32(7f)),
        )

        val expected = listOf(f32(7f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get_1 from the struct_1 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get_1",
            arguments = listOf(f32(7f)),
        )

        val expected = listOf(f32(7f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g0_0 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g0_0",
            arguments = listOf(),
        )

        val expected = listOf(i32(0), i32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g1_0 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g1_0",
            arguments = listOf(),
        )

        val expected = listOf(i32(-2), i32(254))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g0_1 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g0_1",
            arguments = listOf(),
        )

        val expected = listOf(i32(1), i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g1_1 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g1_1",
            arguments = listOf(),
        )

        val expected = listOf(i32(-1), i32(255))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g0_2 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g0_2",
            arguments = listOf(),
        )

        val expected = listOf(i32(2), i32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g1_2 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g1_2",
            arguments = listOf(),
        )

        val expected = listOf(i32(-2), i32(65534))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g0_3 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g0_3",
            arguments = listOf(),
        )

        val expected = listOf(i32(3), i32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run get_packed_g1_3 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "get_packed_g1_3",
            arguments = listOf(),
        )

        val expected = listOf(i32(-1), i32(65535))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get_packed_g0_1 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get_packed_g0_1",
            arguments = listOf(i32(257)),
        )

        val expected = listOf(i32(1), i32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run set_get_packed_g0_3 from the struct_2 gc test spec and return a correct result`() {
        val result = testRunner(
            fileName = "struct_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "set_get_packed_g0_3",
            arguments = listOf(i32(257)),
        )

        val expected = listOf(i32(257), i32(257))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
