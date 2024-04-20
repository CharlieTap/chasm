package io.github.charlietap.chasm.integration.control

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockTest {

    @Test
    fun `can the empty test form control test spec return a correct result`() {

        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "empty",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can the singular test form control test spec return a correct result`() {

        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "singular",
        )

        val expected = listOf(NumberValue.I32(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can the multi test form control test spec return a correct result`() {

        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "multi",
        )

        val expected = listOf(NumberValue.I32(8))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can the nested test form control test spec return a correct result`() {

        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can the deep test form control test spec return a correct result`() {

        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "deep",
        )

        val expected = listOf(NumberValue.I32(150))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-select-first return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-first",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-select-mid return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-mid",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-select-last return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-last",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-loop-first return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loop-first",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-loop-mid return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loop-mid",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-loop-last return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loop-last",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-if-condition execute successfully`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-condition",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-if-then return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-then",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-if-else return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-else",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-br_if-first return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_if-first",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-br_if-last return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_if-last",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-br_table-first return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_table-first",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-br_table-last return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_table-last",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-call_indirect-mid return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-mid",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-call_indirect-last return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-last",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-store-first execute successfully`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-store-first",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-store-last execute successfully`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-store-last",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-memory-grow-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-memory.grow-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-call-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-return-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-return-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-drop-operand execute successfully`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-drop-operand",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-br-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-local-set-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-local.set-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-local-tee-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-local.tee-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-global-set-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-global.set-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-load-operand return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-load-operand",
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-unary-operand return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-unary-operand",
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-binary-operand return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-binary-operand",
        )

        val expected = listOf(NumberValue.I32(12))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-test-operand return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-test-operand",
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-compare-operand return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-compare-operand",
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-binary-operands return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-binary-operands",
        )

        val expected = listOf(NumberValue.I32(12))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-compare-operands return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-compare-operands",
        )

        val expected = listOf(NumberValue.I32(0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can as-mixed-operands return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-mixed-operands",
        )

        val expected = listOf(NumberValue.I32(27))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can break-bare return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "break-bare",
        )

        val expected = listOf(NumberValue.I32(19))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can break-value return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "break-value",
        )

        val expected = listOf(NumberValue.I32(18))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can break-multi-value return the correct results`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "break-multi-value",
        )

        val expected = listOf(NumberValue.I32(18), NumberValue.I32(-18), NumberValue.I64(18))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can break-repeated return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "break-repeated",
        )

        val expected = listOf(NumberValue.I32(18))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can break-inner return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "break-inner",
        )

        val expected = listOf(NumberValue.I32(15))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can param return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "param",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can params return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "params",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can params-id return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "params-id",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can param-break return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "param-break",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can params-break return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "params-break",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can params-id-break return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "params-id-break",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can effects return the correct result`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "effects",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can type-use execute successfully`() {
        val result = testRunner(
            fileName = "block.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-use",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/control/"
    }
}
