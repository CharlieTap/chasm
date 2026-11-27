package io.github.charlietap.chasm.executor.invoker.ext

internal inline fun <T> List<T>.forEachReversed(action: (T) -> Unit) {
    var index = size - 1
    while (index >= 0) {
        action(this[index])
        index--
    }
}
