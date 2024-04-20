package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TableTest {

    // wasm-interp table_init.wasm -r table_init
    @Test
    fun `can run a table init instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_init.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp table_size.wasm -r table_size
    @Test
    fun `can run a table size instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_size.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp table_get.wasm -r table_get
    @Test
    fun `can run a table get instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_get.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(ReferenceValue.Function(Address.Function(0)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp table_set.wasm -r table_set
    @Test
    fun `can run a table set instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_set.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(ReferenceValue.Function(Address.Function(1)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp table_fill.wasm -r table_fill
    @Test
    fun `can run a table fill instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_fill.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(ReferenceValue.Function(Address.Function(0)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp table_grow.wasm -r table_grow
    @Test
    fun `can run a table grow instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_grow.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(117))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp table_copy.wasm -r table_copy
    @Test
    fun `can run a table copy instruction and return a correct result`() {

        val result = testRunner(
            fileName = "table_copy.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(ReferenceValue.Function(Address.Function(0)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/table/"
    }
}
