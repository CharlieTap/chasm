package io.github.charlietap.chasm.runtime.component.function

import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex

class ComponentEntryPolicy(
    enteringInstances: IntArray,
) {

    private val enteringInstances = enteringInstances.copyOf()

    val enteringInstanceCount: Int
        get() = enteringInstances.size

    fun enteringInstance(index: Int): RuntimeComponentInstanceIndex =
        RuntimeComponentInstanceIndex(enteringInstances[index])

    override fun equals(other: Any?): Boolean =
        this === other || other is ComponentEntryPolicy && enteringInstances.contentEquals(other.enteringInstances)

    override fun hashCode(): Int = enteringInstances.contentHashCode()

    override fun toString(): String = "ComponentEntryPolicy(enteringInstances=${enteringInstances.contentToString()})"
}
