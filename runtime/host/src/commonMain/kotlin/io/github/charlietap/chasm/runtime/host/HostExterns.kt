package io.github.charlietap.chasm.runtime.host

/**
 * `HostExterns` is an API for creating and inspecting WebAssembly `externref`
 * values inside host functions.
 *
 * A non-null result is kept alive by the current host reference scope. Retain
 * its [HostExternReference.raw] value when it must outlive that scope. Creating
 * the same Kotlin object more than once creates separate extern identities.
 */
interface HostExterns {

    /** Creates a scoped extern reference containing [value]. */
    context(resources: RawHostResources)
    fun create(value: Any): HostExternReference

    /** Returns the null extern reference. */
    fun nullReference(): HostExternReference

    /** Returns the kind of value held by [reference]. */
    fun kind(reference: HostExternReference): HostExternKind

    /** Returns the host value held by [reference]. */
    fun value(reference: HostExternReference): Any

    /** Returns the guest reference wrapped by [reference]. */
    fun externalizedReference(reference: HostExternReference): HostReference

    /** Wraps [reference] in a scoped extern reference. */
    fun externalize(reference: HostReference): HostExternReference
}
