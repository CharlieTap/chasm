package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TagTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor
import io.github.charlietap.chasm.ir.module.Index.TagIndex as IRTagIndex
import io.github.charlietap.chasm.ir.module.Tag as IRTag

internal typealias TagFactory = (Tag, Substitution) -> IRTag

internal fun TagFactory(
    tag: Tag,
    substitution: Substitution,
): IRTag = TagFactory(
    tag = tag,
    substitution = substitution,
    tagIndexFactory = ::TagIndexFactory,
    tagTypeSubstitutor = ::TagTypeSubstitutor,
)

internal inline fun TagFactory(
    tag: Tag,
    substitution: Substitution,
    tagIndexFactory: IRFactory<TagIndex, IRTagIndex>,
    tagTypeSubstitutor: TypeSubstitutor<TagType>,
): IRTag {
    return IRTag(
        index = tagIndexFactory(tag.index),
        type = tagTypeSubstitutor(tag.type, substitution),
    )
}
