package io.github.charlietap.chasm.executor.instantiator.component.linking

import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.CoreModuleType

internal data class PreparedComponentImport(
    val name: String,
    val value: PreparedComponentImportValue,
)

internal sealed interface PreparedComponentImportValue {

    data class CoreModule(
        val importIndex: Int,
        val type: CoreModuleType,
    ) : PreparedComponentImportValue

    data class Function(
        val importIndex: Int,
        val type: ComponentFunctionType,
        val resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeIndex>,
    ) : PreparedComponentImportValue

    data class Instance(
        val imports: List<PreparedComponentImport>,
    ) : PreparedComponentImportValue

    data class ResourceType(
        val resourceType: RuntimeResourceTypeIndex,
        val type: ComponentResourceTypeId,
    ) : PreparedComponentImportValue
}
