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

    fun clear() {
        if (values.size > RETAINED_CAPACITY) values = LongArray(16)
        size = 0
    }

    operator fun get(index: Int): Long {
        return values[index]
    }

    fun toSortedArray(): LongArray {
        return values.copyOf(size).also { sorted -> sorted.sort() }
    }

    private companion object {
        const val RETAINED_CAPACITY = 16_384
    }
}
