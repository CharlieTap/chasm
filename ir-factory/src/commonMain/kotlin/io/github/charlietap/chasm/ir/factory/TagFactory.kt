package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ir.module.Index.TagIndex as IRTagIndex
import io.github.charlietap.chasm.ir.module.Tag as IRTag

internal fun TagFactory(
    tag: Tag,
): IRTag = TagFactory(
    tag = tag,
    tagIndexFactory = ::TagIndexFactory,
)

internal inline fun TagFactory(
    tag: Tag,
    tagIndexFactory: IRFactory<TagIndex, IRTagIndex>,
): IRTag {
    return IRTag(
        index = tagIndexFactory(tag.index),
        type = tag.type,
    )
}
