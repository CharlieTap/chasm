package io.github.charlietap.chasm.decoder.decoder.section.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.tag.TagTypeDecoder
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

internal fun ImportDescriptorDecoder(
    context: DecoderContext,
): Result<Import.Descriptor, WasmDecodeError> =
    ImportDescriptorDecoder(
        context = context,
        globalTypeDecoder = ::GlobalTypeDecoder,
        memTypeDecoder = ::MemoryTypeDecoder,
        tableTypeDecoder = ::TableTypeDecoder,
        tagTypeDecoder = ::TagTypeDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
    )

internal inline fun ImportDescriptorDecoder(
    context: DecoderContext,
    crossinline globalTypeDecoder: Decoder<GlobalType>,
    crossinline memTypeDecoder: Decoder<MemoryType>,
    crossinline tableTypeDecoder: Decoder<TableType>,
    crossinline tagTypeDecoder: Decoder<TagType>,
    crossinline typeIndexDecoder: Decoder<Index.TypeIndex>,
): Result<Import.Descriptor, WasmDecodeError> = binding {

    when (val descriptorType = context.reader.ubyte().bind()) {
        IMPORT_DESCRIPTOR_TYPE_FUNCTION -> {
            val typeIndex = typeIndexDecoder(context).bind()
            val type = context.definedTypes
                .getOrNull(typeIndex.idx.toInt())
                ?: Err(SectionDecodeError.UnknownImportDescriptor(descriptorType)).bind()
            Import.Descriptor.Function(type)
        }
        IMPORT_DESCRIPTOR_TYPE_TABLE -> Import.Descriptor.Table(tableTypeDecoder(context).bind())
        IMPORT_DESCRIPTOR_TYPE_MEMORY -> Import.Descriptor.Memory(memTypeDecoder(context).bind())
        IMPORT_DESCRIPTOR_TYPE_GLOBAL -> Import.Descriptor.Global(globalTypeDecoder(context).bind())
        IMPORT_DESCRIPTOR_TYPE_TAG -> Import.Descriptor.Tag(tagTypeDecoder(context).bind())
        else -> Err(SectionDecodeError.UnknownImportDescriptor(descriptorType)).bind<Import.Descriptor>()
    }
}

internal const val IMPORT_DESCRIPTOR_TYPE_FUNCTION: UByte = 0x00u
internal const val IMPORT_DESCRIPTOR_TYPE_TABLE: UByte = 0x01u
internal const val IMPORT_DESCRIPTOR_TYPE_MEMORY: UByte = 0x02u
internal const val IMPORT_DESCRIPTOR_TYPE_GLOBAL: UByte = 0x03u
internal const val IMPORT_DESCRIPTOR_TYPE_TAG: UByte = 0x04u
