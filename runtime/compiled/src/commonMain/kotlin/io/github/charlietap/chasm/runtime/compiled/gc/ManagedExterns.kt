package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostExternKind
import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.store.Store

class ManagedExterns(
    private val bridge: ManagedReferenceBridge,
    private val store: Store,
) : HostExterns {

    context(resources: HostResources)
    override fun create(value: Any): HostExternReference {
        val extern = HostValue(value)
        return HostExternReference(bridge.references.rootObject(extern, bridge.toHost(extern, true)))
    }

    override fun nullReference(): HostExternReference {
        return HostExternReference(bridge.toHost(null, true))
    }

    override fun kind(reference: HostExternReference): HostExternKind {

        if (reference.raw and RV_TYPE_MASK == RV_TYPE_NULL) {
            return HostExternKind.NULL
        }

        val inner = reference.raw ushr RV_SHIFT_BITS
        return if (inner and RV_TYPE_MASK == RV_TYPE_HOST) HostExternKind.HOST_VALUE else HostExternKind.EXTERNALIZED_REFERENCE
    }

    override fun value(reference: HostExternReference): Any {
        return when (val value = bridge.fromHost(reference.raw)) {
            is LegacyHostValue -> store.heap.value(HostExternReference((value.encoded shl RV_SHIFT_BITS) or RV_TYPE_EXTERN))
            else -> (value as HostValue).value
        }
    }

    override fun externalizedReference(reference: HostExternReference): HostReference {
        return reference.raw ushr RV_SHIFT_BITS
    }

    override fun externalize(reference: HostReference): HostExternReference {

        if (reference and RV_TYPE_MASK == RV_TYPE_NULL) {
            return nullReference()
        }

        return HostExternReference(bridge.references.rootScoped((reference shl RV_SHIFT_BITS) or RV_TYPE_EXTERN))
    }
}
