package io.github.charlietap.chasm.executor.invoker.drop

import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.type.Limits

typealias MemoryInstanceDropper = (MemoryInstance) -> Unit

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
}
