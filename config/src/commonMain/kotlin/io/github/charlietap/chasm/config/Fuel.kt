package io.github.charlietap.chasm.config

import kotlin.jvm.JvmInline

@JvmInline
value class Fuel(val units: Long) {

    init {
        require(units >= 0) { "fuel must not be negative" }
    }
}

val Int.fuel: Fuel get() = Fuel(toLong())

val Long.fuel: Fuel get() = Fuel(this)
