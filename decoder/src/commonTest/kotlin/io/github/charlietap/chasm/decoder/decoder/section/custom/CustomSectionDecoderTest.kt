package io.github.charlietap.chasm.decoder.decoder.section.custom

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.context.scope.Scope
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.customSection
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.fixture.sectionSize
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.ast.module.nameData
import io.github.charlietap.chasm.fixture.ast.module.uninterpreted
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CustomSectionDecoderTest {

    @Test
    fun `can decode an encoded custom section`() {

        val sectionSize = sectionSize(14u)
        val nameValue = nameValue("custom")
        val sectionBytes = "section".encodeToByteArray().asUByteArray()
        val custom = uninterpreted(nameValue, sectionBytes)
        val expected = Ok(customSection(custom))

        val reader = FakeWasmBinaryReader(
            fakeUBytesReader = { bytesRequested ->
                assertEquals(7u, bytesRequested)
                Ok(sectionBytes)
            },
            fakePositionReader = sequenceOf(0u, 7u).iterator()::next,
        )
        val context = decoderContext(
            reader = reader,
            sectionSize = sectionSize,
        )

        val nameDataDecoder: Decoder<NameData> = {
            fail("NameData decoder should not be called in this scenario")
        }
        val nameScope: Scope<UInt> = { _, _ ->
            fail("NameScope should not be called in this scenario")
        }
        val nameValueDecoder: Decoder<NameValue> = {
            Ok(nameValue)
        }
        val actual = CustomSectionDecoder(context, nameDataDecoder, nameScope, nameValueDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a namedata section when config decodeNameSection is true`() {

        val sectionSize = sectionSize(7u)
        val nameValue = nameValue("name")
        val nameData = nameData(emptyList())
        val expected = Ok(customSection(nameData))

        val reader = FakeWasmBinaryReader(
            fakeUBytesReader = { bytesRequested ->
                assertEquals(0u, bytesRequested)
                Ok(ubyteArrayOf())
            },
            fakePositionReader = sequenceOf(0u, 3u).iterator()::next,
        )
        val config = moduleConfig(decodeNameSection = true)
        val context = decoderContext(
            reader = reader,
            config = config,
            sectionSize = sectionSize,
        )

        val nameDataDecoder: Decoder<NameData> = {
            Ok(nameData)
        }
        val nameScope: Scope<UInt> = { _, _ ->
            Ok(context)
        }
        val nameValueDecoder: Decoder<NameValue> = {
            Ok(nameValue)
        }
        val actual = CustomSectionDecoder(context, nameDataDecoder, nameScope, nameValueDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns uninterpreted when config decodeNameSection is false`() {

        val sectionSize = sectionSize(7u)
        val nameValue = nameValue("name")
        val sectionBytes = ubyteArrayOf(1u, 2u, 3u, 4u)
        val custom = uninterpreted(nameValue, sectionBytes)
        val expected = Ok(customSection(custom))

        val reader = FakeWasmBinaryReader(
            fakeUBytesReader = { bytesRequested ->
                assertEquals(4u, bytesRequested)
                Ok(sectionBytes)
            },
            fakePositionReader = sequenceOf(0u, 3u).iterator()::next,
        )
        val config = moduleConfig(decodeNameSection = false)
        val context = decoderContext(
            reader = reader,
            config = config,
            sectionSize = sectionSize,
        )

        val nameDataDecoder: Decoder<NameData> = {
            fail("NameData decoder should not be called when decodeNameSection is false")
        }
        val nameScope: Scope<UInt> = { _, _ ->
            fail("NameScope should not be called when decodeNameSection is false")
        }
        val nameValueDecoder: Decoder<NameValue> = {
            Ok(nameValue)
        }
        val actual = CustomSectionDecoder(context, nameDataDecoder, nameScope, nameValueDecoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `can catch io exceptions from the wasm file reader`() {
        val expected = ioError()
        val reader = IOErrorWasmFileReader(expected)
        val context = decoderContext(reader)

        val actual = CustomSectionDecoder(context)

        assertEquals(expected, actual)
    }
}
