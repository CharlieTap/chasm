package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType

internal interface ShapeLayouts {

    fun struct(value: Any): StructLayout?

    fun structStorage(value: Any): StructStorageLayout?

    fun array(value: Any): ArrayLayout?

    fun struct(type: HostGcType): StructLayout

    fun array(type: HostGcType): ArrayLayout
}

internal class StructLayout(
    val type: HostGcType,
    val rtt: Rtt,
    val storageLayout: StructStorageLayout,
    val mutable: BooleanArray,
    val checks: Array<ReferenceCheck?>,
)

internal class ArrayLayout(
    val type: HostGcType,
    val rtt: Rtt,
    val storageLayout: ArrayStorageLayout,
    val mutable: Boolean,
    val check: ReferenceCheck?,
)
