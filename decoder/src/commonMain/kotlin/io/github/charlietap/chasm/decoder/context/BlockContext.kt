package io.github.charlietap.chasm.decoder.context

internal interface BlockContext {
    val blockEndOpcode: UByte
}

internal data class BlockContextImpl(
    override val blockEndOpcode: UByte = 0u,
) : BlockContext
