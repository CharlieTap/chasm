package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import kotlin.jvm.JvmField

interface ReferenceStorage {

    fun referenceGlobal(global: GlobalInstance): ReferenceGlobal

    fun referenceTable(table: TableInstance): ReferenceTable
}

class ReferenceGlobal(
    @JvmField var value: Any?,
) {
    @JvmField var check: ReferenceCheck? = null
}

class ReferenceTable(
    @JvmField var elements: Array<Any?>,
    @JvmField val instance: TableInstance,
) {
    @JvmField var check: ReferenceCheck? = null
}

class ReferenceSegment(
    @JvmField var elements: Array<Any?>,
)
