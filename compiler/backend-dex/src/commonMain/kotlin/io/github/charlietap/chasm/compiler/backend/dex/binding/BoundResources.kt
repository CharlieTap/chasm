package io.github.charlietap.chasm.compiler.backend.dex.binding

import io.github.charlietap.chasm.compiler.backend.abi.Resource

internal class BoundResources(
    val objects: List<SlotBinding>,
    val values: List<SlotBinding>,
) {
    val objectCount: Int = maxOf(BindingLayout.VALUES_SLOT, objects.maxOfOrNull { binding -> binding.slot } ?: 0) + 1
    val valueCount: Int = (values.maxOfOrNull { binding -> binding.slot } ?: -1) + 1
}

internal class SlotBinding(
    val slot: Int,
    val resource: Resource,
)

internal fun boundResources(
    usedObjects: List<Array<Resource?>>,
    usedValues: List<Array<Resource?>>,
): BoundResources {
    return BoundResources(mergeUsed(usedObjects), mergeUsed(usedValues))
}

private fun mergeUsed(used: List<Array<Resource?>>): List<SlotBinding> {

    val bindings = ArrayList<SlotBinding>()
    val size = used.firstOrNull()?.size ?: 0
    for (slot in 0 until size) {
        val resource = used.firstNotNullOfOrNull { worker -> worker[slot] } ?: continue
        bindings += SlotBinding(slot, resource)
    }

    return bindings
}
