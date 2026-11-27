package io.github.charlietap.chasm.cir

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
@Target(AnnotationTarget.CLASS)
annotation class CirTag(
    val name: String,
)
