package io.github.charlietap.chasm.compiler.backend.analysis.declarations

import java.io.OutputStream

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
