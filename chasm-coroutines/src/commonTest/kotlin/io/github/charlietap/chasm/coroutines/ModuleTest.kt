package io.github.charlietap.chasm.coroutines

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs

class ModuleTest {

    @Test
    fun `decodes and validates a valid module`() = runTest {
        val actual = module(VALID_MODULE, validate = true)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    @Test
    fun `reports an invalid module as a validation error`() = runTest {
        val actual = module(INVALID_MODULE, validate = true)

        assertIs<ChasmResult.Error<ChasmError.ModuleError>>(actual)
        assertIs<ChasmError.ValidationError>(actual.error)
    }

    @Test
    fun `reports a malformed module as a decode error`() = runTest {
        val actual = module(MALFORMED_MODULE, validate = true)

        assertIs<ChasmResult.Error<ChasmError.ModuleError>>(actual)
        assertIs<ChasmError.DecodeError>(actual.error)
    }

    @Test
    fun `decodes an invalid module when not asked to validate it`() = runTest {
        val actual = module(INVALID_MODULE, validate = false)

        assertIs<ChasmResult.Success<Module>>(actual)
    }

    private companion object {
        // header; one type [] -> [i32]; one function of that type
        val PRELUDE = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00) +
            byteArrayOf(0x01, 0x05, 0x01, 0x60, 0x00, 0x01, 0x7F) +
            byteArrayOf(0x03, 0x02, 0x01, 0x00)

        // body: no locals; i32.const 1; end
        val VALID_MODULE = PRELUDE + byteArrayOf(0x0A, 0x06, 0x01, 0x04, 0x00, 0x41, 0x01, 0x0B)

        // body: no locals; end, leaving nothing for the i32 result
        val INVALID_MODULE = PRELUDE + byteArrayOf(0x0A, 0x04, 0x01, 0x02, 0x00, 0x0B)

        // body: no locals; 0x06 is not an instruction; end
        val MALFORMED_MODULE = PRELUDE + byteArrayOf(0x0A, 0x05, 0x01, 0x03, 0x00, 0x06, 0x0B)
    }
}
