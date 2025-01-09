package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.Limits

fun limits(builder: LimitsBuilder.() -> Unit): Limits {
    return LimitsBuilder().apply(builder).build()
}

class LimitsBuilder {

    var min: UInt = 0u
    var max: UInt? = null

    fun build(): Limits = Limits(min, max)
}
