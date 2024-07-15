package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ImportDescriptorDecoder(
    context: DecoderContext,
): Result<Import.Descriptor, WasmDecodeError> =
    ImportDescriptorDecoder(
        context = context,
        globalTypeDecoder = ::GlobalTypeDecoder,
        memTypeDecoder = ::MemoryTypeDecoder,
        tableTypeDecoder = ::TableTypeDecoder,
        typeIndexDecoder = ::TypeIndexDecoder,
    )

internal fun ImportDescriptorDecoder(
    context: DecoderContext,
    globalTypeDecoder: Decoder<GlobalType>,
    memTypeDecoder: Decoder<MemoryType>,
    tableTypeDecoder: Decoder<TableType>,
    typeIndexDecoder: Decoder<Index.TypeIndex>,
): Result<Import.Descriptor, WasmDecodeError> = binding {

    when (val descriptorType = context.reader.ubyte().bind()) {
        IMPORT_DESCRIPTOR_TYPE_FUNCTION -> Import.Descriptor.Function(typeIndexDecoder(context).bind())
        IMPORT_DESCRIPTOR_TYPE_TABLE -> Import.Descriptor.Table(tableTypeDecoder(context).bind())
        IMPORT_DESCRIPTOR_TYPE_MEMORY -> Import.Descriptor.Memory(memTypeDecoder(context).bind())
        IMPORT_DESCRIPTOR_TYPE_GLOBAL -> Import.Descriptor.Global(globalTypeDecoder(context).bind())
        else -> Err(SectionDecodeError.UnknownImportDescriptor(descriptorType)).bind<Import.Descriptor>()
    }
}

internal const val IMPORT_DESCRIPTOR_TYPE_FUNCTION: UByte = 0x00u
internal const val IMPORT_DESCRIPTOR_TYPE_TABLE: UByte = 0x01u
internal const val IMPORT_DESCRIPTOR_TYPE_MEMORY: UByte = 0x02u
internal const val IMPORT_DESCRIPTOR_TYPE_GLOBAL: UByte = 0x03u
