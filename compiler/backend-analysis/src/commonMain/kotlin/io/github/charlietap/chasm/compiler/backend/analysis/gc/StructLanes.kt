package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType

@InternalChasmApi
class StructLanes(
    private val plans: StoragePlans,
    private val types: List<DefinedType>,
    private val sizes: StructSizes,
) {

    val structs: Map<Int, StructStorage> = types.indices
        .filter { index -> types[index].subType().compositeType is CompositeType.Struct }
        .mapNotNull { index -> supported(index)?.let { storage -> index to storage } }
        .toMap()

    fun encode(): LongArray {

        val values = ArrayList<Long>()
        structs.forEach { (type, storage) ->
            val lanes = storage.fields.flatMap { field -> field.lanes }
            values += type.toLong()
            values += sizes.bytes(storage).toLong()
            values += lanes.size.toLong()
            lanes.forEach { lane ->
                values += (lane.index.toLong() shl 1) or if (lane.spilled) SPILLED else 0L
            }
        }

        return values.toLongArray()
    }

    private fun supported(index: Int): StructStorage? {
        return try {
            plans.struct(index)
        } catch (_: UnsupportedShape) {
            null
        }
    }

    private companion object {
        const val SPILLED = 1L
    }
}
