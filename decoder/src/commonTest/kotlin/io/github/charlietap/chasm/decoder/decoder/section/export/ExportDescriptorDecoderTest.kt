package io.github.charlietap.chasm.decoder.decoder.section.export

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.fixture.ioError
import io.github.charlietap.chasm.decoder.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ExportDescriptorDecoderTest {

    @Test
    fun `can decode a function export descriptor`() {

        val descriptor = EXPORT_DESCRIPTOR_TYPE_FUNCTION

        val functionIndex = Index.FunctionIndex(117u)
        val expected = Ok(Export.Descriptor.Function(functionIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val functionIndexDecoder: Decoder<Index.FunctionIndex> = {
            Ok(functionIndex)
        }

        val actual = ExportDescriptorDecoder(
            context = context,
            functionIndexDecoder = functionIndexDecoder,
            globalIndexDecoder = neverGlobalIndexDecoder,
            memIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a table export descriptor`() {

        val descriptor = EXPORT_DESCRIPTOR_TYPE_TABLE

        val tableIndex = Index.TableIndex(117u)
        val expected = Ok(Export.Descriptor.Table(tableIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val tableIndexDecoder: Decoder<Index.TableIndex> = { _ ->
            Ok(tableIndex)
        }

        val actual = ExportDescriptorDecoder(
            context = context,
            functionIndexDecoder = neverFunctionIndexDecoder,
            globalIndexDecoder = neverGlobalIndexDecoder,
            memIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = tableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a memory export descriptor`() {

        val descriptor = EXPORT_DESCRIPTOR_TYPE_MEMORY

        val memoryIndex = Index.MemoryIndex(117u)
        val expected = Ok(Export.Descriptor.Memory(memoryIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val memoryIndexDecoder: Decoder<Index.MemoryIndex> = { _ ->
            Ok(memoryIndex)
        }

        val actual = ExportDescriptorDecoder(
            context = context,
            functionIndexDecoder = neverFunctionIndexDecoder,
            globalIndexDecoder = neverGlobalIndexDecoder,
            memIndexDecoder = memoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode a global export descriptor`() {

        val descriptor = EXPORT_DESCRIPTOR_TYPE_GLOBAL

        val globalIndex = Index.GlobalIndex(117u)
        val expected = Ok(Export.Descriptor.Global(globalIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }
        val context = decoderContext(reader)

        val globalIndexDecoder: Decoder<Index.GlobalIndex> = { _ ->
            Ok(globalIndex)
        }

        val actual = ExportDescriptorDecoder(
            context = context,
            functionIndexDecoder = neverFunctionIndexDecoder,
            globalIndexDecoder = globalIndexDecoder,
            memIndexDecoder = neverMemoryIndexDecoder,
            tableIndexDecoder = neverTableIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns io error when read fails`() {

        val err = ioError()
        val reader = IOErrorWasmFileReader(err)
        val context = decoderContext(reader)

        val actual = ExportDescriptorDecoder(context)

        assertEquals(err, actual)
    }

    companion object {
        private val neverFunctionIndexDecoder: Decoder<Index.FunctionIndex> = {
            fail("function index decoder should not be called in this scenario")
        }
        private val neverTableIndexDecoder: Decoder<Index.TableIndex> = {
            fail("table index decoder should not be called in this scenario")
        }
        private val neverMemoryIndexDecoder: Decoder<Index.MemoryIndex> = {
            fail("memory index decoder should not be called in this scenario")
        }
        private val neverGlobalIndexDecoder: Decoder<Index.GlobalIndex> = {
            fail("global index decoder should not be called in this scenario")
        }
    }
}
