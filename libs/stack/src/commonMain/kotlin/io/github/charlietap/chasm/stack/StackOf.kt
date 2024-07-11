package io.github.charlietap.chasm.stack

fun <T> stackOf(vararg entries: T): Stack<T> = Stack(entries.toCollection(ArrayDeque()))
