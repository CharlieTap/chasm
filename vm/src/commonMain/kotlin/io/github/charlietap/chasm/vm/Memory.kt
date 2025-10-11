package io.github.charlietap.chasm.vm

internal expect class MemoryReference

class Memory internal constructor(internal val reference: MemoryReference)
