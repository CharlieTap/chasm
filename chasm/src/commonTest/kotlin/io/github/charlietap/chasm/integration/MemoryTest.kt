package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {

    // wasm-interp memory_init.wasm -r memory_init
    @Test
    fun `can run a memory init instruction and return a correct result`() {

        val result = testRunner(
            fileName = "memory_init.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp memory_load_store.wasm -r memory_load_store
    @Test
    fun `can run a wasm file with load and store memory instructions and return a correct result`() {

        val result = testRunner(
            fileName = "memory_load_store.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(118))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp memory_copy.wasm -r memory_copy -a i32:0 -a i32:4 -a i32:4
    @Test
    fun `can run a memory copy instruction and return a correct result`() {

        val result = testRunner(
            fileName = "memory_copy.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(
                NumberValue.I32(0),
                NumberValue.I32(4),
                NumberValue.I32(4),
            ),
        )

        val expected = listOf(NumberValue.I32(2000000000))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp memory_fill.wasm -r memory_fill -a i32:0 -a i32:117 -a i32:1
    @Test
    fun `can run a memory fill instruction and return a correct result`() {

        val result = testRunner(
            fileName = "memory_fill.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(
                NumberValue.I32(0),
                NumberValue.I32(117),
                NumberValue.I32(1),
            ),
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp memory_size.wasm -r memory_size
    @Test
    fun `can run a memory size instruction and return a correct result`() {

        val result = testRunner(
            fileName = "memory_size.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp memory_grow.wasm -r memory_grow -a i32:116
    @Test
    fun `can run a memory grow instruction and return a correct result`() {

        val result = testRunner(
            fileName = "memory_grow.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(
                NumberValue.I32(116),
            ),
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/memory/"
    }
}
