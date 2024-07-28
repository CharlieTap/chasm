package io.github.charlietap.chasm.decoder.decoder.section.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ExportDescriptorDecoder(
    context: DecoderContext,
): Result<Export.Descriptor, WasmDecodeError> =
    ExportDescriptorDecoder(
        context = context,
        functionIndexDecoder = ::FunctionIndexDecoder,
        globalIndexDecoder = ::GlobalIndexDecoder,
        memIndexDecoder = ::MemoryIndexDecoder,
        tableIndexDecoder = ::TableIndexDecoder,
    )

internal fun ExportDescriptorDecoder(
    context: DecoderContext,
    functionIndexDecoder: Decoder<Index.FunctionIndex>,
    globalIndexDecoder: Decoder<Index.GlobalIndex>,
    memIndexDecoder: Decoder<Index.MemoryIndex>,
    tableIndexDecoder: Decoder<Index.TableIndex>,
): Result<Export.Descriptor, WasmDecodeError> = binding {

    when (val descriptorType = context.reader.ubyte().bind()) {
        EXPORT_DESCRIPTOR_TYPE_FUNCTION -> Export.Descriptor.Function(functionIndexDecoder(context).bind())
        EXPORT_DESCRIPTOR_TYPE_TABLE -> Export.Descriptor.Table(tableIndexDecoder(context).bind())
        EXPORT_DESCRIPTOR_TYPE_MEMORY -> Export.Descriptor.Memory(memIndexDecoder(context).bind())
        EXPORT_DESCRIPTOR_TYPE_GLOBAL -> Export.Descriptor.Global(globalIndexDecoder(context).bind())
        else -> Err(SectionDecodeError.UnknownExportDescriptor(descriptorType)).bind<Export.Descriptor>()
    }
}

internal const val EXPORT_DESCRIPTOR_TYPE_FUNCTION: UByte = 0x00u
internal const val EXPORT_DESCRIPTOR_TYPE_TABLE: UByte = 0x01u
internal const val EXPORT_DESCRIPTOR_TYPE_MEMORY: UByte = 0x02u
internal const val EXPORT_DESCRIPTOR_TYPE_GLOBAL: UByte = 0x03u
