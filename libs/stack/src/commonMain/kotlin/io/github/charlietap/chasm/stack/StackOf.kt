package io.github.charlietap.chasm.stack

fun <T> stackOf(vararg entries: T): Stack<T> {
    val stack = Stack<T>()
    entries.forEach { stack.push(it) }
    return stack
}
