package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryImportDescriptorDecoderTest {

    @Test
    fun `can decode a function import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_FUNCTION

        val typeIndex = Index.TypeIndex(117u)
        val expected = Ok(Import.Descriptor.Function(typeIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }

        val typeIndexDecoder: TypeIndexDecoder = {
            Ok(typeIndex)
        }

        val actual = BinaryImportDescriptorDecoder(
            reader = reader,
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

        val tableType = TableType(ReferenceType.RefNull(HeapType.Func), Limits(117u))
        val expected = Ok(Import.Descriptor.Table(tableType))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }

        val tableTypeDecoder: TableTypeDecoder = { _ ->
            Ok(tableType)
        }

        val actual = BinaryImportDescriptorDecoder(
            reader = reader,
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

        val memoryTypeDecoder: MemoryTypeDecoder = { _ ->
            Ok(memoryType)
        }

        val actual = BinaryImportDescriptorDecoder(
            reader = reader,
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

        val globalType = GlobalType(ValueType.Number(NumberType.I32), GlobalType.Mutability.Var)
        val expected = Ok(Import.Descriptor.Global(globalType))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }

        val globalTypeDecoder: GlobalTypeDecoder = { _ ->
            Ok(globalType)
        }

        val actual = BinaryImportDescriptorDecoder(
            reader = reader,
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

        val actual = BinaryImportDescriptorDecoder(reader)

        assertEquals(err, actual)
    }

    companion object {
        private val neverTypeIndexDecoder: TypeIndexDecoder = {
            fail("type index decoder should not be called in this scenario")
        }
        private val neverTableTypeDecoder: TableTypeDecoder = {
            fail("table type decoder should not be called in this scenario")
        }
        private val neverMemoryTypeDecoder: MemoryTypeDecoder = {
            fail("memory type decoder should not be called in this scenario")
        }
        private val neverGlobalTypeDecoder: GlobalTypeDecoder = {
            fail("global type decoder should not be called in this scenario")
        }
    }
}
