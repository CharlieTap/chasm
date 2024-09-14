package io.github.charlietap.chasm.integration.gc

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.embedding.fixture.publicI32 as i32

class RefCastTest {

    @Test
    fun `can run the ref_cast_non_null 1 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_non_null",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_non_null 2 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_non_null",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_non_null 3 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_non_null",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_non_null 4 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_non_null",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 0 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 1 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 2 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 3 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 4 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 5 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 6 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_null 7 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_null",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 0 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 1 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 2 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 3 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 4 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 5 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 6 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_cast_i31 7 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_i31",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 0 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 1 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 2 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_struct 3 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 4 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 5 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 6 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_struct 7 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_struct",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 0 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(0)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 1 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(1)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 2 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(2)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 3 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(3)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the ref_array 4 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(4)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 5 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(5)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 6 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(6)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the ref_array 7 test from the ref_cast_1 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_1.wasm",
            fileDirectory = FILE_DIR,
            functionName = "ref_cast_array",
            arguments = listOf(i32(7)),
            setupFunctions = listOf("init" to listOf(Value.Reference.Extern(Value.Reference.Host(0)))),
        )

        val expected = ChasmError.ExecutionError(InvocationError.Trap.TrapEncountered.toString())

        assertEquals(ChasmResult.Error(expected), result)
    }

    @Test
    fun `can run the test-sub test from the ref_cast_2 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-sub",
            arguments = listOf(),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run the test-canon test from the ref_cast_2 gc test spec return a correct result`() {

        val result = testRunner(
            fileName = "ref_cast_2.wasm",
            fileDirectory = FILE_DIR,
            functionName = "test-canon",
            arguments = listOf(),
        )

        val expected = listOf<Value>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/gc/"
    }
}
