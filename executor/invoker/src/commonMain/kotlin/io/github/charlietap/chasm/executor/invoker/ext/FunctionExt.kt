package io.github.charlietap.chasm.executor.invoker.ext

internal infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R =
    { p1 -> f(this(p1)) }
