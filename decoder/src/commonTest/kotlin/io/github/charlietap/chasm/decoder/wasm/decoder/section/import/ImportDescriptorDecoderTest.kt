package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.import.IMPORT_DESCRIPTOR_TYPE_FUNCTION
import io.github.charlietap.chasm.decoder.decoder.section.import.IMPORT_DESCRIPTOR_TYPE_GLOBAL
import io.github.charlietap.chasm.decoder.decoder.section.import.IMPORT_DESCRIPTOR_TYPE_MEMORY
import io.github.charlietap.chasm.decoder.decoder.section.import.IMPORT_DESCRIPTOR_TYPE_TABLE
import io.github.charlietap.chasm.decoder.decoder.section.import.ImportDescriptorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.type.globalType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ImportDescriptorDecoderTest {

    @Test
    fun `can decode a function import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_FUNCTION

        val typeIndex = Index.TypeIndex(117u)
        val expected = Ok(Import.Descriptor.Function(typeIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val typeIndexDecoder: Decoder<Index.TypeIndex> = {
            Ok(typeIndex)
        }

        val actual = ImportDescriptorDecoder(
            context = context,
            globalTypeDecoder = neverGlobalTypeDecoder,
            memTypeDecoder = neverMemoryTypeDecoder,
            tableTypeDecoder = neverTableTypeDecoder,
            typeIndexDecoder = typeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a table import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_TABLE

        val tableType = TableType(ReferenceType.RefNull(AbstractHeapType.Func), Limits(117u))
        val expected = Ok(Import.Descriptor.Table(tableType))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val tableTypeDecoder: Decoder<TableType> = { _ ->
            Ok(tableType)
        }

        val actual = ImportDescriptorDecoder(
            context = context,
            globalTypeDecoder = neverGlobalTypeDecoder,
            memTypeDecoder = neverMemoryTypeDecoder,
            tableTypeDecoder = tableTypeDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a memory import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_MEMORY

        val memoryType = MemoryType(Limits(117u))
        val expected = Ok(Import.Descriptor.Memory(memoryType))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val memoryTypeDecoder: Decoder<MemoryType> = { _ ->
            Ok(memoryType)
        }

        val actual = ImportDescriptorDecoder(
            context = context,
            globalTypeDecoder = neverGlobalTypeDecoder,
            memTypeDecoder = memoryTypeDecoder,
            tableTypeDecoder = neverTableTypeDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a global import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_GLOBAL

        val globalType = globalType()
        val expected = Ok(Import.Descriptor.Global(globalType))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val globalTypeDecoder: Decoder<GlobalType> = { _ ->
            Ok(globalType)
        }

        val actual = ImportDescriptorDecoder(
            context = context,
            globalTypeDecoder = globalTypeDecoder,
            memTypeDecoder = neverMemoryTypeDecoder,
            tableTypeDecoder = neverTableTypeDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = ImportDescriptorDecoder(context)

        assertEquals(err, actual)
    }

    companion object {
        private val neverTypeIndexDecoder: Decoder<Index.TypeIndex> = {
            fail("type index decoder should not be called in this scenario")
        }
        private val neverTableTypeDecoder: Decoder<TableType> = {
            fail("table type decoder should not be called in this scenario")
        }
        private val neverMemoryTypeDecoder: Decoder<MemoryType> = {
            fail("memory type decoder should not be called in this scenario")
        }
        private val neverGlobalTypeDecoder: Decoder<GlobalType> = {
            fail("global type decoder should not be called in this scenario")
        }
    }
}
