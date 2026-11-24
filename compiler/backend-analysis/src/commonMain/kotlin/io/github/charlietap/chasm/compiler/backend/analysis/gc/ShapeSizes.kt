package io.github.charlietap.chasm.compiler.backend.analysis.gc

class ShapeSizes(
    private val arrayOverhead: Int,
) {

    fun structBytes(shape: StructShape): Int {
        val fields = shape.fields.sumOf { field -> descriptorBytes(field.descriptor) }
        return (OBJECT_HEADER_BYTES + fields + 7) and 7.inv()
    }

    fun arrayShift(shape: ArrayShape): Int {
        return when (descriptorBytes(shape.data.descriptor.substring(1))) {
            1 -> 0
            2 -> 1
            8 -> 3
            else -> 2
        }
    }

    fun arrayBytes(
        length: Int,
        shift: Int,
    ): Int {
        return (length shl shift) + arrayOverhead
    }

    private fun descriptorBytes(descriptor: String): Int {
        return when (descriptor) {
            "B" -> 1
            "C" -> 2
            "J", "D" -> 8
            else -> 4
        }
    }

    private companion object {
        const val OBJECT_HEADER_BYTES = 8
    }
}
