package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.ArrayReference

@InternalChasmApi
interface ManagedEngine : Engine {

    fun <T : Any> unsafeBorrow(
        array: ArrayReference,
        storage: Class<T>,
    ): T

    fun unsafeAllocateArray(
        type: HostGcType,
        storage: Any,
    ): ArrayReference

    fun unsafeBorrowElements(table: Table): Array<Any?>
}
