package io.github.charlietap.chasm.executor.instantiator.component.fixture

import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImport
import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImportValue
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.fixture.type.component.coreModuleType
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.CoreModuleType

internal fun preparedComponentImport(
    name: String = "import",
    value: PreparedComponentImportValue = functionPreparedComponentImportValue(),
) = PreparedComponentImport(
    name = name,
    value = value,
)

internal fun coreModulePreparedComponentImportValue(
    importIndex: Int = 0,
    type: CoreModuleType = coreModuleType(),
) = PreparedComponentImportValue.CoreModule(
    importIndex = importIndex,
    type = type,
)

internal fun functionPreparedComponentImportValue(
    importIndex: Int = 0,
    type: ComponentFunctionType = componentFunctionType(),
    resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeIndex> = emptyMap(),
) = PreparedComponentImportValue.Function(
    importIndex = importIndex,
    type = type,
    resourceTypes = resourceTypes,
)

internal fun instancePreparedComponentImportValue(
    imports: List<PreparedComponentImport> = emptyList(),
) = PreparedComponentImportValue.Instance(imports)

internal fun resourceTypePreparedComponentImportValue(
    type: ComponentResourceTypeId,
    resourceType: RuntimeResourceTypeIndex = RuntimeResourceTypeIndex(0),
) = PreparedComponentImportValue.ResourceType(
    resourceType = resourceType,
    type = type,
)
