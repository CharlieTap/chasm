package io.github.charlietap.chasm.vm

internal expect class ModuleReference

class Module internal constructor(internal val reference: ModuleReference)
