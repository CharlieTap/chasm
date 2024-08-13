package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.ast.type.Limits as InternalLimits
import io.github.charlietap.chasm.ast.type.MemoryType as InternalMemoryType

internal class MemoryTypeMapper(
    private val limitsMapper: BidirectionalMapper<Limits, InternalLimits> = LimitsMapper,
) : BidirectionalMapper<MemoryType, InternalMemoryType> {
    override fun map(input: MemoryType): InternalMemoryType {
        return InternalMemoryType(limitsMapper.map(input.limits))
    }

    override fun bimap(input: InternalMemoryType): MemoryType {
        return MemoryType(limitsMapper.bimap(input.limits))
    }

    companion object {
        val instance = MemoryTypeMapper()
    }
}
