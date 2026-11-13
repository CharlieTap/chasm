package io.github.charlietap.chasm.compiler.backend.analysis

internal class LongList {
    private var values = LongArray(16)

    var size = 0
        private set

    fun add(value: Long) {

        if (size == values.size) {
            values = values.copyOf(size * 2)
        }

        values[size++] = value
    }

    operator fun get(index: Int): Long {
        return values[index]
    }

    fun sort() {
        values.sort(0, size)
    }

    fun toSortedArray(): LongArray {
        return values.copyOf(size).also { sorted -> sorted.sort() }
    }
}
