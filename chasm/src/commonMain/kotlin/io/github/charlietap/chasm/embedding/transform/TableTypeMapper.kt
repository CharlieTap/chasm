package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.ValueType
import io.github.charlietap.chasm.ast.type.Limits as InternalLimits
import io.github.charlietap.chasm.ast.type.TableType as InternalTableType

internal class TableTypeMapper(
    private val limitsMapper: BidirectionalMapper<Limits, InternalLimits> = LimitsMapper,
    private val referenceTypeMapper: BidirectionalMapper<ValueType.Reference, ReferenceType> = ReferenceTypeMapper.instance,
) : BidirectionalMapper<TableType, InternalTableType> {
    override fun map(input: TableType): InternalTableType {
        val limits = limitsMapper.map(input.limits)
        val referenceType = referenceTypeMapper.map(input.referenceType)
        return InternalTableType(referenceType, limits)
    }

    override fun bimap(input: InternalTableType): TableType {
        val limits = limitsMapper.bimap(input.limits)
        val referenceType = referenceTypeMapper.bimap(input.referenceType)
        return TableType(limits, referenceType)
    }

    companion object {
        val instance = TableTypeMapper()
    }
}
