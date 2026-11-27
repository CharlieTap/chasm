package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
enum class Version(val number: UShort) {
    Prototype13(13u.toUShort()),
}
