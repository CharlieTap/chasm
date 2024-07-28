package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.decoder.decoder.section.index.DataIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.ElementIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.FieldIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.IndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.LabelIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.LocalIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexDecoderTest {

    @Test
    fun `can decode an encoded index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(TypeIndex(32u))
        val actual = IndexDecoder(context, ::TypeIndex)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded type index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(TypeIndex(32u))
        val actual = TypeIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded local index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.LocalIndex(32u))
        val actual = LocalIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded global index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.GlobalIndex(32u))
        val actual = GlobalIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded table index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.TableIndex(32u))
        val actual = TableIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded element index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.ElementIndex(32u))
        val actual = ElementIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded label index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.LabelIndex(32u))
        val actual = LabelIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded function index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.FunctionIndex(32u))
        val actual = FunctionIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded data index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.DataIndex(32u))
        val actual = DataIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded memory index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.MemoryIndex(32u))
        val actual = MemoryIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded field index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)
        val context = decoderContext(reader)

        val expected = Ok(Index.FieldIndex(32u))
        val actual = FieldIndexDecoder(context)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = IndexDecoder(context, ::TypeIndex)

        assertEquals(err, actual)
    }
}
