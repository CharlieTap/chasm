package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.ir.module.Index.TagIndex as IRTagIndex
import io.github.charlietap.chasm.ir.module.Tag as IRTag
import io.github.charlietap.chasm.ir.type.TagType as IRTagType

internal fun TagFactory(
    tag: Tag,
): IRTag = TagFactory(
    tag = tag,
    tagIndexFactory = ::TagIndexFactory,
    tagTypeFactory = ::TagTypeFactory,
)

internal inline fun TagFactory(
    tag: Tag,
    tagIndexFactory: IRFactory<TagIndex, IRTagIndex>,
    tagTypeFactory: IRFactory<TagType, IRTagType>,
): IRTag {
    return IRTag(
        index = tagIndexFactory(tag.index),
        type = tagTypeFactory(tag.type),
    )
}
