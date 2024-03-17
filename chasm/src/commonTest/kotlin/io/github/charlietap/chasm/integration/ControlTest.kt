package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ControlTest {

    // wasm-interp loops_and_labels.wasm -r loops_and_labels
    @Test
    fun `can run a wasm file with multiple control blocks and return a correct result`() {

        val result = testRunner(
            fileName = "loops_and_labels.wasm",
            fileDirectory = FILE_DIR,
        )

        val expected = listOf(NumberValue.I32(10))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp nested_function.wasm -r nested_function -a i32:9
    @Test
    fun `can run a wasm file with nested functions and a return a correct result`() {

        val result = testRunner(
            fileName = "nested_function.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(9)),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp nested_return.wasm -r nested_return -a i32:4
    @Test
    fun `can run a wasm file with nested return statements and return a correct result`() {

        val result = testRunner(
            fileName = "nested_return.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(4)),
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp --enable-tail-call return_call.wasm -r return_call -a i64:5
    @Test
    fun `can run a wasm file with the return call instruction and return a correct result`() {

        val result = testRunner(
            fileName = "return_call.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I64(5)),
        )

        val expected = listOf(NumberValue.I64(120))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp --enable-tail-call return_call_indirect.wasm -r return_call_indirect -a i64:5
    @Test
    fun `can run a wasm file with the return call indirect instruction and return a correct result`() {

        val result = testRunner(
            fileName = "return_call_indirect.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I64(5)),
        )

        val expected = listOf(NumberValue.I64(120))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp --enable-function-references call_ref.wasm -r call_ref -a i32:3
    @Test
    fun `can run a wasm file with the call ref instruction and return a correct result`() {

        val result = testRunner(
            fileName = "call_ref.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I32(3)),
        )

        val expected = listOf(NumberValue.I32(-9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp --enable-function-references return_call_ref.wasm -r return_call_ref -a i64:5
    @Test
    fun `can run a wasm file with the return call ref instruction and return a correct result`() {

        val result = testRunner(
            fileName = "return_call_ref.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(NumberValue.I64(5)),
        )

        val expected = listOf(NumberValue.I64(120))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp --enable-function-references br_on_null.wasm -r br_on_null
    @Test
    fun `can run a wasm file with the br_on_null instruction and return a correct result`() {

        val result = testRunner(
            fileName = "br_on_null.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    // wasm-interp --enable-function-references br_on_non_null.wasm -r br_on_non_null
    @Test
    fun `can run a wasm file with the br_on_non_null instruction and return a correct result`() {

        val result = testRunner(
            fileName = "br_on_non_null.wasm",
            fileDirectory = FILE_DIR,
            arguments = listOf(),
        )

        val expected = listOf(NumberValue.I32(-1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/control/"
    }
}
