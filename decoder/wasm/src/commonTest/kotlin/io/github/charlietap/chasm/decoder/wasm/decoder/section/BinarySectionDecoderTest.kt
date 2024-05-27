package io.github.charlietap.chasm.decoder.wasm.decoder.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.section.code.CodeSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.custom.CustomSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.data.DataSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.datacount.DataCountSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.element.ElementSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.export.ExportSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.function.FunctionSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.global.GlobalSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.import.ImportSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.memory.MemorySectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.start.StartSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.table.TableSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.type.TypeSectionDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakePositionReader
import io.github.charlietap.chasm.decoder.wasm.section.CodeSection
import io.github.charlietap.chasm.decoder.wasm.section.CustomSection
import io.github.charlietap.chasm.decoder.wasm.section.DataCountSection
import io.github.charlietap.chasm.decoder.wasm.section.DataSection
import io.github.charlietap.chasm.decoder.wasm.section.ElementSection
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection
import io.github.charlietap.chasm.decoder.wasm.section.FunctionSection
import io.github.charlietap.chasm.decoder.wasm.section.GlobalSection
import io.github.charlietap.chasm.decoder.wasm.section.ImportSection
import io.github.charlietap.chasm.decoder.wasm.section.MemorySection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType
import io.github.charlietap.chasm.decoder.wasm.section.StartSection
import io.github.charlietap.chasm.decoder.wasm.section.TableSection
import io.github.charlietap.chasm.decoder.wasm.section.TypeSection
import kotlin.test.Test
import kotlin.test.assertEquals

class BinarySectionDecoderTest {

    @Test
    fun `delegates to custom section decoder when given custom section type`() {

        val type = SectionType.Custom
        val secSize = SectionSize(117u)

        val expectedSection = CustomSection(Custom(NameValue(""), ubyteArrayOf()))

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val customSectionDecoder = CustomSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(customSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to type section decoder when given type section type`() {

        val type = SectionType.Type
        val secSize = SectionSize(117u)

        val expectedSection = TypeSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val typeSectionDecoder = TypeSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(typeSectionDecoder = typeSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to function section decoder when given function section type`() {

        val type = SectionType.Function
        val secSize = SectionSize(117u)

        val expectedSection = FunctionSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val functionSectionDecoder = FunctionSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(functionSectionDecoder = functionSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to global section decoder when given global section type`() {

        val type = SectionType.Global
        val secSize = SectionSize(117u)

        val expectedSection = GlobalSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val globalSectionDecoder = GlobalSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(globalSectionDecoder = globalSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to memory section decoder when given memory section type`() {

        val type = SectionType.Memory
        val secSize = SectionSize(117u)

        val expectedSection = MemorySection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val memorySectionDecoder = MemorySectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(memorySectionDecoder = memorySectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to import section decoder when given memory section type`() {

        val type = SectionType.Import
        val secSize = SectionSize(117u)

        val expectedSection = ImportSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val importSectionDecoder = ImportSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(importSectionDecoder = importSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to table section decoder when given table section type`() {

        val type = SectionType.Table
        val secSize = SectionSize(117u)

        val expectedSection = TableSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val tableSectionDecoder = TableSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(tableSectionDecoder = tableSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to exports section decoder when given export section type`() {

        val type = SectionType.Export
        val secSize = SectionSize(117u)

        val expectedSection = ExportSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val exportSectionDecoder = ExportSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(exportSectionDecoder = exportSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to start section decoder when given start section type`() {

        val type = SectionType.Start
        val secSize = SectionSize(117u)

        val expectedSection = StartSection(StartFunction(Index.FunctionIndex(117u)))

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val startSectionDecoder = StartSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(startSectionDecoder = startSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to code section decoder when given code section type`() {

        val type = SectionType.Code
        val secSize = SectionSize(117u)

        val expectedSection = CodeSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val codeSectionDecoder = CodeSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(codeSectionDecoder = codeSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to data section decoder when given data section type`() {

        val type = SectionType.Data
        val secSize = SectionSize(117u)

        val expectedSection = DataSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val dataSectionDecoder = DataSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(dataSectionDecoder = dataSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to data count section decoder when given data count section type`() {

        val type = SectionType.DataCount
        val secSize = SectionSize(117u)

        val expectedSection = DataCountSection(117u)

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val dataCountSectionDecoder = DataCountSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(dataCountSectionDecoder = dataCountSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to element section decoder when given element section type`() {

        val type = SectionType.Element
        val secSize = SectionSize(117u)

        val expectedSection = ElementSection(emptyList())

        val positions = sequenceOf(0u, secSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }

        val elementSectionDecoder = ElementSectionDecoder { _, size ->
            assertEquals(secSize, size)
            Ok(expectedSection)
        }

        val decoder = BinarySectionDecoder(elementSectionDecoder = elementSectionDecoder)

        val actual = decoder(reader, type, secSize)

        assertEquals(Ok(expectedSection), actual)
    }
}
