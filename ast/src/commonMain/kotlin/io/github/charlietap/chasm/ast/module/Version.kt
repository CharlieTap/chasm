package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
enum class Version(val number: UByteArray) {
    One(ubyteArrayOf(0x01u, 0x00u, 0x00u, 0x00u)),
}
