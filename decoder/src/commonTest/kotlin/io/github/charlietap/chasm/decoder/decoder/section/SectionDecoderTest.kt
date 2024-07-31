package io.github.charlietap.chasm.decoder.decoder.section

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakePositionReader
import io.github.charlietap.chasm.decoder.section.CodeSection
import io.github.charlietap.chasm.decoder.section.CustomSection
import io.github.charlietap.chasm.decoder.section.DataCountSection
import io.github.charlietap.chasm.decoder.section.DataSection
import io.github.charlietap.chasm.decoder.section.ElementSection
import io.github.charlietap.chasm.decoder.section.ExportSection
import io.github.charlietap.chasm.decoder.section.FunctionSection
import io.github.charlietap.chasm.decoder.section.GlobalSection
import io.github.charlietap.chasm.decoder.section.ImportSection
import io.github.charlietap.chasm.decoder.section.MemorySection
import io.github.charlietap.chasm.decoder.section.SectionSize
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.decoder.section.StartSection
import io.github.charlietap.chasm.decoder.section.TableSection
import io.github.charlietap.chasm.decoder.section.TypeSection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class SectionDecoderTest {

    @Test
    fun `delegates to custom section decoder when given custom section type`() {

        val sectionType = SectionType.Custom
        val sectionSize = SectionSize(117u)

        val expectedSection = CustomSection(Custom(NameValue(""), ubyteArrayOf()))

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val customSectionDecoder: Decoder<CustomSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to type section decoder when given type section type`() {

        val sectionType = SectionType.Type
        val sectionSize = SectionSize(117u)

        val expectedSection = TypeSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val typeSectionDecoder: Decoder<TypeSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to function section decoder when given function section type`() {

        val sectionType = SectionType.Function
        val sectionSize = SectionSize(117u)

        val expectedSection = FunctionSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val functionSectionDecoder: Decoder<FunctionSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to global section decoder when given global section type`() {

        val sectionType = SectionType.Global
        val sectionSize = SectionSize(117u)

        val expectedSection = GlobalSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val globalSectionDecoder: Decoder<GlobalSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to memory section decoder when given memory section type`() {

        val sectionType = SectionType.Memory
        val sectionSize = SectionSize(117u)

        val expectedSection = MemorySection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val memorySectionDecoder: Decoder<MemorySection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to import section decoder when given memory section type`() {

        val sectionType = SectionType.Import
        val sectionSize = SectionSize(117u)

        val expectedSection = ImportSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val importSectionDecoder: Decoder<ImportSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to table section decoder when given table section type`() {

        val sectionType = SectionType.Table
        val sectionSize = SectionSize(117u)

        val expectedSection = TableSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val tableSectionDecoder: Decoder<TableSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to exports section decoder when given export section type`() {

        val sectionType = SectionType.Export
        val sectionSize = SectionSize(117u)

        val expectedSection = ExportSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val exportSectionDecoder: Decoder<ExportSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to start section decoder when given start section type`() {

        val sectionType = SectionType.Start
        val sectionSize = SectionSize(117u)

        val expectedSection = StartSection(StartFunction(Index.FunctionIndex(117u)))

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val startSectionDecoder: Decoder<StartSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to code section decoder when given code section type`() {

        val sectionType = SectionType.Code
        val sectionSize = SectionSize(117u)

        val expectedSection = CodeSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val codeSectionDecoder: Decoder<CodeSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to data section decoder when given data section type`() {

        val sectionType = SectionType.Data
        val sectionSize = SectionSize(117u)

        val expectedSection = DataSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val dataSectionDecoder: Decoder<DataSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to data count section decoder when given data count section type`() {

        val sectionType = SectionType.DataCount
        val sectionSize = SectionSize(117u)

        val expectedSection = DataCountSection(117u)

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val dataCountSectionDecoder: Decoder<DataCountSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    @Test
    fun `delegates to element section decoder when given element section type`() {

        val sectionType = SectionType.Element
        val sectionSize = SectionSize(117u)

        val expectedSection = ElementSection(emptyList())

        val positions = sequenceOf(0u, sectionSize.size).iterator()
        val reader = FakePositionReader {
            positions.next()
        }
        val context = decoderContext(
            reader,
            sectionSize,
            sectionType,
        )

        val elementSectionDecoder: Decoder<ElementSection> = { _context ->
            assertEquals(context, _context)
            Ok(expectedSection)
        }

        val actual = SectionDecoder(
            context = context,
            customSectionDecoder = customSectionDecoder,
            codeSectionDecoder = codeSectionDecoder,
            dataSectionDecoder = dataSectionDecoder,
            dataCountSectionDecoder = dataCountSectionDecoder,
            elementSectionDecoder = elementSectionDecoder,
            importSectionDecoder = importSectionDecoder,
            exportSectionDecoder = exportSectionDecoder,
            functionSectionDecoder = functionSectionDecoder,
            globalSectionDecoder = globalSectionDecoder,
            memorySectionDecoder = memorySectionDecoder,
            startSectionDecoder = startSectionDecoder,
            tableSectionDecoder = tableSectionDecoder,
            typeSectionDecoder = typeSectionDecoder,
        )

        assertEquals(Ok(expectedSection), actual)
    }

    companion object {
        private val customSectionDecoder: Decoder<CustomSection> = { _ ->
            fail("CustomSectionDecoder should not be called in this scenario")
        }
        private val codeSectionDecoder: Decoder<CodeSection> = { _ ->
            fail("CodeSectionDecoder should not be called in this scenario")
        }
        private val dataSectionDecoder: Decoder<DataSection> = { _ ->
            fail("DataSectionDecoder should not be called in this scenario")
        }
        private val dataCountSectionDecoder: Decoder<DataCountSection> = { _ ->
            fail("DataCountSectionDecoder should not be called in this scenario")
        }
        private val elementSectionDecoder: Decoder<ElementSection> = { _ ->
            fail("ElementSectionDecoder should not be called in this scenario")
        }
        private val importSectionDecoder: Decoder<ImportSection> = { _ ->
            fail("ImportSectionDecoder should not be called in this scenario")
        }
        private val exportSectionDecoder: Decoder<ExportSection> = { _ ->
            fail("ExportSectionDecoder should not be called in this scenario")
        }
        private val functionSectionDecoder: Decoder<FunctionSection> = { _ ->
            fail("FunctionSectionDecoder should not be called in this scenario")
        }
        private val globalSectionDecoder: Decoder<GlobalSection> = { _ ->
            fail("GlobalSectionDecoder should not be called in this scenario")
        }
        private val memorySectionDecoder: Decoder<MemorySection> = { _ ->
            fail("MemorySectionDecoder should not be called in this scenario")
        }
        private val startSectionDecoder: Decoder<StartSection> = { _ ->
            fail("StartSectionDecoder should not be called in this scenario")
        }
        private val tableSectionDecoder: Decoder<TableSection> = { _ ->
            fail("TableSectionDecoder should not be called in this scenario")
        }
        private val typeSectionDecoder: Decoder<TypeSection> = { _ ->
            fail("TypeSectionDecoder should not be called in this scenario")
        }
    }
}
