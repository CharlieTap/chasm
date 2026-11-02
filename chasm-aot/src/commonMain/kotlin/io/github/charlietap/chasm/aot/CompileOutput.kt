package io.github.charlietap.chasm.aot

import java.io.OutputStream

interface CompileOutput {

    fun classFile(
        internalName: String,
        bytes: ByteArray,
    )

    fun resource(
        path: String,
        write: (OutputStream) -> Unit,
    )
}
