package io.github.charlietap.chasm.decoder.wasm.decoder.section.index

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryIndexDecoderTest {

    @Test
    fun `can decode an encoded index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(TypeIndex(32u))
        val actual = BinaryIndexDecoder(reader, ::TypeIndex)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded type index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(TypeIndex(32u))
        val actual = BinaryTypeIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded local index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.LocalIndex(32u))
        val actual = BinaryLocalIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded global index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.GlobalIndex(32u))
        val actual = BinaryGlobalIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded table index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.TableIndex(32u))
        val actual = BinaryTableIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded element index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.ElementIndex(32u))
        val actual = BinaryElementIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded label index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.LabelIndex(32u))
        val actual = BinaryLabelIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded function index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.FunctionIndex(32u))
        val actual = BinaryFunctionIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded data index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.DataIndex(32u))
        val actual = BinaryDataIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an encoded memory index`() {

        val intReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(32u)
        }
        val reader = FakeWasmBinaryReader(fakeUIntReader = intReader)

        val expected = Ok(Index.MemoryIndex(32u))
        val actual = BinaryMemoryIndexDecoder(reader)

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)

        val actual = BinaryIndexDecoder(reader, ::TypeIndex)

        assertEquals(err, actual)
    }
}
