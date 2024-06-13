package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.type.ReferenceType

internal interface ElementSegmentContext {
    val elementSegmentType: ReferenceType?
}

internal data class ElementSegmentContextImpl(
    override val elementSegmentType: ReferenceType? = null,
) : ElementSegmentContext
