package io.github.charlietap.chasm.runtime.reference

import io.github.charlietap.chasm.host.HostExceptions
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostGc
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostReferenceRoot
import io.github.charlietap.chasm.host.HostReferences
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

interface ReferenceBridge {

    val references: HostReferences

    val gc: HostGc

    val externs: HostExterns

    val exceptions: HostExceptions

    fun global(global: GlobalInstance): HostGlobal

    fun table(table: TableInstance): HostTable

    fun growTable(
        table: TableInstance,
        elementsToAdd: Int,
        value: Long,
    ): Int

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

    fun retainExtern(value: Any): HostReferenceRoot

    fun toHost(
        value: Any?,
        extern: Boolean,
    ): Long

    fun toHostResult(
        value: Any?,
        extern: Boolean,
    ): Long

    fun fromHost(encoded: Long): Any?

    fun releaseEscaped()
}
