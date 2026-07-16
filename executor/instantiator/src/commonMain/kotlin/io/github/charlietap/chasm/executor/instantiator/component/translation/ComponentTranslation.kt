package io.github.charlietap.chasm.executor.instantiator.component.translation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.component.AliasDefinition
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.ast.component.Definition
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.ExternalType
import io.github.charlietap.chasm.ast.component.InstanceExportAliasTarget
import io.github.charlietap.chasm.ast.component.OuterAliasTarget
import io.github.charlietap.chasm.executor.instantiator.ModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.component.ComponentPlanningContext
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.invalidPreparation
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentScopeTypes
import io.github.charlietap.chasm.type.component.ComponentTypeDefinition
import io.github.charlietap.chasm.type.component.CoreModuleType
import io.github.charlietap.chasm.type.component.CoreType

internal fun translateComponent(
    context: ComponentPlanningContext,
    component: Component,
    types: ComponentScopeTypes,
    moduleCompiler: ModuleCompiler,
): Result<ComponentTranslation, ComponentPreparationError> = binding {
    var componentIndex = 0
    val definitions = buildList {
        component.definitions.forEach { definition ->
            when (definition) {
                is io.github.charlietap.chasm.ast.component.CoreModule -> {
                    val compiled = moduleCompiler(context.config, definition.module)
                        .mapError { error -> invalidPreparation("core module compilation failed: $error") }
                        .bind()
                    val moduleIndex = context.modules.size
                    context.modules += compiled
                    add(TranslatedComponentDefinition.CoreModule(moduleIndex))
                }
                is io.github.charlietap.chasm.ast.component.NestedComponent -> {
                    val nestedTypes = types.components.getOrNull(componentIndex)?.nested
                        .toResultOr { invalidPreparation("missing validated nested component types") }
                        .bind()
                    val nested = translateComponent(
                        context = context,
                        component = definition.component,
                        types = nestedTypes,
                        moduleCompiler = moduleCompiler,
                    ).bind()
                    add(TranslatedComponentDefinition.NestedComponent(nested))
                    componentIndex += 1
                }
                else -> {
                    add(TranslatedComponentDefinition.Source(definition))
                    if (definition.appendsComponent()) componentIndex += 1
                }
            }
        }
    }
    ComponentTranslation(types, definitions)
}

private fun Definition.appendsComponent(): Boolean = when (this) {
    is io.github.charlietap.chasm.ast.component.Import -> type is ExternalType.Component
    is io.github.charlietap.chasm.ast.component.Export -> target is ExportTarget.Component
    is io.github.charlietap.chasm.ast.component.Alias -> when (val alias = alias) {
        is AliasDefinition.InstanceExport -> alias.target is InstanceExportAliasTarget.Component
        is AliasDefinition.Outer -> alias.target is OuterAliasTarget.Component
        is AliasDefinition.CoreInstanceExport -> false
    }
    else -> false
}

internal data class ComponentTranslation(
    val types: ComponentScopeTypes,
    val definitions: List<TranslatedComponentDefinition>,
)

internal sealed interface TranslatedComponentDefinition {

    data class CoreModule(
        val moduleIndex: Int,
    ) : TranslatedComponentDefinition

    data class NestedComponent(
        val component: ComponentTranslation,
    ) : TranslatedComponentDefinition

    data class Source(
        val definition: Definition,
    ) : TranslatedComponentDefinition
}

internal data class PlannerCoreModuleReference(
    val type: CoreModuleType,
    val source: PreparedCoreModuleSource,
)

internal data class PlannerCoreInstanceReference(
    val runtimeIndex: RuntimeCoreInstanceIndex?,
    val exports: Map<String, PlannerCoreExportReference>,
)

internal sealed interface PlannerCoreExportReference {

    data class Function(
        val value: PreparedCoreExternalValue.Function,
    ) : PlannerCoreExportReference

    data class Table(
        val value: PreparedCoreExternalValue.Table,
    ) : PlannerCoreExportReference

    data class Memory(
        val value: PreparedCoreExternalValue.Memory,
    ) : PlannerCoreExportReference

    data class Global(
        val value: PreparedCoreExternalValue.Global,
    ) : PlannerCoreExportReference

    data class Tag(
        val value: PreparedCoreExternalValue.Tag,
    ) : PlannerCoreExportReference

    data class Type(
        val value: CoreType,
    ) : PlannerCoreExportReference

    data class Module(
        val value: PlannerCoreModuleReference,
    ) : PlannerCoreExportReference

    data class Instance(
        val value: PlannerCoreInstanceReference,
    ) : PlannerCoreExportReference
}

internal data class PlannerComponentReference(
    val translation: ComponentTranslation,
    val captures: List<PlannerFrameSnapshot>,
)

internal data class PlannerComponentInstanceReference(
    val runtimeIndex: RuntimeComponentInstanceIndex,
    val exports: Map<String, PlannerExternalReference>,
)

internal data class PlannerTypeReference(
    val type: ComponentTypeDefinition,
    val resourceType: RuntimeResourceTypeIndex? = null,
)

internal sealed interface PlannerExternalReference {

    data class CoreModule(
        val value: PlannerCoreModuleReference,
    ) : PlannerExternalReference

    data class Function(
        val value: PreparedComponentFunctionIndex,
    ) : PlannerExternalReference

    data class Type(
        val value: PlannerTypeReference,
    ) : PlannerExternalReference

    data class Component(
        val value: PlannerComponentReference,
    ) : PlannerExternalReference

    data class Instance(
        val value: PlannerComponentInstanceReference,
    ) : PlannerExternalReference
}

internal class PlannerFrame(
    val owner: RuntimeComponentInstanceIndex,
    val types: ComponentScopeTypes,
    private val captures: List<PlannerFrameSnapshot>,
) {
    var canonicalAbiIndex: Int = 0
    val coreModules = mutableListOf<PlannerCoreModuleReference>()
    val coreInstances = mutableListOf<PlannerCoreInstanceReference>()
    val coreTypes = mutableListOf<CoreType>()
    val coreFunctions = mutableListOf<PreparedCoreExternalValue.Function>()
    val coreTables = mutableListOf<PreparedCoreExternalValue.Table>()
    val coreMemories = mutableListOf<PreparedCoreExternalValue.Memory>()
    val coreGlobals = mutableListOf<PreparedCoreExternalValue.Global>()
    val coreTags = mutableListOf<PreparedCoreExternalValue.Tag>()
    val componentTypes = mutableListOf<PlannerTypeReference>()
    val functions = mutableListOf<PreparedComponentFunctionIndex>()
    val components = mutableListOf<PlannerComponentReference>()
    val instances = mutableListOf<PlannerComponentInstanceReference>()
    val exports = linkedMapOf<String, PlannerExternalReference>()
    val importedResourceTypes = linkedMapOf<ComponentResourceTypeId, RuntimeResourceTypeIndex>()

    fun resourceType(id: ComponentResourceTypeId): RuntimeResourceTypeIndex? = componentTypes
        .asReversed()
        .firstOrNull { reference ->
            (reference.type.type as? ComponentDefinedType.Resource)?.id == id
        }
        ?.resourceType

    fun capture(): List<PlannerFrameSnapshot> = captures + snapshot()

    fun outer(count: UInt): PlannerFrameSnapshot? {
        if (count == 0u) return snapshot()
        return captures.getOrNull(captures.size - count.toInt())
    }

    private fun snapshot(): PlannerFrameSnapshot = PlannerFrameSnapshot(
        coreModules = coreModules.toList(),
        coreInstances = coreInstances.toList(),
        coreTypes = coreTypes.toList(),
        componentTypes = componentTypes.toList(),
        components = components.toList(),
    )
}

internal data class PlannerFrameSnapshot(
    val coreModules: List<PlannerCoreModuleReference>,
    val coreInstances: List<PlannerCoreInstanceReference>,
    val coreTypes: List<CoreType>,
    val componentTypes: List<PlannerTypeReference>,
    val components: List<PlannerComponentReference>,
)
