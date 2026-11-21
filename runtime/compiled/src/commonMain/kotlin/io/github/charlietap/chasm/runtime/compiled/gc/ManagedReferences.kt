package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostReference
import io.github.charlietap.chasm.host.HostReferenceRoot
import io.github.charlietap.chasm.host.HostReferences

class ManagedReferences(
    private val bridge: ManagedReferenceBridge,
) : HostReferences {

    private val scoped = ArrayList<Any?>()
    private val retainedReferences = ArrayList<Long>()
    private val retainedObjects = ArrayList<Any?>()
    private val freeRoots = ArrayList<Int>()

    @Synchronized
    override fun beginScope(capacity: Int): Int {
        scoped.ensureCapacity(scoped.size + capacity)
        return scoped.size
    }

    @Synchronized
    override fun rootScoped(reference: HostReference): HostReference {
        scoped += bridge.fromHost(reference)
        return reference
    }

    @Synchronized
    fun rootObject(
        value: Any?,
        reference: HostReference,
    ): HostReference {
        scoped += value
        return reference
    }

    @Synchronized
    fun retainObject(
        value: Any?,
        reference: HostReference,
    ): HostReferenceRoot {

        if (freeRoots.isNotEmpty()) {
            val slot = freeRoots.removeAt(freeRoots.size - 1)
            retainedReferences[slot] = reference
            retainedObjects[slot] = value
            return HostReferenceRoot(slot)
        }

        retainedReferences += reference
        retainedObjects += value

        return HostReferenceRoot(retainedReferences.size - 1)
    }

    @Synchronized
    override fun endScope(marker: Int) {
        while (scoped.size > marker) scoped.removeAt(scoped.size - 1)
    }

    override fun retain(reference: HostReference): HostReferenceRoot {
        return retainObject(bridge.fromHost(reference), reference)
    }

    @Synchronized
    override fun reference(root: HostReferenceRoot): HostReference {
        return retainedReferences[root.slot]
    }

    @Synchronized
    override fun release(root: HostReferenceRoot) {
        retainedReferences[root.slot] = 0L
        retainedObjects[root.slot] = null
        freeRoots += root.slot
    }
}
