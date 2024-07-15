package io.github.charlietap.chasm.decoder.wasm.decoder.section

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
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
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.ext.trackBytes
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
import io.github.charlietap.chasm.decoder.wasm.section.Section
import io.github.charlietap.chasm.decoder.wasm.section.SectionType
import io.github.charlietap.chasm.decoder.wasm.section.StartSection
import io.github.charlietap.chasm.decoder.wasm.section.TableSection
import io.github.charlietap.chasm.decoder.wasm.section.TypeSection

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
        typeSectionDecoder = ::TypeSectionDecoder,
    )

internal fun SectionDecoder(
    context: DecoderContext,
    customSectionDecoder: Decoder<CustomSection>,
    codeSectionDecoder: Decoder<CodeSection>,
    dataSectionDecoder: Decoder<DataSection>,
    dataCountSectionDecoder: Decoder<DataCountSection>,
    elementSectionDecoder: Decoder<ElementSection>,
    importSectionDecoder: Decoder<ImportSection>,
    exportSectionDecoder: Decoder<ExportSection>,
    functionSectionDecoder: Decoder<FunctionSection>,
    globalSectionDecoder: Decoder<GlobalSection>,
    memorySectionDecoder: Decoder<MemorySection>,
    startSectionDecoder: Decoder<StartSection>,
    tableSectionDecoder: Decoder<TableSection>,
    typeSectionDecoder: Decoder<TypeSection>,
): Result<Section, WasmDecodeError> = binding {

    val (section, bytesConsumed) = context.reader.trackBytes {
        when (context.sectionType) {
            SectionType.Custom -> customSectionDecoder(context)
            SectionType.Type -> typeSectionDecoder(context)
            SectionType.Import -> importSectionDecoder(context)
            SectionType.Function -> functionSectionDecoder(context)
            SectionType.Table -> tableSectionDecoder(context)
            SectionType.Memory -> memorySectionDecoder(context)
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
