package io.github.charlietap.chasm.vm

internal expect class GlobalReference

class Global internal constructor(internal val reference: GlobalReference)
