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
