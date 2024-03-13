package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReferenceTest {

    // wasm-interp ref_null.wasm -r ref_null
    @Test
    fun `can run a ref_null instruction and return a correct result`() {

        val result = testRunner(
            fileName = "ref_null.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(ReferenceValue.Null(ReferenceType.Funcref))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp ref_is_null.wasm -r ref_is_null
    @Test
    fun `can run a ref_is_null instruction and return a correct result`() {

        val result = testRunner(
            fileName = "ref_is_null.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp ref_func.wasm -r ref_func
    @Test
    fun `can run a ref_func instruction and return a correct result`() {

        val result = testRunner(
            fileName = "ref_func.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(ReferenceValue.FunctionAddress(Address.Function(0)))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/reference/"
    }
}
