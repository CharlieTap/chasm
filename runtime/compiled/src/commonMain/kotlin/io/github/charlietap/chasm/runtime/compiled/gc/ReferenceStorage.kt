package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.instance.TableInstance
import kotlin.jvm.JvmField

class ReferenceGlobal(
    @JvmField var value: Any?,
)

class ReferenceTable(
    @JvmField var elements: Array<Any?>,
    @JvmField val instance: TableInstance,
)

class ReferenceSegment(
    @JvmField var elements: Array<Any?>,
)
