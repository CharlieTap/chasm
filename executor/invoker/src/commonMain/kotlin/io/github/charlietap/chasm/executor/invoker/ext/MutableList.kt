package io.github.charlietap.chasm.executor.invoker.ext

fun <T> MutableList<T>.grow(newSize: Int, value: T) {
    var currentSize = this.size
    while (currentSize < newSize) {
        this.add(value)
        currentSize++
    }
}
