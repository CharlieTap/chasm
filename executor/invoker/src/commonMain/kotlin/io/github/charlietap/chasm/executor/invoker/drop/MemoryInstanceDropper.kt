package io.github.charlietap.chasm.executor.invoker.drop

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.executor.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

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
