package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.executor.instantiator.ModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.component.fixture.compiledModule
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExportProjection
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreFunctionSource
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImportValue
import io.github.charlietap.chasm.fixture.ast.component.aliasComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.backpressureSetCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueLiteral
import io.github.charlietap.chasm.fixture.ast.component.boolComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.canonComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.component
import io.github.charlietap.chasm.fixture.ast.component.componentIndex
import io.github.charlietap.chasm.fixture.ast.component.componentModuleIndex
import io.github.charlietap.chasm.fixture.ast.component.componentTypeIndex
import io.github.charlietap.chasm.fixture.ast.component.componentValue
import io.github.charlietap.chasm.fixture.ast.component.coreInstanceComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.coreInstanceExportAliasDefinition
import io.github.charlietap.chasm.fixture.ast.component.coreModuleComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.exportComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.functionCoreInstanceExportAliasTarget
import io.github.charlietap.chasm.fixture.ast.component.functionExportTarget
import io.github.charlietap.chasm.fixture.ast.component.functionExternalType
import io.github.charlietap.chasm.fixture.ast.component.functionTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.importComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.instanceComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.instantiateCoreInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.instantiateInstanceDefinition
import io.github.charlietap.chasm.fixture.ast.component.liftCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.lowerCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.memoryCanonicalOption
import io.github.charlietap.chasm.fixture.ast.component.memoryCoreInstanceExportAliasTarget
import io.github.charlietap.chasm.fixture.ast.component.moduleExportTarget
import io.github.charlietap.chasm.fixture.ast.component.nameAttributes
import io.github.charlietap.chasm.fixture.ast.component.nestedComponentComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.resourceTypeDefinition
import io.github.charlietap.chasm.fixture.ast.component.startComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.streamNewCanonicalDefinition
import io.github.charlietap.chasm.fixture.ast.component.stringComponentValueType
import io.github.charlietap.chasm.fixture.ast.component.typeComponentDefinition
import io.github.charlietap.chasm.fixture.ast.component.typeExportTarget
import io.github.charlietap.chasm.fixture.ast.component.typeExternalType
import io.github.charlietap.chasm.fixture.ast.component.valueComponentDefinition
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.ir.module.export
import io.github.charlietap.chasm.fixture.ir.module.functionExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.runtime.component.error.unsupportedFeatureComponentPreparationError
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceCounts
import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiDescriptor
import io.github.charlietap.chasm.fixture.type.component.componentFunctionEntityType
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.fixture.type.component.componentFunctionTypeDefinition
import io.github.charlietap.chasm.fixture.type.component.componentInstanceType
import io.github.charlietap.chasm.fixture.type.component.componentItemType
import io.github.charlietap.chasm.fixture.type.component.componentResourceEntityType
import io.github.charlietap.chasm.fixture.type.component.componentResourceTypeDefinition
import io.github.charlietap.chasm.fixture.type.component.componentResourceTypeId
import io.github.charlietap.chasm.fixture.type.component.componentScopeTypes
import io.github.charlietap.chasm.fixture.type.component.componentTypes
import io.github.charlietap.chasm.fixture.type.component.coreFunctionEntityType
import io.github.charlietap.chasm.fixture.type.component.coreMemoryEntityType
import io.github.charlietap.chasm.fixture.type.component.coreModuleComponentEntityType
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExportValue
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceCounts
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiContext
import io.github.charlietap.chasm.type.component.canonical.CanonicalCoreFunctionType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.ast.component.componentFunctionType as astComponentFunctionType
import io.github.charlietap.chasm.fixture.ast.module.module as astModule
import io.github.charlietap.chasm.fixture.ir.module.module as irModule
import io.github.charlietap.chasm.fixture.ir.value.nameValue as irNameValue
import io.github.charlietap.chasm.fixture.type.component.componentType as componentTypeFixture
import io.github.charlietap.chasm.fixture.type.component.coreInstanceType as coreInstanceTypeFixture
import io.github.charlietap.chasm.fixture.type.component.coreModuleType as coreModuleTypeFixture

class ComponentPlannerTest {

    @Test
    fun `plans definitions in source order and compiles a nested module once`() {
        val config = runtimeConfig()
        val module = astModule()
        val compiled = compiledModule()
        val coreModuleType = coreModuleTypeFixture()
        val nestedTypes = componentScopeTypes(
            type = componentTypeFixture(
                exports = linkedMapOf("module" to coreModuleComponentEntityType(coreModuleType)),
            ),
            coreModules = listOf(coreModuleType, coreModuleType),
            coreInstances = listOf(coreInstanceTypeFixture(), coreInstanceTypeFixture()),
        )
        val types = componentTypes(
            componentScopeTypes(
                components = listOf(componentItemType(nestedTypes.type, nestedTypes)),
                instances = listOf(componentInstanceType()),
            ),
        )
        val nested = component(
            definitions = listOf(
                coreModuleComponentDefinition(module),
                coreInstanceComponentDefinition(
                    instantiateCoreInstanceDefinition(componentModuleIndex(0u)),
                ),
                exportComponentDefinition(
                    name = nameAttributes(nameValue("module")),
                    target = moduleExportTarget(componentModuleIndex(0u)),
                ),
                coreInstanceComponentDefinition(
                    instantiateCoreInstanceDefinition(componentModuleIndex(1u)),
                ),
            ),
        )
        val root = component(
            definitions = listOf(
                nestedComponentComponentDefinition(nested),
                instanceComponentDefinition(
                    instantiateInstanceDefinition(componentIndex(0u)),
                ),
            ),
        )
        val compiledInputs = mutableListOf<io.github.charlietap.chasm.ast.module.Module>()
        val compiler: ModuleCompiler = { _, input ->
            compiledInputs += input
            Ok(compiled)
        }

        val prepared = ComponentPlanner(config, root, types, compiler).unwrap()
        val actual = PlannerObservation(
            compiledInputs = compiledInputs,
            moduleCount = prepared.modules.size,
            initializers = prepared.initializers.map { initializer ->
                val instantiate = initializer as ComponentInitializer.InstantiateCoreModule
                val source = instantiate.module as PreparedCoreModuleSource.Embedded
                source.moduleIndex to instantiate.instance.index
            },
            counts = prepared.counts,
        )

        val expected = PlannerObservation(
            compiledInputs = listOf(module),
            moduleCount = 1,
            initializers = listOf(
                0 to 0,
                0 to 1,
            ),
            counts = componentInstanceCounts(componentInstances = 2, coreInstances = 2),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `assigns each static nested instantiation a unique logical index`() {
        val nestedTypes = componentScopeTypes()
        val types = componentTypes(
            componentScopeTypes(
                components = listOf(componentItemType(nestedTypes.type, nestedTypes)),
                instances = listOf(componentInstanceType(), componentInstanceType()),
            ),
        )
        val root = component(
            definitions = listOf(
                nestedComponentComponentDefinition(component()),
                instanceComponentDefinition(instantiateInstanceDefinition(componentIndex(0u))),
                instanceComponentDefinition(instantiateInstanceDefinition(componentIndex(0u))),
            ),
        )
        var compilationCount = 0
        val compiler: ModuleCompiler = { _, _ ->
            compilationCount += 1
            Ok(compiledModule())
        }

        val prepared = ComponentPlanner(runtimeConfig(), root, types, compiler).unwrap()
        val actual = NestedInstantiationObservation(
            componentInstances = prepared.counts.componentInstances,
            parents = prepared.componentInstanceParents.toList(),
            compilationCount = compilationCount,
        )

        val expected = NestedInstantiationObservation(
            componentInstances = 3,
            parents = listOf(-1, 0, 0),
            compilationCount = 0,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `compiles canonical lift metadata from a core function alias`() {
        val functionType = componentFunctionType(
            result = primitiveComponentValueType(ComponentPrimitiveType.String),
        )
        val descriptor = canonicalAbiDescriptor(functionType, CanonicalAbiContext.Lift)
        val coreFunctionType = descriptor.type
        val coreMemoryType = memoryType()
        val coreModuleType = coreModuleTypeFixture(
            exports = linkedMapOf(
                "run" to coreFunctionEntityType(coreFunctionType),
                "memory" to coreMemoryEntityType(coreMemoryType),
            ),
        )
        val module = astModule()
        val compiled = compiledModule(
            irModule(
                exports = listOf(
                    export(
                        name = irNameValue("run"),
                        descriptor = functionExportDescriptor(),
                    ),
                    export(
                        name = irNameValue("memory"),
                        descriptor = memoryExportDescriptor(),
                    ),
                ),
            ),
        )
        val component = component(
            definitions = listOf(
                coreModuleComponentDefinition(module),
                coreInstanceComponentDefinition(
                    instantiateCoreInstanceDefinition(componentModuleIndex()),
                ),
                aliasComponentDefinition(
                    coreInstanceExportAliasDefinition(
                        functionCoreInstanceExportAliasTarget(name = nameValue("run")),
                    ),
                ),
                aliasComponentDefinition(
                    coreInstanceExportAliasDefinition(
                        memoryCoreInstanceExportAliasTarget(name = nameValue("memory")),
                    ),
                ),
                typeComponentDefinition(
                    functionTypeDefinition(
                        astComponentFunctionType(result = stringComponentValueType()),
                    ),
                ),
                canonComponentDefinition(
                    liftCanonicalDefinition(options = listOf(memoryCanonicalOption())),
                ),
                exportComponentDefinition(
                    name = nameAttributes(nameValue("run")),
                    target = functionExportTarget(),
                ),
            ),
        )
        val types = componentTypes(
            componentScopeTypes(
                type = componentTypeFixture(
                    exports = mapOf("run" to componentFunctionEntityType(functionType)),
                ),
                coreFunctions = listOf(coreFunctionType),
                coreMemories = listOf(coreMemoryType),
                coreModules = listOf(coreModuleType),
                coreInstances = listOf(coreInstanceTypeFixture(coreModuleType.exports)),
                types = listOf(componentFunctionTypeDefinition(type = functionType)),
                functions = listOf(functionType),
                canonicalAbi = listOf(descriptor),
            ),
        )
        val compiler: ModuleCompiler = { _, _ -> Ok(compiled) }

        val prepared = ComponentPlanner(runtimeConfig(), component, types, compiler).unwrap()
        val instantiate = prepared.initializers[0] as ComponentInitializer.InstantiateCoreModule
        val moduleSource = instantiate.module as PreparedCoreModuleSource.Embedded
        val extractMemory = prepared.initializers[1] as ComponentInitializer.ExtractMemory
        val memoryProjection = extractMemory.memory.projection as PreparedCoreExportProjection.Direct
        val extract = prepared.initializers[2] as ComponentInitializer.ExtractCoreFunction
        val functionSource = extract.function.source as PreparedCoreFunctionSource.Export
        val projection = functionSource.projection as PreparedCoreExportProjection.Direct
        val function = prepared.runtimeInfo.functions.single() as PreparedComponentFunction.LiftedCore
        val callPlan = prepared.runtimeInfo.callPlans.single() as LinearMemoryLiftPlan
        val layout = prepared.runtimeInfo.linearMemoryLayouts.single()
        val export = prepared.runtimeInfo.exports.single()
        val exportValue = export.value as PreparedComponentExportValue.Function
        val actual = CanonicalLiftObservation(
            moduleCount = prepared.modules.size,
            moduleIndex = moduleSource.moduleIndex,
            coreInstance = instantiate.instance.index,
            extractedInstance = functionSource.instance.index,
            extractedExport = projection.index,
            extractedSlot = extract.slot.index,
            extractedMemoryInstance = extractMemory.memory.instance.index,
            extractedMemoryExport = memoryProjection.index,
            extractedMemorySlot = extractMemory.slot,
            functionSlot = function.liftPlan.coreFunctionSlot,
            callPlanSlot = callPlan.coreFunctionSlot,
            callPlanMemorySlot = callPlan.memorySlot,
            resultLayouts = callPlan.resultTuple.layouts.toList(),
            exportName = export.name,
            exportFunction = exportValue.function.index,
            layoutKind = layout.kind,
            counts = prepared.counts,
        )

        val expected = CanonicalLiftObservation(
            moduleCount = 1,
            moduleIndex = 0,
            coreInstance = 0,
            extractedInstance = 0,
            extractedExport = 0,
            extractedSlot = 0,
            extractedMemoryInstance = 0,
            extractedMemoryExport = 0,
            extractedMemorySlot = 0,
            functionSlot = 0,
            callPlanSlot = 0,
            callPlanMemorySlot = 0,
            resultLayouts = listOf(0),
            exportName = "run",
            exportFunction = 0,
            layoutKind = CanonicalLayoutKind.String,
            counts = componentInstanceCounts(
                componentInstances = 1,
                coreInstances = 1,
                coreFunctions = 1,
                memories = 1,
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `compiles canonical lower metadata for a component function import`() {
        val functionType = componentFunctionType()
        val coreFunctionType = CanonicalCoreFunctionType()
        val component = component(
            definitions = listOf(
                typeComponentDefinition(functionTypeDefinition()),
                importComponentDefinition(
                    name = nameAttributes(nameValue("host")),
                    type = functionExternalType(),
                ),
                canonComponentDefinition(lowerCanonicalDefinition()),
            ),
        )
        val types = componentTypes(
            componentScopeTypes(
                type = componentTypeFixture(
                    imports = mapOf("host" to componentFunctionEntityType(functionType)),
                ),
                coreFunctions = listOf(coreFunctionType),
                types = listOf(componentFunctionTypeDefinition(type = functionType)),
                functions = listOf(functionType),
                canonicalAbi = listOf(canonicalAbiDescriptor(type = coreFunctionType)),
            ),
        )
        val compiler: ModuleCompiler = { _, _ -> Ok(compiledModule()) }

        val prepared = ComponentPlanner(runtimeConfig(), component, types, compiler).unwrap()
        val initializer = prepared.initializers.single() as ComponentInitializer.LowerImport
        val function = prepared.runtimeInfo.functions.single() as PreparedComponentFunction.HostImport
        val callPlan = prepared.runtimeInfo.callPlans.single() as LinearMemoryLowerPlan
        val componentImport = prepared.imports.single()
        val importValue = componentImport.value as PreparedComponentImportValue.Function
        val actual = CanonicalLowerObservation(
            initializerFunction = initializer.function.index,
            initializerCallPlan = initializer.callPlan,
            hostImportSlot = function.importSlot,
            callPlanTarget = callPlan.targetFunctionSlot,
            importName = componentImport.name,
            importSlot = importValue.importIndex,
            layoutCount = prepared.runtimeInfo.linearMemoryLayouts.size,
            counts = prepared.counts,
        )

        val expected = CanonicalLowerObservation(
            initializerFunction = 0,
            initializerCallPlan = 0,
            hostImportSlot = 0,
            callPlanTarget = 0,
            importName = "host",
            importSlot = 0,
            layoutCount = 0,
            counts = componentInstanceCounts(
                componentInstances = 1,
                coreFunctions = 1,
                hostFunctions = 1,
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `plans declared and imported resource origins per component instance`() {
        val declaredResource = componentResourceTypeId(1u)
        val importedResource = componentResourceTypeId(2u)
        val declaredType = componentResourceTypeDefinition(resource = declaredResource)
        val importedType = componentResourceTypeDefinition(resource = importedResource)
        val component = component(
            definitions = listOf(
                typeComponentDefinition(resourceTypeDefinition()),
                exportComponentDefinition(
                    name = nameAttributes(nameValue("declared")),
                    target = typeExportTarget(),
                ),
                importComponentDefinition(
                    name = nameAttributes(nameValue("imported")),
                    type = typeExternalType(),
                ),
                exportComponentDefinition(
                    name = nameAttributes(nameValue("imported")),
                    target = typeExportTarget(componentTypeIndex(2u)),
                ),
            ),
        )
        val types = componentTypes(
            componentScopeTypes(
                type = componentTypeFixture(
                    imports = mapOf("imported" to componentResourceEntityType(importedType)),
                ),
                types = listOf(declaredType),
            ),
        )
        val compiler: ModuleCompiler = { _, _ -> Ok(compiledModule()) }

        val prepared = ComponentPlanner(runtimeConfig(), component, types, compiler).unwrap()
        val imported = prepared.imports.single().value as PreparedComponentImportValue.ResourceType
        val declaredExport = prepared.runtimeInfo.exports[0].value as PreparedComponentExportValue.ResourceType
        val importedExport = prepared.runtimeInfo.exports[1].value as PreparedComponentExportValue.ResourceType
        val actual = ResourceOriginObservation(
            declaredIndex = declaredExport.resourceType.index,
            importedIndex = imported.resourceType.index,
            importedExportIndex = importedExport.resourceType.index,
            counts = prepared.counts,
        )

        val expected = ResourceOriginObservation(
            declaredIndex = 0,
            importedIndex = 1,
            importedExportIndex = 1,
            counts = componentInstanceCounts(componentInstances = 1, resourceTypes = 2),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects deferred component execution features explicitly`() {
        val types = componentTypes()
        val compiler: ModuleCompiler = { _, _ -> Ok(compiledModule()) }
        val definitions = listOf(
            component(definitions = listOf(startComponentDefinition())),
            component(
                definitions = listOf(
                    valueComponentDefinition(
                        componentValue(
                            type = boolComponentValueType(),
                            value = boolComponentValueLiteral(),
                        ),
                    ),
                ),
            ),
            component(
                definitions = listOf(
                    canonComponentDefinition(streamNewCanonicalDefinition()),
                ),
            ),
            component(
                definitions = listOf(
                    canonComponentDefinition(backpressureSetCanonicalDefinition()),
                ),
            ),
        )

        val actual = definitions.map { definition ->
            ComponentPlanner(runtimeConfig(), definition, types, compiler)
        }

        val expected = listOf(
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.ComponentStart)),
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.ComponentValue)),
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.Stream)),
            Err(unsupportedFeatureComponentPreparationError(UnsupportedComponentFeature.Async)),
        )
        assertEquals(expected, actual)
    }
}

private data class PlannerObservation(
    val compiledInputs: List<io.github.charlietap.chasm.ast.module.Module>,
    val moduleCount: Int,
    val initializers: List<Pair<Int, Int>>,
    val counts: ComponentInstanceCounts,
)

private data class NestedInstantiationObservation(
    val componentInstances: Int,
    val parents: List<Int>,
    val compilationCount: Int,
)

private data class CanonicalLiftObservation(
    val moduleCount: Int,
    val moduleIndex: Int,
    val coreInstance: Int,
    val extractedInstance: Int,
    val extractedExport: Int,
    val extractedSlot: Int,
    val extractedMemoryInstance: Int,
    val extractedMemoryExport: Int,
    val extractedMemorySlot: Int,
    val functionSlot: Int,
    val callPlanSlot: Int,
    val callPlanMemorySlot: Int,
    val resultLayouts: List<Int>,
    val exportName: String,
    val exportFunction: Int,
    val layoutKind: CanonicalLayoutKind,
    val counts: ComponentInstanceCounts,
)

private data class CanonicalLowerObservation(
    val initializerFunction: Int,
    val initializerCallPlan: Int,
    val hostImportSlot: Int,
    val callPlanTarget: Int,
    val importName: String,
    val importSlot: Int,
    val layoutCount: Int,
    val counts: ComponentInstanceCounts,
)

private data class ResourceOriginObservation(
    val declaredIndex: Int,
    val importedIndex: Int,
    val importedExportIndex: Int,
    val counts: ComponentInstanceCounts,
)
