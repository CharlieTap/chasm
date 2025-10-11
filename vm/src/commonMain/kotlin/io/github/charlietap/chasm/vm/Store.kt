package io.github.charlietap.chasm.vm

internal expect class StoreReference

class Store internal constructor(internal val reference: StoreReference)
