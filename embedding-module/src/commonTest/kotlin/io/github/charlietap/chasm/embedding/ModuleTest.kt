@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.DecodedModule
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ModuleTest {

    @Test
    fun `validating a decoded module keeps its declarations and module information`() = runTest {

        val module = module(EMPTY_MODULE, validate = false).expect("the module decodes")
        val decoded = assertIs<DecodedModule>(module._payload())

        val validated = validate(module).expect("the module validates")
        val payload = assertIs<DecodedModule>(validated._payload())

        assertTrue(payload.validated)
        assertSame(decoded.module, payload.module)
        assertSame(decoded.declarations, payload.declarations)
        assertSame(module.imports, validated.imports)
        assertSame(module.exports, validated.exports)
    }

    @Test
    fun `a module read from a source reader is validated before it is returned`() = runTest {

        val actual = module(FakeSourceReader(INVALID_MODULE))

        assertIs<ModuleError.Invalid>(assertIs<ChasmResult.Error<ModuleError>>(actual).error)
    }

    @Test
    fun `a validated module read from a source reader is marked validated`() = runTest {

        val module = module(FakeSourceReader(EMPTY_MODULE)).expect("the module decodes and validates")

        assertTrue(assertIs<DecodedModule>(module._payload()).validated)
    }

    @Test
    fun `an unvalidated module read from a source reader is not validated`() = runTest {

        val module = module(FakeSourceReader(INVALID_MODULE), validate = false).expect("the module decodes")

        assertFalse(assertIs<DecodedModule>(module._payload()).validated)
    }

    @Test
    fun `validate reports an invalid module read from a source reader`() = runTest {

        val module = module(FakeSourceReader(INVALID_MODULE), validate = false).expect("the module decodes")

        val actual = validate(module)

        assertIs<ModuleError.Invalid>(assertIs<ChasmResult.Error<ModuleError.Invalid>>(actual).error)
    }

    private companion object {
        val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        val INVALID_MODULE = EMPTY_MODULE + byteArrayOf(0x03, 0x02, 0x01, 0x00, 0x0a, 0x04, 0x01, 0x02, 0x00, 0x0b)
    }
}
