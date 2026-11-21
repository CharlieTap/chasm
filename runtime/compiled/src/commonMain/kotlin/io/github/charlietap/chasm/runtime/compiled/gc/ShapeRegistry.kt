package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.type.DefinedType

interface ShapeRegistry {

    fun type(value: Any): Int

    fun shape(type: HostGcType): Class<*>?

    fun definedType(type: HostGcType): DefinedType?
}
