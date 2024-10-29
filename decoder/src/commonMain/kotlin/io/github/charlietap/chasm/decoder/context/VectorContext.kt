package io.github.charlietap.chasm.decoder.context

internal interface VectorContext {
    var index: Int
}

internal data class VectorContextImpl(
    override var index: Int = 0,
) : VectorContext
