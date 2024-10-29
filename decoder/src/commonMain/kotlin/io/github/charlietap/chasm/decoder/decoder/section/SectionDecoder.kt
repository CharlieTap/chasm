package io.github.charlietap.chasm.decoder.decoder.section

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.code.CodeSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.custom.CustomSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.data.DataSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.datacount.DataCountSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.element.ElementSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.export.ExportSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.function.FunctionSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.global.GlobalSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.import.ImportSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.memory.MemorySectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.start.StartSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.table.TableSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.tag.TagSectionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.type.TypeSectionDecoder
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.ext.trackBytes
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
import io.github.charlietap.chasm.decoder.section.Section
import io.github.charlietap.chasm.decoder.section.SectionType
import io.github.charlietap.chasm.decoder.section.StartSection
import io.github.charlietap.chasm.decoder.section.TableSection
import io.github.charlietap.chasm.decoder.section.TagSection
import io.github.charlietap.chasm.decoder.section.TypeSection

internal fun SectionDecoder(
    context: DecoderContext,
): Result<Section, WasmDecodeError> =
    SectionDecoder(
        context = context,
        customSectionDecoder = ::CustomSectionDecoder,
        codeSectionDecoder = ::CodeSectionDecoder,
        dataSectionDecoder = ::DataSectionDecoder,
        dataCountSectionDecoder = ::DataCountSectionDecoder,
        elementSectionDecoder = ::ElementSectionDecoder,
        importSectionDecoder = ::ImportSectionDecoder,
        exportSectionDecoder = ::ExportSectionDecoder,
        functionSectionDecoder = ::FunctionSectionDecoder,
        globalSectionDecoder = ::GlobalSectionDecoder,
        memorySectionDecoder = ::MemorySectionDecoder,
        startSectionDecoder = ::StartSectionDecoder,
        tableSectionDecoder = ::TableSectionDecoder,
        tagSectionDecoder = ::TagSectionDecoder,
        typeSectionDecoder = ::TypeSectionDecoder,
    )

internal inline fun SectionDecoder(
    context: DecoderContext,
    crossinline customSectionDecoder: Decoder<CustomSection>,
    crossinline codeSectionDecoder: Decoder<CodeSection>,
    crossinline dataSectionDecoder: Decoder<DataSection>,
    crossinline dataCountSectionDecoder: Decoder<DataCountSection>,
    crossinline elementSectionDecoder: Decoder<ElementSection>,
    crossinline importSectionDecoder: Decoder<ImportSection>,
    crossinline exportSectionDecoder: Decoder<ExportSection>,
    crossinline functionSectionDecoder: Decoder<FunctionSection>,
    crossinline globalSectionDecoder: Decoder<GlobalSection>,
    crossinline memorySectionDecoder: Decoder<MemorySection>,
    crossinline startSectionDecoder: Decoder<StartSection>,
    crossinline tableSectionDecoder: Decoder<TableSection>,
    crossinline tagSectionDecoder: Decoder<TagSection>,
    crossinline typeSectionDecoder: Decoder<TypeSection>,
): Result<Section, WasmDecodeError> = binding {

    val (section, bytesConsumed) = context.reader.trackBytes {
        when (context.sectionType) {
            SectionType.Custom -> customSectionDecoder(context)
            SectionType.Type -> typeSectionDecoder(context)
            SectionType.Import -> importSectionDecoder(context)
            SectionType.Function -> functionSectionDecoder(context)
            SectionType.Table -> tableSectionDecoder(context)
            SectionType.Memory -> memorySectionDecoder(context)
            SectionType.Tag -> tagSectionDecoder(context)
            SectionType.Global -> globalSectionDecoder(context)
            SectionType.Export -> exportSectionDecoder(context)
            SectionType.Start -> startSectionDecoder(context)
            SectionType.Element -> elementSectionDecoder(context)
            SectionType.Code -> codeSectionDecoder(context)
            SectionType.Data -> dataSectionDecoder(context)
            SectionType.DataCount -> dataCountSectionDecoder(context)
        }.bind()
    }

    if (context.sectionSize.size != bytesConsumed) {
        Err(SectionDecodeError.SectionSizeMismatch).bind<Unit>()
    }

    section
}
