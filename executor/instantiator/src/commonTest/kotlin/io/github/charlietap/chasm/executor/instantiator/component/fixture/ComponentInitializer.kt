package io.github.charlietap.chasm.executor.instantiator.component.fixture

import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExportProjection
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreFunctionSource
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreImport
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeCoreFunctionIndex
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeCoreInstanceIndex
import io.github.charlietap.chasm.fixture.runtime.component.resource.canonicalResourceFunction
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunction

internal fun instantiateCoreModuleInitializer(
    index: Int = 0,
    module: PreparedCoreModuleSource = embeddedCoreModuleSource(index),
    imports: List<PreparedCoreImport> = emptyList(),
) = ComponentInitializer.InstantiateCoreModule(
    module = module,
    instance = runtimeCoreInstanceIndex(index),
    imports = imports,
)

internal fun lowerImportComponentInitializer(
    functionIndex: Int = 0,
    callPlan: Int = 0,
) = ComponentInitializer.LowerImport(
    function = runtimeCoreFunctionIndex(functionIndex),
    callPlan = callPlan,
)

internal fun resourceFunctionComponentInitializer(
    functionIndex: Int = 0,
    resource: CanonicalResourceFunction = canonicalResourceFunction(),
) = ComponentInitializer.ResourceFunction(
    function = runtimeCoreFunctionIndex(functionIndex),
    resource = resource,
)

internal fun extractCoreFunctionComponentInitializer(
    functionIndex: Int = 0,
    instanceIndex: Int = 0,
    exportIndex: Int = 0,
) = ComponentInitializer.ExtractCoreFunction(
    function = PreparedCoreExternalValue.Function(
        PreparedCoreFunctionSource.Export(
            instance = runtimeCoreInstanceIndex(instanceIndex),
            projection = PreparedCoreExportProjection.Direct(exportIndex),
        ),
    ),
    slot = runtimeCoreFunctionIndex(functionIndex),
)

internal fun embeddedCoreModuleSource(
    moduleIndex: Int = 0,
) = PreparedCoreModuleSource.Embedded(moduleIndex)

internal fun importedCoreModuleSource(
    importIndex: Int = 0,
) = PreparedCoreModuleSource.Import(importIndex)

internal fun preparedCoreImport(
    moduleName: String = "module",
    entityName: String = "entity",
    value: PreparedCoreExternalValue = preparedCoreMemoryExternalValue(),
) = PreparedCoreImport(
    moduleName = moduleName,
    entityName = entityName,
    value = value,
)

internal fun preparedCoreMemoryExternalValue(
    instanceIndex: Int = 0,
    exportIndex: Int = 0,
) = PreparedCoreExternalValue.Memory(
    instance = runtimeCoreInstanceIndex(instanceIndex),
    projection = PreparedCoreExportProjection.Direct(exportIndex),
)
