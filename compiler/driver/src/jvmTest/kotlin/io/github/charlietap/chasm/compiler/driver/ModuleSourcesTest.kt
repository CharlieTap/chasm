package io.github.charlietap.chasm.compiler.driver

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.SequenceInputStream
import java.nio.file.Files
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

class ModuleSourcesTest {

    private val sources = ModuleSources(::WasmModuleDecoder, ::WasmModuleDecoder, ::WasmModuleValidator)

    @Test
    fun `a module file that cannot be read is unreadable`() {

        val missing = Files.createTempDirectory("sources").resolve("missing.wasm")

        assertIs<SourceFailure.Unreadable>(sources.read(missing, keepBinary = false, limits = MemoryLimits()).getError())
        assertIs<SourceFailure.Unreadable>(sources.read(missing, keepBinary = true, limits = MemoryLimits()).getError())
    }

    @Test
    fun `a stream that fails after the header is unreadable and is not read again`() {

        var opens = 0

        val source = sources.streamSource {
            opens++
            SequenceInputStream(ByteArrayInputStream(EMPTY_MODULE), FailingInputStream())
        }
        val decoded = decodeForCompilation(ModuleConfig(), source)

        assertIs<SourceFailure.Unreadable>(decoded.getError())
        assertEquals(1, opens)
    }

    @Test
    fun `bytes that are not a module are undecodable`() {

        val garbage = byteArrayOf(1, 2, 3)
        val file = Files.createTempFile("sources", ".wasm").also { path -> Files.write(path, garbage) }

        assertIs<SourceFailure.Undecodable>(sources.read(garbage, MemoryLimits()).getError())
        assertIs<SourceFailure.Undecodable>(sources.read(file, keepBinary = false, limits = MemoryLimits()).getError())
        Files.delete(file)
    }

    @Test
    fun `a module that fails validation is invalid`() {
        assertIs<SourceFailure.Invalid>(sources.read(START_OF_MISSING_FUNCTION, MemoryLimits()).getError())
    }

    @Test
    fun `a module whose memory starts above the limit is over the limit`() {

        val failure = sources.read(TWO_PAGE_MEMORY, MemoryLimits(linearMemory = 1.pages)).getError()

        assertIs<SourceFailure.OverLimit>(failure)
        assertEquals("memory 0 starts at 2 pages, above the limit of ${1.pages}", failure.message)
    }

    @Test
    fun `a module with a 64-bit memory or table is unsupported`() {

        val memory = sources.read(WIDE_MEMORY, MemoryLimits()).getError()
        val table = sources.read(WIDE_TABLE, MemoryLimits()).getError()

        assertEquals(SourceFailure.Unsupported("memory64 is unsupported"), memory)
        assertEquals(SourceFailure.Unsupported("memory64 is unsupported"), table)
    }

    @Test
    fun `a streamed module has the hash of its bytes and keeps no binary`() {

        val file = Files.createTempFile("sources", ".wasm").also { path -> Files.write(path, TWO_PAGE_MEMORY) }
        val expected = MessageDigest.getInstance("SHA-256").digest(TWO_PAGE_MEMORY).joinToString("") { byte -> "%02x".format(byte) }

        val streamed = read(sources.read(file, keepBinary = false, limits = MemoryLimits()))
        val kept = read(sources.read(file, keepBinary = true, limits = MemoryLimits()))
        val bytes = read(sources.read(TWO_PAGE_MEMORY, MemoryLimits()))
        Files.delete(file)

        assertEquals(expected, streamed.hash)
        assertEquals(expected, kept.hash)
        assertEquals(expected, bytes.hash)
        assertNull(streamed.binary)
        assertContentEquals(TWO_PAGE_MEMORY, kept.binary)
        assertContentEquals(TWO_PAGE_MEMORY, bytes.binary)
    }

    @Test
    fun `a module whose names do not decode is decoded again without them`() {

        val module = WasmModuleDecoder(ModuleConfig(), EMPTY_MODULE).getOrThrow { failure -> IllegalStateException(failure.toString()) }
        val names = ArrayList<Boolean>()

        val decoded = decodeForCompilation(ModuleConfig()) { config ->
            names += config.decodeNameSection
            if (config.decodeNameSection) Err(SourceFailure.Undecodable("bad names")) else Ok(module)
        }

        assertEquals(listOf(true, false), names)
        assertSame(module, decoded.get())
    }

    @Test
    fun `a binary that cannot be read is not read again`() {

        var reads = 0

        val decoded = decodeForCompilation(ModuleConfig()) {
            reads++
            Err(SourceFailure.Unreadable("cannot read"))
        }

        assertEquals(1, reads)
        assertIs<SourceFailure.Unreadable>(decoded.getError())
    }

    private fun read(result: Result<SourceModule, SourceFailure>): SourceModule {
        return result.getOrThrow { failure -> IllegalStateException(failure.message) }
    }

    private class FailingInputStream : InputStream() {

        override fun read(): Int {
            throw IOException("the disk went away")
        }
    }

    private companion object {
        val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        val WIDE_MEMORY = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00, 0x05, 0x03, 0x01, 0x04, 0x01)
        val WIDE_TABLE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00, 0x04, 0x04, 0x01, 0x70, 0x04, 0x01)
        val START_OF_MISSING_FUNCTION = EMPTY_MODULE + byteArrayOf(0x08, 0x01, 0x00)
        val TWO_PAGE_MEMORY = EMPTY_MODULE + byteArrayOf(0x05, 0x03, 0x01, 0x00, 0x02)
    }
}
