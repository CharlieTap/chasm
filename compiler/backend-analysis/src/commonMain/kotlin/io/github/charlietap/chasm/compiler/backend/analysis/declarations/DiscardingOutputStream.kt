package io.github.charlietap.chasm.compiler.backend.analysis.declarations

import io.github.charlietap.chasm.InternalChasmApi
import java.io.OutputStream

@InternalChasmApi
class DiscardingOutputStream : OutputStream() {

    override fun write(byte: Int) {
    }

    override fun write(
        bytes: ByteArray,
        offset: Int,
        length: Int,
    ) {
    }
}
