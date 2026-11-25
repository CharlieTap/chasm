package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.declarations.DataSource
import java.io.InputStream

internal interface ModuleResources {

    fun contains(path: String): Boolean

    fun length(path: String): Long?

    fun open(path: String): InputStream?

    fun data(path: String): DataSource
}
