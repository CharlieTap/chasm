package io.github.charlietap.chasm.host

/** Resolves [index] in the module instance and makes that memory the receiver. */
context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withMemory(
    index: Int,
    block: HostMemory.() -> T,
): T = resources.memory(module, index).block()

/** Resolves [index] in the module instance and makes that table the receiver. */
context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withTable(
    index: Int,
    block: HostTable.() -> T,
): T = resources.table(module, index).block()

/** Resolves [index] in the module instance and makes that global the receiver. */
context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withGlobal(
    index: Int,
    block: HostGlobal.() -> T,
): T = resources.global(module, index).block()

/** Resolves [index] in the module instance and makes that tag the receiver. */
context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withTag(
    index: Int,
    block: HostTag.() -> T,
): T = resources.tag(module, index).block()

/** Runs [block] in a nested reference scope owned by the calling store. */
context(resources: HostResources)
inline fun <T> withReferences(
    capacity: Int = 0,
    block: HostReferences.() -> T,
): T = resources.references.withScope(capacity, block)

/** Makes the calling store's garbage collector the receiver. */
context(resources: HostResources)
inline fun <T> withGc(block: HostGc.() -> T): T = resources.gc.block()

/** Makes the calling store's extern API the receiver. */
context(resources: HostResources)
inline fun <T> withExterns(block: HostExterns.() -> T): T = resources.externs.block()

/** Makes the calling store's exception API the receiver. */
context(resources: HostResources)
inline fun <T> withExceptions(block: HostExceptions.() -> T): T = resources.exceptions.block()
