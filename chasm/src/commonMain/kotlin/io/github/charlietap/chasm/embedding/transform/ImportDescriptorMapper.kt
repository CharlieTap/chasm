package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.embedding.shapes.ExternalType
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.TagType
import io.github.charlietap.chasm.ast.type.FunctionType as InternalFunctionType
import io.github.charlietap.chasm.ast.type.GlobalType as InternalGlobalType
import io.github.charlietap.chasm.ast.type.MemoryType as InternalMemoryType
import io.github.charlietap.chasm.ast.type.TableType as InternalTableType
import io.github.charlietap.chasm.ast.type.TagType as InternalTagType

internal class ImportDescriptorMapper(
    private val functionTypeMapper: BidirectionalMapper<FunctionType, InternalFunctionType> = FunctionTypeMapper.instance,
    private val globalTypeMapper: BidirectionalMapper<GlobalType, InternalGlobalType> = GlobalTypeMapper.instance,
    private val memoryTypeMapper: BidirectionalMapper<MemoryType, InternalMemoryType> = MemoryTypeMapper.instance,
    private val tableTypeMapper: BidirectionalMapper<TableType, InternalTableType> = TableTypeMapper.instance,
    private val tagTypeMapper: BidirectionalMapper<TagType, InternalTagType> = TagTypeMapper.instance,
) : Mapper<Import.Descriptor, ExternalType> {

    override fun map(input: Import.Descriptor): ExternalType {
        return when (input) {
            is Import.Descriptor.Function -> {
                val type = functionTypeMapper.bimap(input.type)
                ExternalType.Function(type)
            }
            is Import.Descriptor.Global -> {
                val type = globalTypeMapper.bimap(input.type)
                ExternalType.Global(type)
            }
            is Import.Descriptor.Memory -> {
                val type = memoryTypeMapper.bimap(input.type)
                ExternalType.Memory(type)
            }
            is Import.Descriptor.Table -> {
                val type = tableTypeMapper.bimap(input.type)
                ExternalType.Table(type)
            }
            is Import.Descriptor.Tag -> {
                val type = tagTypeMapper.bimap(input.type)
                ExternalType.Tag(type)
            }
        }
    }

    companion object {
        val instance = ImportDescriptorMapper()
    }
}
