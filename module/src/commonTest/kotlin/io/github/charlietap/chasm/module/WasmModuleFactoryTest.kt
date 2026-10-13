package io.github.charlietap.chasm.module

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WasmModuleFactoryTest {

    private val config = ModuleConfig()

    @Test
    fun `a valid module is decoded and validated`() = runTest {
        val bytes = module(VALID_BODY, VALID_BODY)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertEquals(WasmModuleDecoder(config, bytes).toString(), actual.toString())
    }

    @Test
    fun `an invalid body is reported as the serial validator reports it`() = runTest {
        val bytes = module(VALID_BODY, INVALID_BODY)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertTrue(actual.error() is ModuleFactoryError.Invalid)
        assertEquals(serialError(bytes), actual.error().toString())
    }

    @Test
    fun `a malformed body is reported as malformed`() = runTest {
        val bytes = module(VALID_BODY, MALFORMED_BODY)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertTrue(actual.error() is ModuleFactoryError.Malformed)
        assertEquals(serialError(bytes), actual.error().toString())
    }

    @Test
    fun `a malformed body takes precedence over an earlier invalid one`() = runTest {
        val bytes = module(INVALID_BODY, MALFORMED_BODY)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertTrue(actual.error() is ModuleFactoryError.Malformed)
    }

    @Test
    fun `a binary without a readable layout is reported as malformed`() = runTest {
        val bytes = byteArrayOf(0x00, 0x61, 0x73)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertTrue(actual.error() is ModuleFactoryError.Malformed)
    }

    @Test
    fun `a large module is decoded and validated`() = runTest {
        val bytes = module(*Array(LARGE_BODY_COUNT) { LARGE_BODY })

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertEquals(WasmModuleDecoder(config, bytes).toString(), actual.toString())
    }

    @Test
    fun `the first invalid body is reported whichever worker finds it`() = runTest {
        val bodies = Array(LARGE_BODY_COUNT) { LARGE_BODY }
        bodies[LARGE_BODY_COUNT / 2] = INVALID_BODY
        bodies[LARGE_BODY_COUNT - 1] = UNBALANCED_BODY
        val bytes = module(*bodies)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor(reverse = true))

        assertTrue(actual.error() is ModuleFactoryError.Invalid)
        assertEquals(serialError(bytes), actual.error().toString())
    }

    @Test
    fun `a malformed body is reported from a large invalid module`() = runTest {
        val bodies = Array(LARGE_BODY_COUNT) { LARGE_BODY }
        bodies[1] = INVALID_BODY
        bodies[LARGE_BODY_COUNT - 1] = MALFORMED_BODY
        val bytes = module(*bodies)

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor(reverse = true))

        assertTrue(actual.error() is ModuleFactoryError.Malformed)
        assertEquals(serialError(bytes), actual.error().toString())
    }

    @Test
    fun `an error that depends on the data segments is the one the serial validator reports`() = runTest {
        // the body's ref.func is only declared by the offset of a data segment, which is itself invalid
        val bytes = module(
            REFERENCING_BODY,
            beforeCode = byteArrayOf(0x05, 0x03, 0x01, 0x00, 0x01),
            afterCode = byteArrayOf(0x0B, 0x07, 0x01, 0x00, 0xD2.toByte(), 0x00, 0x0B, 0x01, 0x00),
        )

        val actual = WasmModuleFactory(config, bytes, TestParallelTaskExecutor())

        assertTrue(actual.error() is ModuleFactoryError.Invalid)
        assertEquals(serialError(bytes), actual.error().toString())
    }

    private fun serialError(bytes: ByteArray): String = WasmModuleDecoder(config, bytes).fold(
        success = { module ->
            WasmModuleValidator(config, module).fold(
                success = { error("expected an error but the module is valid") },
                failure = { it.toString() },
            )
        },
        failure = { it.toString() },
    )

    private fun Result<Module, ModuleFactoryError>.error(): ModuleFactoryError = fold(
        success = { error("expected an error but got a module") },
        failure = { it },
    )

    /** A module of functions that all have type `[] -> [i32]`, one per body. */
    private fun module(
        vararg bodies: ByteArray,
        beforeCode: ByteArray = byteArrayOf(),
        afterCode: ByteArray = byteArrayOf(),
    ): ByteArray {
        val functions = leb(bodies.size) + ByteArray(bodies.size)
        var code = leb(bodies.size)
        val entries = ByteArray(bodies.sumOf { body -> leb(body.size).size + body.size })
        var position = 0
        for (body in bodies) {
            val size = leb(body.size)
            size.copyInto(entries, position)
            body.copyInto(entries, position + size.size)
            position += size.size + body.size
        }
        code += entries

        return HEADER +
            byteArrayOf(0x01, 0x05, 0x01, 0x60, 0x00, 0x01, 0x7F) +
            byteArrayOf(0x03) + leb(functions.size) + functions +
            beforeCode +
            byteArrayOf(0x0A) + leb(code.size) + code +
            afterCode
    }

    private fun leb(value: Int): ByteArray {
        var remaining = value
        var encoded = byteArrayOf()
        do {
            val byte = remaining and 0x7F
            remaining = remaining ushr 7
            encoded += (if (remaining != 0) byte or 0x80 else byte).toByte()
        } while (remaining != 0)
        return encoded
    }

    private class TestParallelTaskExecutor(
        private val reverse: Boolean = false,
    ) : ParallelTaskExecutor {

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {
            val scope = ParallelTaskScope {}
            val indices = if (reverse) tasks.indices.reversed() else tasks.indices
            val results = arrayOfNulls<Any?>(tasks.size)
            for (index in indices) results[index] = tasks[index](scope)
            @Suppress("UNCHECKED_CAST")
            return results.asList() as List<T>
        }
    }

    private companion object {
        val HEADER = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00)

        // no locals; i32.const 1; end
        val VALID_BODY = byteArrayOf(0x00, 0x41, 0x01, 0x0B)

        // no locals; end, leaving nothing for the i32 result
        val INVALID_BODY = byteArrayOf(0x00, 0x0B)

        // no locals; i32.const 1; i32.const 1; end, leaving one value too many
        val UNBALANCED_BODY = byteArrayOf(0x00, 0x41, 0x01, 0x41, 0x01, 0x0B)

        // no locals; ref.func 0; drop; i32.const 1; end
        val REFERENCING_BODY = byteArrayOf(0x00, 0xD2.toByte(), 0x00, 0x1A, 0x41, 0x01, 0x0B)

        // no locals; 0x06 is not an instruction; end
        val MALFORMED_BODY = byteArrayOf(0x00, 0x06, 0x0B)

        // no locals; forty times i32.const 1, drop; i32.const 1; end
        val LARGE_BODY = byteArrayOf(0x00) +
            ByteArray(120) { index -> byteArrayOf(0x41, 0x01, 0x1A)[index % 3] } +
            byteArrayOf(0x41, 0x01, 0x0B)

        // enough bodies to be shared between workers where the platform and its processors allow
        const val LARGE_BODY_COUNT = 4500
    }
}
