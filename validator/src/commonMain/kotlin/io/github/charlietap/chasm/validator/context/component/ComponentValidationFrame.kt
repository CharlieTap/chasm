package io.github.charlietap.chasm.validator.context.component

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.ComponentEntityType
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentInstanceType
import io.github.charlietap.chasm.type.component.ComponentItemType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentType
import io.github.charlietap.chasm.type.component.ComponentTypeDefinition
import io.github.charlietap.chasm.type.component.ComponentTypeId
import io.github.charlietap.chasm.type.component.CoreInstanceType
import io.github.charlietap.chasm.type.component.CoreModuleType
import io.github.charlietap.chasm.type.component.CoreType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDescriptor
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.validator.context.CoreTypeValidationContext
import io.github.charlietap.chasm.validator.error.ComponentValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.type.component.ComponentTypeEntry
import io.github.charlietap.chasm.validator.type.component.ComponentTypeInfo
import io.github.charlietap.chasm.validator.type.component.ComponentTypeVisibility
import io.github.charlietap.chasm.validator.type.component.ComponentValueEntry
import io.github.charlietap.chasm.validator.type.component.canCombineWith
import io.github.charlietap.chasm.validator.type.component.combineEffectiveTypeSizes
import io.github.charlietap.chasm.validator.type.component.effectiveTypeSize
import io.github.charlietap.chasm.validator.type.component.typeInfo

internal class ComponentValidationFrame : CoreTypeValidationContext {

    var kind: ComponentScopeKind = ComponentScopeKind.Component
        private set

    val coreTypes = mutableListOf<CoreType>()
    val coreFunctions = mutableListOf<DefinedType>()
    val coreTables = mutableListOf<TableType>()
    val coreMemories = mutableListOf<MemoryType>()
    val coreGlobals = mutableListOf<GlobalType>()
    val coreTags = mutableListOf<TagType>()
    val coreModules = mutableListOf<CoreModuleType>()
    val coreInstances = mutableListOf<CoreInstanceType>()

    val types = mutableListOf<ComponentTypeEntry>()
    private val typeInfo = mutableMapOf<ComponentTypeId, ComponentTypeInfo>()
    val functions = mutableListOf<ComponentFunctionType>()
    val values = mutableListOf<ComponentValueEntry>()
    val components = mutableListOf<ComponentItemType>()
    val instances = mutableListOf<ComponentInstanceType>()
    val canonicalAbi = mutableListOf<CanonicalAbiDescriptor>()

    val imports = linkedMapOf<String, io.github.charlietap.chasm.type.component.ComponentEntityType>()
    val exports = linkedMapOf<String, io.github.charlietap.chasm.type.component.ComponentEntityType>()
    val importNames = ComponentNameContext()
    val exportNames = ComponentNameContext()
    val importedFunctions = mutableSetOf<UInt>()
    val importedResources = linkedMapOf<ComponentResourceTypeId, List<String>>()
    val definedResources = mutableSetOf<ComponentResourceTypeId>()
    val localResourceRepresentations = mutableMapOf<ComponentResourceTypeId, ValueType>()
    val explicitResources = linkedMapOf<ComponentResourceTypeId, List<String>>()
    val visibility = ComponentTypeVisibility()
    var hasStart: Boolean = false
    var contextType: ValueType? = null
    var effectiveTypeSize: Int = 1
        private set

    override var limitsMaximum: ULong = ULong.MAX_VALUE

    override val definedTypeCount: Int
        get() = coreTypes.size

    override val lookup: DefinedTypeLookup = { index ->
        (coreTypes.getOrNull(index) as? CoreType.Defined)?.type
    }

    fun reset(kind: ComponentScopeKind) {
        clear()
        this.kind = kind
    }

    fun clear() {
        coreTypes.clear()
        coreFunctions.clear()
        coreTables.clear()
        coreMemories.clear()
        coreGlobals.clear()
        coreTags.clear()
        coreModules.clear()
        coreInstances.clear()
        types.clear()
        typeInfo.clear()
        functions.clear()
        values.clear()
        components.clear()
        instances.clear()
        canonicalAbi.clear()
        imports.clear()
        exports.clear()
        importNames.clear()
        exportNames.clear()
        importedFunctions.clear()
        importedResources.clear()
        definedResources.clear()
        localResourceRepresentations.clear()
        explicitResources.clear()
        visibility.clear()
        hasStart = false
        contextType = null
        effectiveTypeSize = 1
        limitsMaximum = ULong.MAX_VALUE
    }

    override fun definedType(index: Int): Result<DefinedType, ModuleValidatorError> {
        return lookup(index).toResultOr { TypeValidatorError.TypeMismatch }
    }

    fun componentType(): ComponentType = ComponentType(
        imports = LinkedHashMap(imports),
        exports = LinkedHashMap(exports),
        importedResources = importedResources.toMap(),
        definedResources = buildMap {
            definedResources.forEach { resource ->
                explicitResources[resource]?.let { path -> put(resource, path) }
            }
        },
        explicitResources = explicitResources.toMap(),
    )

    fun instanceType(): ComponentInstanceType = ComponentInstanceType(
        exports = LinkedHashMap(exports),
        definedResources = definedResources.toSet(),
        explicitResources = explicitResources.toMap(),
    )

    fun typeEntry(definition: ComponentTypeDefinition): ComponentTypeEntry = ComponentTypeEntry(
        definition = definition,
        info = definition.type.typeInfo(::componentTypeInfo),
    )

    fun addType(entry: ComponentTypeEntry) {
        types += entry
        typeInfo[entry.id] = entry.info
    }

    fun componentTypeInfo(id: ComponentTypeId): ComponentTypeInfo? = typeInfo[id]

    fun canAddExternalType(type: ComponentEntityType): Boolean =
        effectiveTypeSize.canCombineWith(type, ::componentTypeInfo)

    fun addExternalType(type: ComponentEntityType) {
        effectiveTypeSize = combineEffectiveTypeSizes(
            effectiveTypeSize,
            type.effectiveTypeSize(::componentTypeInfo),
        )
    }

    fun requireAllValuesConsumed(): Result<Unit, ComponentValidatorError> {
        val index = values.indexOfFirst { value -> !value.consumed }
        return if (index == -1) {
            com.github.michaelbull.result.Ok(Unit)
        } else {
            com.github.michaelbull.result.Err(ComponentValidatorError.UnconsumedValue(index.toUInt()))
        }
    }
}
