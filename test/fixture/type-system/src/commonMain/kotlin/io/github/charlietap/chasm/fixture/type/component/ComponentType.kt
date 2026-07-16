package io.github.charlietap.chasm.fixture.type.component

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentDefinedValueType
import io.github.charlietap.chasm.type.component.ComponentEntityType
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentInstanceType
import io.github.charlietap.chasm.type.component.ComponentItemType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentScopeTypes
import io.github.charlietap.chasm.type.component.ComponentType
import io.github.charlietap.chasm.type.component.ComponentTypeDefinition
import io.github.charlietap.chasm.type.component.ComponentTypeId
import io.github.charlietap.chasm.type.component.ComponentTypes
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.chasm.type.component.ComponentVariantCase
import io.github.charlietap.chasm.type.component.CoreEntityType
import io.github.charlietap.chasm.type.component.CoreImportName
import io.github.charlietap.chasm.type.component.CoreInstanceType
import io.github.charlietap.chasm.type.component.CoreModuleType
import io.github.charlietap.chasm.type.component.CoreType
import io.github.charlietap.chasm.type.component.LabeledComponentValueType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDescriptor

fun componentTypes(
    root: ComponentScopeTypes = componentScopeTypes(),
) = ComponentTypes(root)

fun componentScopeTypes(
    type: ComponentType = componentType(),
    coreTypes: List<CoreType> = emptyList(),
    coreFunctions: List<DefinedType> = emptyList(),
    coreTables: List<TableType> = emptyList(),
    coreMemories: List<MemoryType> = emptyList(),
    coreGlobals: List<GlobalType> = emptyList(),
    coreTags: List<TagType> = emptyList(),
    coreModules: List<CoreModuleType> = emptyList(),
    coreInstances: List<CoreInstanceType> = emptyList(),
    types: List<ComponentTypeDefinition> = emptyList(),
    functions: List<ComponentFunctionType> = emptyList(),
    values: List<ComponentValueType> = emptyList(),
    components: List<ComponentItemType> = emptyList(),
    instances: List<ComponentInstanceType> = emptyList(),
    localResourceRepresentations: Map<ComponentResourceTypeId, ValueType> = emptyMap(),
    canonicalAbi: List<CanonicalAbiDescriptor> = emptyList(),
) = ComponentScopeTypes(
    type = type,
    coreTypes = coreTypes,
    coreFunctions = coreFunctions,
    coreTables = coreTables,
    coreMemories = coreMemories,
    coreGlobals = coreGlobals,
    coreTags = coreTags,
    coreModules = coreModules,
    coreInstances = coreInstances,
    types = types,
    functions = functions,
    values = values,
    components = components,
    instances = instances,
    localResourceRepresentations = localResourceRepresentations,
    canonicalAbi = canonicalAbi,
)

fun componentType(
    imports: Map<String, ComponentEntityType> = emptyMap(),
    exports: Map<String, ComponentEntityType> = emptyMap(),
) = ComponentType(
    imports = imports,
    exports = exports,
)

fun componentItemType(
    type: ComponentType = componentType(),
    nested: ComponentScopeTypes? = null,
) = ComponentItemType(
    type = type,
    nested = nested,
)

fun componentInstanceType(
    exports: Map<String, ComponentEntityType> = emptyMap(),
) = ComponentInstanceType(exports = exports)

fun coreModuleType(
    imports: Map<CoreImportName, CoreEntityType> = emptyMap(),
    exports: Map<String, CoreEntityType> = emptyMap(),
) = CoreModuleType(
    imports = imports,
    exports = exports,
)

fun coreInstanceType(
    exports: Map<String, CoreEntityType> = emptyMap(),
) = CoreInstanceType(exports = exports)

fun coreFunctionEntityType(
    type: DefinedType,
) = CoreEntityType.Function(type)

fun coreMemoryEntityType(
    type: MemoryType,
) = CoreEntityType.Memory(type)

fun componentFunctionEntityType(
    type: ComponentFunctionType = componentFunctionType(),
) = ComponentEntityType.Function(type)

fun coreModuleComponentEntityType(
    type: CoreModuleType = coreModuleType(),
) = ComponentEntityType.CoreModule(type)

fun componentTypeId(
    value: UInt = 0u,
) = ComponentTypeId(value)

fun componentResourceTypeId(
    value: UInt = 0u,
) = ComponentResourceTypeId(value)

fun componentTypeDefinition(
    id: ComponentTypeId = componentTypeId(),
    type: ComponentDefinedType = ComponentDefinedType.Value(
        ComponentDefinedValueType.Primitive(ComponentPrimitiveType.Bool),
    ),
) = ComponentTypeDefinition(
    id = id,
    type = type,
)

fun componentFunctionTypeDefinition(
    id: ComponentTypeId = componentTypeId(),
    type: ComponentFunctionType = componentFunctionType(),
) = componentTypeDefinition(
    id = id,
    type = ComponentDefinedType.Function(type),
)

fun componentResourceTypeDefinition(
    id: ComponentTypeId = componentTypeId(),
    resource: ComponentResourceTypeId = componentResourceTypeId(),
) = componentTypeDefinition(
    id = id,
    type = ComponentDefinedType.Resource(resource),
)

fun componentResourceEntityType(
    referenced: ComponentTypeDefinition = componentResourceTypeDefinition(),
    createdId: ComponentTypeId = componentTypeId(),
) = ComponentEntityType.Type(
    referenced = referenced,
    createdId = createdId,
)

fun primitiveComponentValueType(
    type: ComponentPrimitiveType = ComponentPrimitiveType.Bool,
) = ComponentValueType.Primitive(type)

fun definedComponentValueType(
    type: ComponentDefinedValueType,
    id: ComponentTypeId = componentTypeId(),
) = ComponentValueType.Defined(
    componentTypeDefinition(
        id = id,
        type = ComponentDefinedType.Value(type),
    ),
)

fun recordComponentValueType(
    fields: List<LabeledComponentValueType> = emptyList(),
) = definedComponentValueType(ComponentDefinedValueType.Record(fields))

fun variantComponentValueType(
    cases: List<ComponentVariantCase> = emptyList(),
) = definedComponentValueType(ComponentDefinedValueType.Variant(cases))

fun listComponentValueType(
    element: ComponentValueType = primitiveComponentValueType(),
) = definedComponentValueType(ComponentDefinedValueType.ListValue(element))

fun fixedLengthListComponentValueType(
    element: ComponentValueType = primitiveComponentValueType(),
    length: UInt = 0u,
) = definedComponentValueType(ComponentDefinedValueType.FixedLengthList(element, length))

fun mapComponentValueType(
    key: ComponentPrimitiveType = ComponentPrimitiveType.String,
    value: ComponentValueType = primitiveComponentValueType(),
) = definedComponentValueType(ComponentDefinedValueType.Map(key, value))

fun tupleComponentValueType(
    elements: List<ComponentValueType> = emptyList(),
) = definedComponentValueType(ComponentDefinedValueType.Tuple(elements))

fun optionComponentValueType(
    value: ComponentValueType = primitiveComponentValueType(),
) = definedComponentValueType(ComponentDefinedValueType.Option(value))

fun resultComponentValueType(
    ok: ComponentValueType? = null,
    error: ComponentValueType? = null,
) = definedComponentValueType(ComponentDefinedValueType.Result(ok, error))

fun ownComponentValueType(
    id: ComponentTypeId = componentTypeId(),
    resource: ComponentResourceTypeId = componentResourceTypeId(),
) = definedComponentValueType(ComponentDefinedValueType.Own(id, resource))

fun borrowComponentValueType(
    id: ComponentTypeId = componentTypeId(),
    resource: ComponentResourceTypeId = componentResourceTypeId(),
) = definedComponentValueType(ComponentDefinedValueType.Borrow(id, resource))

fun streamComponentValueType(
    element: ComponentValueType? = null,
) = definedComponentValueType(ComponentDefinedValueType.Stream(element))

fun futureComponentValueType(
    value: ComponentValueType? = null,
) = definedComponentValueType(ComponentDefinedValueType.Future(value))

fun componentFunctionType(
    params: List<LabeledComponentValueType> = emptyList(),
    result: ComponentValueType? = null,
    async: Boolean = false,
) = ComponentFunctionType(
    params = params,
    result = result,
    async = async,
)

fun labeledComponentValueType(
    label: String = "label",
    type: ComponentValueType = primitiveComponentValueType(),
) = LabeledComponentValueType(
    label = label,
    type = type,
)

fun componentVariantCase(
    label: String = "case",
    type: ComponentValueType? = null,
) = ComponentVariantCase(
    label = label,
    type = type,
)
