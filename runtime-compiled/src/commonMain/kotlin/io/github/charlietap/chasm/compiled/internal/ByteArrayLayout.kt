package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.runtime.compiled.byteArrayBase

internal interface ByteArrayLayout {

    fun baseOffset(): Long
}

internal class VmByteArrayLayout : ByteArrayLayout {

    override fun baseOffset(): Long {
        return byteArrayBase()
    }
}
