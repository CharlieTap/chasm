package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.type.Limits

fun limits(builder: LimitsBuilder.() -> Unit): Limits {
    return LimitsBuilder().apply(builder).build()
}

class LimitsBuilder {

    var min: ULong = 0u
    var max: ULong? = null

    fun build(): Limits = Limits(min, max)
}
