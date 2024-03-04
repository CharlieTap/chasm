package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.section.index.BinaryTypeIndexDecoder
import io.github.charlietap.chasm.decoder.section.index.TypeIndexDecoder
import io.github.charlietap.chasm.decoder.type.global.BinaryGlobalTypeDecoder
import io.github.charlietap.chasm.decoder.type.global.GlobalTypeDecoder
import io.github.charlietap.chasm.decoder.type.memory.BinaryMemoryTypeDecoder
import io.github.charlietap.chasm.decoder.type.memory.MemoryTypeDecoder
import io.github.charlietap.chasm.decoder.type.table.BinaryTableTypeDecoder
import io.github.charlietap.chasm.decoder.type.table.TableTypeDecoder
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryImportDescriptorDecoder(
    reader: WasmBinaryReader,
): Result<Import.Descriptor, WasmDecodeError> =
    BinaryImportDescriptorDecoder(
        reader = reader,
        globalTypeDecoder = ::BinaryGlobalTypeDecoder,
        memTypeDecoder = ::BinaryMemoryTypeDecoder,
        tableTypeDecoder = ::BinaryTableTypeDecoder,
        typeIndexDecoder = ::BinaryTypeIndexDecoder,
    )

fun BinaryImportDescriptorDecoder(
    reader: WasmBinaryReader,
    globalTypeDecoder: GlobalTypeDecoder,
    memTypeDecoder: MemoryTypeDecoder,
    tableTypeDecoder: TableTypeDecoder,
    typeIndexDecoder: TypeIndexDecoder,
): Result<Import.Descriptor, WasmDecodeError> = binding {

    when (val descriptorType = reader.ubyte().bind()) {
        IMPORT_DESCRIPTOR_TYPE_FUNCTION -> Import.Descriptor.Function(typeIndexDecoder(reader).bind())
        IMPORT_DESCRIPTOR_TYPE_TABLE -> Import.Descriptor.Table(tableTypeDecoder(reader).bind())
        IMPORT_DESCRIPTOR_TYPE_MEMORY -> Import.Descriptor.Memory(memTypeDecoder(reader).bind())
        IMPORT_DESCRIPTOR_TYPE_GLOBAL -> Import.Descriptor.Global(globalTypeDecoder(reader).bind())
        else -> Err(SectionDecodeError.UnknownImportDescriptor(descriptorType)).bind<Import.Descriptor>()
    }
}

internal const val IMPORT_DESCRIPTOR_TYPE_FUNCTION: UByte = 0x00u
internal const val IMPORT_DESCRIPTOR_TYPE_TABLE: UByte = 0x01u
internal const val IMPORT_DESCRIPTOR_TYPE_MEMORY: UByte = 0x02u
internal const val IMPORT_DESCRIPTOR_TYPE_GLOBAL: UByte = 0x03u
