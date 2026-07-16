package io.github.charlietap.chasm.runtime.component.info

import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex

data class PreparedComponentExport(
    val name: String,
    val value: PreparedComponentExportValue,
)

sealed interface PreparedComponentExportValue {

    data class CoreModule(
        val module: PreparedComponentCoreModule,
    ) : PreparedComponentExportValue

    data class Function(
        val function: PreparedComponentFunctionIndex,
    ) : PreparedComponentExportValue

    data class Instance(
        val exports: List<PreparedComponentExport>,
    ) : PreparedComponentExportValue

    data class ResourceType(
        val resourceType: RuntimeResourceTypeIndex,
    ) : PreparedComponentExportValue
}

sealed interface PreparedComponentCoreModule {

    data class Embedded(
        val moduleIndex: Int,
    ) : PreparedComponentCoreModule

    data class Import(
        val importIndex: Int,
    ) : PreparedComponentCoreModule
}
