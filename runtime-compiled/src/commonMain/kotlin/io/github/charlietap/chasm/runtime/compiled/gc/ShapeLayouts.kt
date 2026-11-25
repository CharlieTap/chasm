package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.host.HostGcType

@InternalChasmApi
interface ShapeLayouts {

    fun struct(value: Any): StructLayout?

    fun array(value: Any): ArrayLayout?

    fun struct(type: HostGcType): StructLayout

    fun array(type: HostGcType): ArrayLayout
}

@InternalChasmApi
class StructLayout(
    val type: HostGcType,
    val rtt: Rtt,
    val storageLayout: StructStorageLayout,
    val mutable: BooleanArray,
    val checks: Array<ReferenceCheck?>,
)

@InternalChasmApi
class ArrayLayout(
    val type: HostGcType,
    val rtt: Rtt,
    val storageLayout: ArrayStorageLayout,
    val mutable: Boolean,
    val check: ReferenceCheck?,
)
