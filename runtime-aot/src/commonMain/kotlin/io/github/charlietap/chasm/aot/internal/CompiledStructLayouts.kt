@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModuleStructs
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import io.github.charlietap.chasm.runtime.compiled.gc.StorageField
import io.github.charlietap.chasm.runtime.compiled.gc.StorageLane
import io.github.charlietap.chasm.runtime.compiled.gc.StructAllocator
import io.github.charlietap.chasm.runtime.compiled.gc.StructStorageLayout
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.structType

internal class CompiledStructLayouts(
    private val types: List<DefinedType>,
    private val structs: CompiledModuleStructs,
) {

    private val lanesByType: Map<Int, StructLanes> = decode()

    fun layout(index: Int): StructStorageLayout? {

        val structLanes = lanesByType[index] ?: return null
        val fields = checkNotNull(types[index].structType()) { "type $index is not a struct type" }.fields
        var nextLane = 0
        val storageFields = fields.map { field ->
            val width = if (isWide(field.storageType)) 2 else 1
            val fieldLanes = List(width) { offset -> structLanes.lanes[nextLane + offset] }
            nextLane += width
            StorageField(field, fieldLanes)
        }

        return StructStorageLayout(storageFields, structLanes.bytes, StructAllocator { rtt -> structs.newStruct(index, rtt) as CompiledStruct })
    }

    private fun decode(): Map<Int, StructLanes> {

        val values = structs.structLanes()
        val decoded = HashMap<Int, StructLanes>()
        var position = 0

        while (position < values.size) {
            val type = values[position].toInt()
            val bytes = values[position + 1].toInt()
            val count = values[position + 2].toInt()
            val firstLane = position + LANES_OFFSET
            decoded[type] = StructLanes(bytes, List(count) { lane -> storageLane(values[firstLane + lane]) })
            position = firstLane + count
        }

        return decoded
    }

    private fun storageLane(encoded: Long): StorageLane {
        return StorageLane((encoded ushr 1).toInt(), encoded and SPILLED != 0L)
    }

    private fun isWide(storage: StorageType): Boolean {

        val number = ((storage as? StorageType.Value)?.type as? ValueType.Number)?.numberType

        return number == NumberType.I64 || number == NumberType.F64
    }

    private class StructLanes(
        val bytes: Int,
        val lanes: List<StorageLane>,
    )

    private companion object {
        const val LANES_OFFSET = 3
        const val SPILLED = 1L
    }
}
