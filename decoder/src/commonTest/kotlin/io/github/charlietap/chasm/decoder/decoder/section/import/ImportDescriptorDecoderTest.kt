package io.github.charlietap.chasm.decoder.decoder.section.import

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import io.github.charlietap.chasm.fixture.ast.module.functionImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.globalImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.memoryImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tagImportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ImportDescriptorDecoderTest {

    @Test
    fun `can decode a function import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_FUNCTION

        val recursiveType = functionRecursiveType()
        val functionType = DefinedTypeFactory(listOf(recursiveType))
        val expected = Ok(functionImportDescriptor(functionType.first()))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(
            reader = reader,
            types = mutableListOf(
                type(
                    recursiveType = recursiveType,
                ),
            ),
        )

        val typeIndex = typeIndex(0u)
        val typeIndexDecoder: Decoder<Index.TypeIndex> = {
            Ok(typeIndex)
        }

        val actual = ImportDescriptorDecoder(
            context = context,
            globalTypeDecoder = neverGlobalTypeDecoder,
            memTypeDecoder = neverMemoryTypeDecoder,
            tableTypeDecoder = neverTableTypeDecoder,
            tagTypeDecoder = neverTagTypeDecoder,
            typeIndexDecoder = typeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a table import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_TABLE

        val tableType = tableType(refNullReferenceType(AbstractHeapType.Func), Limits(117u))
        val expected = Ok(tableImportDescriptor(tableType))

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
            tagTypeDecoder = neverTagTypeDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a memory import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_MEMORY

        val memoryType = memoryType(limits(117u))
        val expected = Ok(memoryImportDescriptor(memoryType))

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
            tagTypeDecoder = neverTagTypeDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a global import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_GLOBAL

        val globalType = globalType()
        val expected = Ok(globalImportDescriptor(globalType))

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
            tagTypeDecoder = neverTagTypeDecoder,
            typeIndexDecoder = neverTypeIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a tag import descriptor`() {

        val descriptor = IMPORT_DESCRIPTOR_TYPE_TAG

        val tagType = tagType()
        val expected = Ok(tagImportDescriptor(tagType))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val tagTypeDecoder: Decoder<TagType> = { _ ->
            Ok(tagType)
        }

        val actual = ImportDescriptorDecoder(
            context = context,
            globalTypeDecoder = neverGlobalTypeDecoder,
            memTypeDecoder = neverMemoryTypeDecoder,
            tableTypeDecoder = neverTableTypeDecoder,
            tagTypeDecoder = tagTypeDecoder,
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
        private val neverTagTypeDecoder: Decoder<TagType> = {
            fail("tag type decoder should not be called in this scenario")
        }
    }
}
