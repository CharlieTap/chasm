package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryFunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryGlobalIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryMemoryIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.BinaryTableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.FunctionIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.GlobalIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.MemoryIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.index.TableIndexDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryExportDescriptorDecoder(
    reader: WasmBinaryReader,
): Result<Export.Descriptor, WasmDecodeError> =
    BinaryExportDescriptorDecoder(
        reader = reader,
        functionIndexDecoder = ::BinaryFunctionIndexDecoder,
        globalIndexDecoder = ::BinaryGlobalIndexDecoder,
        memIndexDecoder = ::BinaryMemoryIndexDecoder,
        tableIndexDecoder = ::BinaryTableIndexDecoder,
    )

internal fun BinaryExportDescriptorDecoder(
    reader: WasmBinaryReader,
    functionIndexDecoder: FunctionIndexDecoder,
    globalIndexDecoder: GlobalIndexDecoder,
    memIndexDecoder: MemoryIndexDecoder,
    tableIndexDecoder: TableIndexDecoder,
): Result<Export.Descriptor, WasmDecodeError> = binding {

    when (val descriptorType = reader.ubyte().bind()) {
        EXPORT_DESCRIPTOR_TYPE_FUNCTION -> Export.Descriptor.Function(functionIndexDecoder(reader).bind())
        EXPORT_DESCRIPTOR_TYPE_TABLE -> Export.Descriptor.Table(tableIndexDecoder(reader).bind())
        EXPORT_DESCRIPTOR_TYPE_MEMORY -> Export.Descriptor.Memory(memIndexDecoder(reader).bind())
        EXPORT_DESCRIPTOR_TYPE_GLOBAL -> Export.Descriptor.Global(globalIndexDecoder(reader).bind())
        else -> Err(SectionDecodeError.UnknownExportDescriptor(descriptorType)).bind<Export.Descriptor>()
    }
}

internal const val EXPORT_DESCRIPTOR_TYPE_FUNCTION: UByte = 0x00u
internal const val EXPORT_DESCRIPTOR_TYPE_TABLE: UByte = 0x01u
internal const val EXPORT_DESCRIPTOR_TYPE_MEMORY: UByte = 0x02u
internal const val EXPORT_DESCRIPTOR_TYPE_GLOBAL: UByte = 0x03u
