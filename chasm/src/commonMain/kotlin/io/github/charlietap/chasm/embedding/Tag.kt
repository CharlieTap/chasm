package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.shapes.TagType
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.embedding.transform.TagTypeMapper
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ast.type.TagType as InternalTagType

fun tag(
    store: Store,
    type: TagType,
): Tag = tag(
    store = store,
    type = type,
    allocator = ::TagAllocator,
    tagTypeMapper = TagTypeMapper.instance,
)

internal fun tag(
    store: Store,
    type: TagType,
    allocator: TagAllocator,
    tagTypeMapper: Mapper<TagType, InternalTagType>,
): Tag {
    val tagType = tagTypeMapper.map(type)
    return Tag(ExternalValue.Tag(allocator(store.store, tagType)))
}
