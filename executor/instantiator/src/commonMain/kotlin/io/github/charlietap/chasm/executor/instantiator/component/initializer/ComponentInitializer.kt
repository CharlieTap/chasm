package io.github.charlietap.chasm.executor.instantiator.component.initializer

import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunction
import io.github.charlietap.chasm.type.ValueType

internal sealed interface ComponentInitializer {

    data class InstantiateCoreModule(
        val module: PreparedCoreModuleSource,
        val instance: RuntimeCoreInstanceIndex,
        val imports: List<PreparedCoreImport>,
    ) : ComponentInitializer

    data class ExtractCoreFunction(
        val function: PreparedCoreExternalValue.Function,
        val slot: RuntimeCoreFunctionIndex,
    ) : ComponentInitializer

    data class ExtractMemory(
        val memory: PreparedCoreExternalValue.Memory,
        val slot: Int,
    ) : ComponentInitializer

    data class ExtractRealloc(
        val function: PreparedCoreExternalValue.Function,
        val slot: Int,
    ) : ComponentInitializer

    data class ExtractPostReturn(
        val function: PreparedCoreExternalValue.Function,
        val slot: Int,
    ) : ComponentInitializer

    data class LowerImport(
        val function: RuntimeCoreFunctionIndex,
        val callPlan: Int,
    ) : ComponentInitializer

    data class DefineResourceType(
        val resourceType: RuntimeResourceTypeIndex,
        val owner: RuntimeComponentInstanceIndex,
        val representation: ValueType,
        val destructor: PreparedCoreExternalValue.Function?,
    ) : ComponentInitializer

    data class ResourceFunction(
        val function: RuntimeCoreFunctionIndex,
        val resource: CanonicalResourceFunction,
    ) : ComponentInitializer
}

internal sealed interface PreparedCoreModuleSource {

    data class Embedded(
        val moduleIndex: Int,
    ) : PreparedCoreModuleSource

    data class Import(
        val importIndex: Int,
    ) : PreparedCoreModuleSource
}

internal data class PreparedCoreImport(
    val moduleName: String,
    val entityName: String,
    val value: PreparedCoreExternalValue,
)

internal sealed interface PreparedCoreExternalValue {
    data class Function(
        val source: PreparedCoreFunctionSource,
    ) : PreparedCoreExternalValue

    data class Table(
        val instance: RuntimeCoreInstanceIndex,
        val projection: PreparedCoreExportProjection,
    ) : PreparedCoreExternalValue

    data class Memory(
        val instance: RuntimeCoreInstanceIndex,
        val projection: PreparedCoreExportProjection,
    ) : PreparedCoreExternalValue

    data class Global(
        val instance: RuntimeCoreInstanceIndex,
        val projection: PreparedCoreExportProjection,
    ) : PreparedCoreExternalValue

    data class Tag(
        val instance: RuntimeCoreInstanceIndex,
        val projection: PreparedCoreExportProjection,
    ) : PreparedCoreExternalValue
}

internal sealed interface PreparedCoreFunctionSource {

    data class Export(
        val instance: RuntimeCoreInstanceIndex,
        val projection: PreparedCoreExportProjection,
    ) : PreparedCoreFunctionSource

    data class Lowered(
        val function: RuntimeCoreFunctionIndex,
    ) : PreparedCoreFunctionSource
}

internal sealed interface PreparedCoreExportProjection {

    data class Direct(
        val index: Int,
    ) : PreparedCoreExportProjection

    data class Imported(
        val moduleImportIndex: Int,
        val exportIndex: Int,
    ) : PreparedCoreExportProjection
}
