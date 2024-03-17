package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.ioError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeUByteReader
import io.github.charlietap.chasm.decoder.wasm.reader.IOErrorWasmFileReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryExportDescriptorDecoderTest {

    @Test
    fun `can decode a function export descriptor`() {

        val descriptor = EXPORT_DESCRIPTOR_TYPE_FUNCTION

        val functionIndex = Index.FunctionIndex(117u)
        val expected = Ok(Export.Descriptor.Function(functionIndex))

        val reader = FakeUByteReader {
            Ok(descriptor)
        }

        val functionIndexDecoder: FunctionIndexDecoder = {
            Ok(functionIndex)
        }

        val actual = BinaryExportDescriptorDecoder(
            reader = reader,
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

        val tableIndexDecoder: TableIndexDecoder = { _ ->
            Ok(tableIndex)
        }

        val actual = BinaryExportDescriptorDecoder(
            reader = reader,
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

        val memoryIndexDecoder: MemoryIndexDecoder = { _ ->
            Ok(memoryIndex)
        }

        val actual = BinaryExportDescriptorDecoder(
            reader = reader,
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

        val globalIndexDecoder: GlobalIndexDecoder = { _ ->
            Ok(globalIndex)
        }

        val actual = BinaryExportDescriptorDecoder(
            reader = reader,
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

        val actual = BinaryExportDescriptorDecoder(reader)

        assertEquals(err, actual)
    }

    companion object {
        private val neverFunctionIndexDecoder: FunctionIndexDecoder = {
            fail("function index decoder should not be called in this scenario")
        }
        private val neverTableIndexDecoder: TableIndexDecoder = {
            fail("table index decoder should not be called in this scenario")
        }
        private val neverMemoryIndexDecoder: MemoryIndexDecoder = {
            fail("memory index decoder should not be called in this scenario")
        }
        private val neverGlobalIndexDecoder: GlobalIndexDecoder = {
            fail("global index decoder should not be called in this scenario")
        }
    }
}
