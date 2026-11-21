package io.github.charlietap.chasm.runtime.reference

import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

interface ReferenceBridge {

    fun initializeTable(
        table: TableInstance,
        element: ElementInstance,
        offset: Int,
        count: Int,
    )

    fun dropElement(element: ElementInstance)

    fun release(global: GlobalInstance)

    fun release(table: TableInstance)

    fun release(element: ElementInstance)

    fun toHostResult(
        value: Any?,
        extern: Boolean,
    ): Long

    fun fromHost(encoded: Long): Any?

    fun releaseEscaped()
}
