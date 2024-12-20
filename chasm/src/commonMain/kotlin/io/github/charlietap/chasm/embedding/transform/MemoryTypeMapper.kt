package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.SharedStatus
import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.ast.type.Limits as InternalLimits
import io.github.charlietap.chasm.ast.type.MemoryType as InternalMemoryType

internal class MemoryTypeMapper(
    private val limitsMapper: BidirectionalMapper<Limits, InternalLimits> = LimitsMapper,
) : BidirectionalMapper<MemoryType, InternalMemoryType> {
    override fun map(input: MemoryType): InternalMemoryType {
        val status = if (input.shared) SharedStatus.Shared else SharedStatus.Unshared
        return InternalMemoryType(limitsMapper.map(input.limits), status)
    }

    override fun bimap(input: InternalMemoryType): MemoryType {
        return MemoryType(limitsMapper.bimap(input.limits), input.shared == SharedStatus.Shared)
    }

    companion object {
        val instance = MemoryTypeMapper()
    }
}
