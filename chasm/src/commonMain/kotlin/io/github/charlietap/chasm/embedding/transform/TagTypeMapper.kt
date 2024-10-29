package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.TagType
import io.github.charlietap.chasm.ast.type.FunctionType as InternalFunctionType
import io.github.charlietap.chasm.ast.type.TagType as InternalTagType

internal class TagTypeMapper(
    private val functionTypeMapper: BidirectionalMapper<FunctionType, InternalFunctionType> = FunctionTypeMapper.instance,
) : BidirectionalMapper<TagType, InternalTagType> {

    override fun map(input: TagType): InternalTagType {
        val functionType = functionTypeMapper.map(input.type)
        return InternalTagType(InternalTagType.Attribute.Exception, functionType)
    }

    override fun bimap(input: InternalTagType): TagType {
        val functionType = functionTypeMapper.bimap(input.type)
        return TagType(TagType.Attribute.Exception, functionType)
    }

    companion object {
        val instance = TagTypeMapper()
    }
}
