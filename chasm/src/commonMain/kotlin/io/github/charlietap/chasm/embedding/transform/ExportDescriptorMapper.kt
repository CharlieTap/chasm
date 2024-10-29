package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.embedding.shapes.ExternalType
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.TagType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.ast.type.FunctionType as InternalFunctionType
import io.github.charlietap.chasm.ast.type.GlobalType as InternalGlobalType
import io.github.charlietap.chasm.ast.type.MemoryType as InternalMemoryType
import io.github.charlietap.chasm.ast.type.TableType as InternalTableType
import io.github.charlietap.chasm.ast.type.TagType as InternalTagType

internal class ExportDescriptorMapper(
    private val module: Module,
    private val functionTypeMapper: BidirectionalMapper<FunctionType, InternalFunctionType> = FunctionTypeMapper.instance,
    private val globalTypeMapper: BidirectionalMapper<GlobalType, InternalGlobalType> = GlobalTypeMapper.instance,
    private val memoryTypeMapper: BidirectionalMapper<MemoryType, InternalMemoryType> = MemoryTypeMapper.instance,
    private val tableTypeMapper: BidirectionalMapper<TableType, InternalTableType> = TableTypeMapper.instance,
    private val tagTypeMapper: BidirectionalMapper<TagType, InternalTagType> = TagTypeMapper.instance,
) : Mapper<Export.Descriptor, ExternalType> {

    override fun map(input: Export.Descriptor): ExternalType {
        return when (input) {
            is Export.Descriptor.Function -> {
                val function = module.functions.first { function ->
                    function.idx == input.functionIndex
                }
                val functionType = module.types[function.typeIndex.idx.toInt()].recursiveType.functionType()
                val type = functionTypeMapper.bimap(functionType!!)
                ExternalType.Function(type)
            }
            is Export.Descriptor.Global -> {
                val global = module.globals.first { global ->
                    global.idx == input.globalIndex
                }
                val type = globalTypeMapper.bimap(global.type)
                ExternalType.Global(type)
            }
            is Export.Descriptor.Memory -> {
                val memory = module.memories.first { memory ->
                    memory.idx == input.memoryIndex
                }
                val type = memoryTypeMapper.bimap(memory.type)
                ExternalType.Memory(type)
            }
            is Export.Descriptor.Table -> {
                val table = module.tables.first { table ->
                    table.idx == input.tableIndex
                }
                val type = tableTypeMapper.bimap(table.type)
                ExternalType.Table(type)
            }
            is Export.Descriptor.Tag -> {
                val tag = module.tags.first { tag ->
                    tag.index == input.tagIndex
                }
                val type = tagTypeMapper.bimap(tag.type)
                ExternalType.Tag(type)
            }
        }
    }
}
