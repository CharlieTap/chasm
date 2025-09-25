package io.github.charlietap.chasm.vm

internal expect class InstanceReference

class Instance internal constructor(internal val reference: InstanceReference)
