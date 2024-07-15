package io.github.charlietap.chasm.decoder.wasm

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.wasm.context.scope.Scope
import io.github.charlietap.chasm.decoder.wasm.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.wasm.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.wasm.reader.FakeExhaustedReader
import io.github.charlietap.chasm.decoder.wasm.section.Section
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType
import io.github.charlietap.chasm.fixture.module.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class WasmModuleDecoderTest {

    @Test
    fun `can decode a wasm module with no sections`() {

        val sourceReader = FakeSourceReader()
        val reader = FakeExhaustedReader { Ok(true) }
        val version = Version.One

        val readerFactory: BinaryReaderFactory = { _sourceReader ->
            assertEquals(sourceReader, _sourceReader)
            reader
        }

        val magicNumberValidator: MagicNumberValidator = { _reader ->
            assertEquals(reader, _reader)
            Ok(Unit)
        }

        val versionDecoder: Decoder<Version> = { context ->
            assertEquals(reader, context.reader)
            Ok(version)
        }

        val sectionTypeDecoder: Decoder<SectionType> = { _ ->
            fail("Section type decoder should not be called in this scenario")
        }

        val sectionDecoder: Decoder<Section> = { _ ->
            fail("Section decoder should not be called in this scenario")
        }

        val scope: Scope<Pair<SectionSize, SectionType>> = { _, _ ->
            fail("Scope should not be called in this scenario")
        }

        val expected = module(
            version = version,
        )

        val actual = WasmModuleDecoder(
            source = sourceReader,
            readerFactory = readerFactory,
            magicNumberValidator = magicNumberValidator,
            versionDecoder = versionDecoder,
            sectionTypeDecoder = sectionTypeDecoder,
            sectionDecoder = sectionDecoder,
            scope = scope,
        )

        assertEquals(Ok(expected), actual)
    }
}
