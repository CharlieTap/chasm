package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

internal const val MAX_INLINE_INT_LANES = 23

internal fun assertNumberLaneReads(
    struct: CompiledStruct,
    intLanes: Int,
    valueOffset: Int,
    className: String,
) {

    assertTrue(intLanes <= MAX_INLINE_INT_LANES, className)

    for (firstPairedLane in listOf(intLanes, 0, 1)) {

        val fields = laneFields(intLanes, firstPairedLane)
        val destinations = IntArray(MAX_INLINE_INT_LANES) { UNUSED_LANE }
        fields.forEachIndexed { field, lanes ->
            if (lanes.size == 1) {
                destinations[lanes[0]] = (field shl DESTINATION_FIELD_SHIFT) or WHOLE_FIELD
            } else {
                destinations[lanes[0]] = (field shl DESTINATION_FIELD_SHIFT) or LOW_HALF
                destinations[lanes[1]] = (field shl DESTINATION_FIELD_SHIFT) or HIGH_HALF
            }
        }
        val expected = fields.map { lanes -> fieldBits(lanes, valueOffset) }.toLongArray()
        val buffer = LongArray(fields.size) { STALE_BITS }

        struct.readNumberLanes(destinations, buffer, shift = 0, from = 0, until = fields.size)

        assertContentEquals(expected, buffer, "$className paired from lane $firstPairedLane")
    }
}

private fun laneFields(
    intLanes: Int,
    firstPairedLane: Int,
): List<List<Int>> {

    val fields = ArrayList<List<Int>>()
    var lane = 0
    while (lane < intLanes) {
        if (lane >= firstPairedLane && lane + 1 < intLanes) {
            fields.add(listOf(lane, lane + 1))
            lane += 2
        } else {
            fields.add(listOf(lane))
            lane += 1
        }
    }

    return fields
}

private fun fieldBits(
    lanes: List<Int>,
    valueOffset: Int,
): Long {

    val low = (lanes[0] + valueOffset).toLong()

    return if (lanes.size == 1) {
        low
    } else {
        (low and LOW_BITS) or ((lanes[1] + valueOffset).toLong() shl Int.SIZE_BITS)
    }
}

private const val DESTINATION_FIELD_SHIFT = 2
private const val WHOLE_FIELD = 0
private const val LOW_HALF = 1
private const val HIGH_HALF = 2
private const val UNUSED_LANE = -1
private const val LOW_BITS = 0xFFFFFFFFL
private const val STALE_BITS = -1L
