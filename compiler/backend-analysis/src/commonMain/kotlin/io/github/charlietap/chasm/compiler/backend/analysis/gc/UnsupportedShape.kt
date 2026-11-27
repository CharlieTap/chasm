package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
class UnsupportedShape(
    message: String,
) : Exception(message)
