package io.github.charlietap.chasm.runtime.host

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.ModuleIndex

/** Resolves [index] in the module instance and makes that tag the receiver. */
@InternalChasmApi
context(module: RawHostModuleInstance, resources: RawHostResources)
inline fun <T> withTag(
    index: ModuleIndex.TagIndex,
    block: HostTag.() -> T,
): T {
    return resources.tag(module, index).block()
}

/** Makes the calling store's exception API the receiver. */
@InternalChasmApi
context(resources: RawHostResources)
inline fun <T> withExceptions(block: HostExceptions.() -> T): T {
    return resources.exceptions.block()
}
