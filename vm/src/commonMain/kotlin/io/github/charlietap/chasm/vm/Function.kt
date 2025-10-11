package io.github.charlietap.chasm.vm

internal expect class FunctionReference

class Function internal constructor(internal val reference: FunctionReference)
