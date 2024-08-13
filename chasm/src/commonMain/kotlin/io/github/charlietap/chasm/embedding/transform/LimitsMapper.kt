package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.ast.type.Limits as InternalLimits

internal object LimitsMapper : BidirectionalMapper<Limits, InternalLimits> {
    override fun map(input: Limits): InternalLimits = InternalLimits(input.min, input.max)

    override fun bimap(input: InternalLimits): Limits = Limits(input.min, input.max)
}
