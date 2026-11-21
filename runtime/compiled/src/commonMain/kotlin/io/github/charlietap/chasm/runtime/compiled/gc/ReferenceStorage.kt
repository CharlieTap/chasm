package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import kotlin.jvm.JvmField

class ReferenceGlobal(
    @JvmField var value: Any?,
)

class ReferenceTable(
    @JvmField var elements: Array<Any?>,
    @JvmField val instance: TableInstance,
) {

    @JvmField
    var targets: Array<IndirectTarget?> = arrayOfNulls(0)
}

class IndirectTarget(
    @JvmField val function: FunctionInstance,
    @JvmField val type: Int,
    @JvmField val entry: Any?,
)

class ReferenceSegment(
    @JvmField var elements: Array<Any?>,
)
