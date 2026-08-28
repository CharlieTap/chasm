package io.github.charlietap.chasm.host

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
    context(resources: HostResources)
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

/** Creates a null or host-value extern reference for [value]. */
context(resources: HostResources)
inline fun HostExterns.createNullable(value: Any?): HostExternReference =
    if (value == null) nullReference() else create(value)

/** Creates an extern reference which remains alive until its root is released. */
context(resources: HostResources)
inline fun HostExterns.createRetained(value: Any): HostReferenceRoot {
    val reference = create(value)
    return resources.references.retain(reference.raw)
}
