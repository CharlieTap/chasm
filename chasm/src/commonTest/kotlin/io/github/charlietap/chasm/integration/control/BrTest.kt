package io.github.charlietap.chasm.integration.control

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.integration.testRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class BrTest {

    @Test
    fun `can run type-i32 from test spec and return a correct result`() {

        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-i32",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-i64 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-i64",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-f64 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-f64",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-i32-i32 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-i32-i32",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-i64-i64 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-i64-i64",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-f32-f32 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-f32-f32",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-f64-f64 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-f64-f64",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-i32-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-i32-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-i64-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-i64-value",
        )

        val expected = listOf(NumberValue.I64(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-f32-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-f32-value",
        )

        val expected = listOf(NumberValue.F32(3.0f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-f64-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-f64-value",
        )

        val expected = listOf(NumberValue.F64(4.0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run type-f64-f64-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "type-f64-f64-value",
        )

        val expected = listOf(NumberValue.F64(4.0), NumberValue.F64(5.0))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-block-first from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-block-first",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-block-mid from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-block-mid",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-block-last from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-block-last",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-block-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-block-value",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-loop-first from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loop-first",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-loop-mid from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loop-mid",
        )

        val expected = listOf(NumberValue.I32(4))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-loop-last from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loop-last",
        )

        val expected = listOf(NumberValue.I32(5))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br-value",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br_if-cond from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_if-cond",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br_if-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_if-value",
        )

        val expected = listOf(NumberValue.I32(8))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br_if-value-cond from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_if-value-cond",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br_table-index from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_table-index",
        )

        val expected = listOf<ExecutionValue>()

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br_table-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_table-value",
        )

        val expected = listOf(NumberValue.I32(10))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-br_table-value-index from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-br_table-value-index",
        )

        val expected = listOf(NumberValue.I32(11))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-return-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-return-value",
        )

        val expected = listOf(NumberValue.I64(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-return-values from test spec and return correct results`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-return-values",
        )

        val expected = listOf(NumberValue.I32(2), NumberValue.I64(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-if-cond from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-cond",
        )

        val expected = listOf(NumberValue.I32(2))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-if-then with true condition from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-then",
            arguments = listOf(NumberValue.I32(1), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-if-then with false condition from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-then",
            arguments = listOf(NumberValue.I32(0), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(6))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-if-else with false condition from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-else",
            arguments = listOf(NumberValue.I32(0), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(4))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-if-else with true condition from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-if-else",
            arguments = listOf(NumberValue.I32(1), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(6))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-select-first with condition 0 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-first",
            arguments = listOf(NumberValue.I32(0), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(5))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-select-first with condition 1 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-first",
            arguments = listOf(NumberValue.I32(1), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(5))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-select-second with condition 0 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-second",
            arguments = listOf(NumberValue.I32(0), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(6))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-select-second with condition 1 from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-second",
            arguments = listOf(NumberValue.I32(1), NumberValue.I32(6)),
        )

        val expected = listOf(NumberValue.I32(6))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-select-cond from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-cond",
        )

        val expected = listOf(NumberValue.I32(7))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-select-all from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-select-all",
        )

        val expected = listOf(NumberValue.I32(8))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call-first from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call-first",
        )

        val expected = listOf(NumberValue.I32(12))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call-mid from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call-mid",
        )

        val expected = listOf(NumberValue.I32(13))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call-last from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call-last",
        )

        val expected = listOf(NumberValue.I32(14))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call-all from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call-all",
        )

        val expected = listOf(NumberValue.I32(15))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call_indirect-func from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-func",
        )

        val expected = listOf(NumberValue.I32(20))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call_indirect-first from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-first",
        )

        val expected = listOf(NumberValue.I32(21))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call_indirect-mid from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-mid",
        )

        val expected = listOf(NumberValue.I32(22))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call_indirect-last from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-last",
        )

        val expected = listOf(NumberValue.I32(23))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-call_indirect-all from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-call_indirect-all",
        )

        val expected = listOf(NumberValue.I32(24))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-local-set-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-local.set-value",
        )

        val expected = listOf(NumberValue.I32(17))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-local-tee-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-local.tee-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-global-set-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-global.set-value",
        )

        val expected = listOf(NumberValue.I32(1))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-load-address from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-load-address",
        )

        val expected = listOf(NumberValue.F32(1.7f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-loadN-address from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-loadN-address",
        )

        val expected = listOf(NumberValue.I64(30))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-store-address from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-store-address",
        )

        val expected = listOf(NumberValue.I32(30))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-store-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-store-value",
        )

        val expected = listOf(NumberValue.I32(31))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-store-both from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-store-both",
        )

        val expected = listOf(NumberValue.I32(32))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-storeN-address from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-storeN-address",
        )

        val expected = listOf(NumberValue.I32(32))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-storeN-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-storeN-value",
        )

        val expected = listOf(NumberValue.I32(33))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-storeN-both from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-storeN-both",
        )

        val expected = listOf(NumberValue.I32(34))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-unary-operand from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-unary-operand",
        )

        val expected = listOf(NumberValue.F32(3.4f))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-binary-left from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-binary-left",
        )

        val expected = listOf(NumberValue.I32(3))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-binary-right from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-binary-right",
        )

        val expected = listOf(NumberValue.I64(45))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-binary-both from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-binary-both",
        )

        val expected = listOf(NumberValue.I32(46))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-test-operand from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-test-operand",
        )

        val expected = listOf(NumberValue.I32(44))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-compare-left from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-compare-left",
        )

        val expected = listOf(NumberValue.I32(43))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-compare-right from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-compare-right",
        )

        val expected = listOf(NumberValue.I32(42))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-compare-both from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-compare-both",
        )

        val expected = listOf(NumberValue.I32(44))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-convert-operand from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-convert-operand",
        )

        val expected = listOf(NumberValue.I32(41))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run as-memory-grow-size from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "as-memory.grow-size",
        )

        val expected = listOf(NumberValue.I32(40))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run nested-block-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested-block-value",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run nested-br-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested-br-value",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run nested-br_if-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested-br_if-value",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run nested-br_if-value-cond from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested-br_if-value-cond",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run nested-br_table-value from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested-br_table-value",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    @Test
    fun `can run nested-br_table-value-index from test spec and return a correct result`() {
        val result = testRunner(
            fileName = "br.wasm",
            fileDirectory = FILE_DIR,
            functionName = "nested-br_table-value-index",
        )

        val expected = listOf(NumberValue.I32(9))

        assertEquals(ChasmResult.Success(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/control/"
    }
}
