package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import kotlin.jvm.JvmField

class ReferenceGlobal(
    @JvmField var value: Any?,
    @JvmField val instance: GlobalInstance,
) {
    @JvmField var check: ReferenceCheck? = null

    @JvmField var released = false
}

class ReferenceTable(
    @JvmField var elements: Array<Any?>,
    @JvmField val instance: TableInstance,
    @JvmField val trapOnGrowFailure: Boolean,
) {
    @JvmField var check: ReferenceCheck? = null

    @JvmField var released = false
}

class ReferenceSegment(
    @JvmField var elements: Array<Any?>,
)
