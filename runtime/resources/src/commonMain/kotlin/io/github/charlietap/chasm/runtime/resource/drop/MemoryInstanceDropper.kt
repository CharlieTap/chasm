package io.github.charlietap.chasm.runtime.resource.drop

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.type.Limits

@InternalChasmApi
typealias MemoryInstanceDropper = (MemoryInstance) -> Unit

@InternalChasmApi
fun MemoryInstanceDropper(
    instance: MemoryInstance,
) = MemoryInstanceDropper(
    instance = instance,
    destructor = ::LinearMemoryDestructor,
)

internal inline fun MemoryInstanceDropper(
    instance: MemoryInstance,
    destructor: LinearMemoryDestructor,
) {
    instance.type = instance.type.copy(
        limits = Limits(0u, 0u),
    )
    instance.refresh()
    destructor(instance.data)
    instance.refresh()
}
