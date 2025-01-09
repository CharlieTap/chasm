package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.error.ModuleDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeExhaustedReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.section.ImportSection
import io.github.charlietap.chasm.decoder.section.Section
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.fixture.ast.module.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class WasmModuleDecoderTest {

    @Test
    fun `can decode a wasm module with no sections`() {

        val config = moduleConfig()
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
            config = config,
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

    @Test
    fun `malformed error is returned if section order is unexpected`() {

        val config = moduleConfig()
        val sourceReader = FakeSourceReader()
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = { Ok(0u) },
            fakeExhaustedReader = { Ok(false) },
        )
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

        val sectionTypeSequence = sequenceOf(SectionType.Import, SectionType.Type).iterator()
        val sectionTypeDecoder: Decoder<SectionType> = { _ ->
            Ok(sectionTypeSequence.next())
        }

        val sectionDecoder: Decoder<Section> = { _ ->
            Ok(ImportSection(emptyList()))
        }

        val scope: Scope<Pair<SectionSize, SectionType>> = { _, _ ->
            Ok(decoderContext())
        }

        val expected = Err(ModuleDecodeError.ModuleMalformed)

        val actual = WasmModuleDecoder(
            config = config,
            source = sourceReader,
            readerFactory = readerFactory,
            magicNumberValidator = magicNumberValidator,
            versionDecoder = versionDecoder,
            sectionTypeDecoder = sectionTypeDecoder,
            sectionDecoder = sectionDecoder,
            scope = scope,
        )

        assertEquals(expected, actual)
    }
}
