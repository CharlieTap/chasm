package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.component.CanonicalOption
import io.github.charlietap.chasm.ast.component.ComponentStringEncoding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.canonical.Memory32LayoutCompiler
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExportProjection
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreFunctionSource
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImport
import io.github.charlietap.chasm.executor.instantiator.component.linking.PreparedComponentImportValue
import io.github.charlietap.chasm.executor.instantiator.component.linking.index
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerComponentInstanceReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerCoreExportReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerCoreModuleReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerExternalReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerFrame
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerTypeReference
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalCallPlan
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryCanonicalOptions
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.function.ComponentEntryPolicy
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.index.PreparedComponentFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreFunctionIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeCoreInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExport
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentEntityType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.CoreEntityType

internal class ComponentPlanningContext(
    val config: RuntimeConfig,
) {
    val modules = mutableListOf<CompiledModule>()
    val initializers = mutableListOf<ComponentInitializer>()
    val imports = mutableListOf<PreparedComponentImport>()
    val exports = mutableListOf<PreparedComponentExport>()
    val functions = mutableListOf<PreparedComponentFunction>()
    val layoutCompiler = Memory32LayoutCompiler()
    val callPlans = mutableListOf<CanonicalCallPlan>()
    val componentInstanceParents = mutableListOf<Int>()
    val resourceTypes = linkedMapOf<ComponentResourceTypeId, RuntimeResourceTypeIndex>()

    var componentInstanceCount: Int = 0
        private set
    var coreInstanceCount: Int = 0
        private set
    var coreFunctionCount: Int = 0
        private set
    var memoryCount: Int = 0
        private set
    var reallocCount: Int = 0
        private set
    var postReturnCount: Int = 0
        private set
    var resourceTypeCount: Int = 0
        private set
    var hostFunctionCount: Int = 0
        private set
    private var coreModuleImportCount: Int = 0
    private val extractedCoreFunctions = mutableMapOf<PreparedCoreExternalValue.Function, RuntimeCoreFunctionIndex>()
    private val extractedMemories = mutableMapOf<PreparedCoreExternalValue.Memory, Int>()
    private val extractedReallocs = mutableMapOf<PreparedCoreExternalValue.Function, Int>()
    private val extractedPostReturns = mutableMapOf<PreparedCoreExternalValue.Function, Int>()

    fun nextComponentInstance(
        parent: RuntimeComponentInstanceIndex? = null,
    ): RuntimeComponentInstanceIndex = RuntimeComponentInstanceIndex(componentInstanceCount++).also {
        componentInstanceParents += parent?.index ?: ABSENT_COMPONENT_INSTANCE
    }

    fun nextCoreInstance() = RuntimeCoreInstanceIndex(coreInstanceCount++)

    fun nextCoreFunction() = RuntimeCoreFunctionIndex(coreFunctionCount++)

    fun resourceType(id: ComponentResourceTypeId): RuntimeResourceTypeIndex = resourceTypes.getOrPut(id) {
        RuntimeResourceTypeIndex(resourceTypeCount++)
    }

    fun hostEntryPolicy(callee: RuntimeComponentInstanceIndex): ComponentEntryPolicy {
        return entryPolicy(null, callee)
    }

    fun entryPolicy(
        caller: RuntimeComponentInstanceIndex?,
        callee: RuntimeComponentInstanceIndex,
    ): ComponentEntryPolicy {
        val ancestor = commonAncestor(caller?.index ?: ABSENT_COMPONENT_INSTANCE, callee.index)
        var current = callee.index
        var count = 0
        while (current != ancestor) {
            count += 1
            current = componentInstanceParents[current]
        }

        current = callee.index
        return ComponentEntryPolicy(
            IntArray(count) {
                current.also { current = componentInstanceParents[current] }
            },
        )
    }

    private fun commonAncestor(first: Int, second: Int): Int {
        if (first == ABSENT_COMPONENT_INSTANCE || second == ABSENT_COMPONENT_INSTANCE) {
            return ABSENT_COMPONENT_INSTANCE
        }

        var left = first
        var right = second
        var leftDepth = depth(left)
        var rightDepth = depth(right)
        while (leftDepth > rightDepth) {
            left = componentInstanceParents[left]
            leftDepth -= 1
        }
        while (rightDepth > leftDepth) {
            right = componentInstanceParents[right]
            rightDepth -= 1
        }
        while (left != right) {
            left = componentInstanceParents[left]
            right = componentInstanceParents[right]
        }
        return left
    }

    private fun depth(instance: Int): Int {
        var depth = 0
        var current = instance
        while (current != ABSENT_COMPONENT_INSTANCE) {
            depth += 1
            current = componentInstanceParents[current]
        }
        return depth
    }

    fun extractCoreFunction(function: PreparedCoreExternalValue.Function): RuntimeCoreFunctionIndex =
        extractedCoreFunctions.getOrPut(function) {
            nextCoreFunction().also { slot ->
                initializers += ComponentInitializer.ExtractCoreFunction(function, slot)
            }
        }

    private fun extractMemory(memory: PreparedCoreExternalValue.Memory): Int =
        extractedMemories.getOrPut(memory) {
            memoryCount++.also { slot ->
                initializers += ComponentInitializer.ExtractMemory(memory, slot)
            }
        }

    private fun extractRealloc(function: PreparedCoreExternalValue.Function): Int =
        extractedReallocs.getOrPut(function) {
            reallocCount++.also { slot ->
                initializers += ComponentInitializer.ExtractRealloc(function, slot)
            }
        }

    private fun extractPostReturn(function: PreparedCoreExternalValue.Function): Int =
        extractedPostReturns.getOrPut(function) {
            postReturnCount++.also { slot ->
                initializers += ComponentInitializer.ExtractPostReturn(function, slot)
            }
        }

    fun canonicalOptions(
        frame: PlannerFrame,
        options: List<CanonicalOption>,
    ): Result<LinearMemoryCanonicalOptions, ComponentPreparationError> = binding {
        var encoding = CanonicalStringEncoding.Utf8
        var memorySlot = ABSENT_CANONICAL_SLOT
        var reallocSlot = ABSENT_CANONICAL_SLOT
        var postReturnSlot = ABSENT_CANONICAL_SLOT
        var hasEncoding = false

        options.forEach { option ->
            when (option) {
                is CanonicalOption.StringEncoding -> {
                    if (hasEncoding) invalidCanonicalOptions("duplicate string encoding option").let { Err(it).bind<Unit>() }
                    hasEncoding = true
                    encoding = when (option.encoding) {
                        ComponentStringEncoding.Utf8 -> CanonicalStringEncoding.Utf8
                        ComponentStringEncoding.Utf16 -> CanonicalStringEncoding.Utf16
                        ComponentStringEncoding.Latin1Utf16 -> CanonicalStringEncoding.Latin1Utf16
                    }
                }
                is CanonicalOption.Memory -> {
                    if (memorySlot != ABSENT_CANONICAL_SLOT) {
                        invalidCanonicalOptions("duplicate memory option").let { Err(it).bind<Unit>() }
                    }
                    val memory = frame.coreMemories.getOrNull(option.index.idx.toInt())
                        .toResultOr { invalidPreparation("canonical memory option references an unknown memory") }
                        .bind()
                    memorySlot = extractMemory(memory)
                }
                is CanonicalOption.Realloc -> {
                    if (reallocSlot != ABSENT_CANONICAL_SLOT) {
                        invalidCanonicalOptions("duplicate realloc option").let { Err(it).bind<Unit>() }
                    }
                    val realloc = frame.coreFunctions.getOrNull(option.index.idx.toInt())
                        .toResultOr { invalidPreparation("canonical realloc option references an unknown function") }
                        .bind()
                    reallocSlot = extractRealloc(realloc)
                }
                is CanonicalOption.PostReturn -> {
                    if (postReturnSlot != ABSENT_CANONICAL_SLOT) {
                        invalidCanonicalOptions("duplicate post-return option").let { Err(it).bind<Unit>() }
                    }
                    val postReturn = frame.coreFunctions.getOrNull(option.index.idx.toInt())
                        .toResultOr { invalidPreparation("canonical post-return option references an unknown function") }
                        .bind()
                    postReturnSlot = extractPostReturn(postReturn)
                }
                CanonicalOption.Async,
                is CanonicalOption.Callback,
                -> unsupported(UnsupportedComponentFeature.Async).bind<Unit>()
            }
        }

        LinearMemoryCanonicalOptions(
            optionOwner = frame.owner,
            encoding = encoding,
            memorySlot = memorySlot,
            reallocSlot = reallocSlot,
            postReturnSlot = postReturnSlot,
        )
    }

    fun prepareImport(
        name: String,
        type: ComponentEntityType,
        owner: RuntimeComponentInstanceIndex,
        resourceTypes: MutableMap<ComponentResourceTypeId, RuntimeResourceTypeIndex> = linkedMapOf(),
    ): Result<PreparedImportReference, ComponentPreparationError> = binding {
        when (type) {
            is ComponentEntityType.CoreModule -> {
                val importIndex = coreModuleImportCount++
                PreparedImportReference(
                    import = PreparedComponentImport(
                        name,
                        PreparedComponentImportValue.CoreModule(importIndex, type.type),
                    ),
                    reference = PlannerExternalReference.CoreModule(
                        PlannerCoreModuleReference(
                            type = type.type,
                            source = PreparedCoreModuleSource.Import(importIndex),
                        ),
                    ),
                )
            }
            is ComponentEntityType.Function -> {
                if (type.type.async) unsupported(UnsupportedComponentFeature.Async).bind<Unit>()
                val importIndex = hostFunctionCount++
                val functionIndex = PreparedComponentFunctionIndex(functions.size)
                val parameterLayouts = IntArray(type.type.params.size) { index ->
                    layoutCompiler.compile(type.type.params[index].type, resourceTypes::get).bind().index
                }
                val resultLayouts = type.type.result?.let { result ->
                    intArrayOf(layoutCompiler.compile(result, resourceTypes::get).bind().index)
                } ?: intArrayOf()
                val preparedHostCompatible = (parameterLayouts + resultLayouts).all { layout ->
                    layoutCompiler[layout].properties.let { properties ->
                        !properties.containsString && !properties.containsList && !properties.containsResource
                    }
                }
                functions += PreparedComponentFunction.HostImport(
                    importSlot = importIndex,
                    owner = owner,
                    functionType = type.type,
                    parameterTuple = layoutCompiler.tuple(parameterLayouts),
                    resultTuple = layoutCompiler.tuple(resultLayouts),
                    preparedHostCompatible = preparedHostCompatible,
                    entryPolicy = hostEntryPolicy(owner),
                )
                PreparedImportReference(
                    import = PreparedComponentImport(
                        name,
                        PreparedComponentImportValue.Function(
                            importIndex = importIndex,
                            type = type.type,
                            resourceTypes = resourceTypes.toMap(),
                        ),
                    ),
                    reference = PlannerExternalReference.Function(functionIndex),
                )
            }
            is ComponentEntityType.Instance -> {
                val imports = mutableListOf<PreparedComponentImport>()
                val references = linkedMapOf<String, PlannerExternalReference>()
                type.type.exports.forEach { (exportName, exportType) ->
                    val prepared = prepareImport(exportName, exportType, owner, resourceTypes).bind()
                    prepared.import?.let(imports::add)
                    references[exportName] = prepared.reference
                }
                PreparedImportReference(
                    import = PreparedComponentImport(name, PreparedComponentImportValue.Instance(imports)),
                    reference = PlannerExternalReference.Instance(
                        PlannerComponentInstanceReference(owner, references),
                    ),
                )
            }
            is ComponentEntityType.Type -> {
                val resource = type.referenced.type as? ComponentDefinedType.Resource
                val resourceType = resource?.let { value ->
                    resourceTypes.getOrPut(value.id) { resourceType(value.id) }
                }
                PreparedImportReference(
                    import = resource?.let {
                        PreparedComponentImport(
                            name,
                            PreparedComponentImportValue.ResourceType(checkNotNull(resourceType), resource.id),
                        )
                    },
                    reference = PlannerExternalReference.Type(
                        PlannerTypeReference(type.referenced, resourceType),
                    ),
                )
            }
            is ComponentEntityType.Value -> unsupported(UnsupportedComponentFeature.ComponentValue).bind()
            is ComponentEntityType.Component -> unsupported(
                UnsupportedComponentFeature.DynamicComponentInstantiation,
            ).bind()
        }
    }

    fun moduleExports(
        module: PlannerCoreModuleReference,
        runtimeIndex: RuntimeCoreInstanceIndex,
    ): Result<Map<String, PlannerCoreExportReference>, ComponentPreparationError> = binding {
        val exports = linkedMapOf<String, PlannerCoreExportReference>()
        module.type.exports.entries.forEachIndexed { exportIndex, (name, type) ->
            val projection = when (val source = module.source) {
                is PreparedCoreModuleSource.Embedded -> {
                    val compiled = modules[source.moduleIndex]
                    val export = compiled.componentLinkShape?.exports?.get(name)
                        .toResultOr { invalidPreparation("compiled core module has no export $name") }
                        .bind()
                    PreparedCoreExportProjection.Direct(export.index())
                }
                is PreparedCoreModuleSource.Import -> PreparedCoreExportProjection.Imported(
                    moduleImportIndex = source.importIndex,
                    exportIndex = exportIndex,
                )
            }
            val external = when (type) {
                is CoreEntityType.Function -> PlannerCoreExportReference.Function(
                    PreparedCoreExternalValue.Function(
                        PreparedCoreFunctionSource.Export(runtimeIndex, projection),
                    ),
                )
                is CoreEntityType.Table -> PlannerCoreExportReference.Table(
                    PreparedCoreExternalValue.Table(runtimeIndex, projection),
                )
                is CoreEntityType.Memory -> PlannerCoreExportReference.Memory(
                    PreparedCoreExternalValue.Memory(runtimeIndex, projection),
                )
                is CoreEntityType.Global -> PlannerCoreExportReference.Global(
                    PreparedCoreExternalValue.Global(runtimeIndex, projection),
                )
                is CoreEntityType.Tag -> PlannerCoreExportReference.Tag(
                    PreparedCoreExternalValue.Tag(runtimeIndex, projection),
                )
                is CoreEntityType.Type,
                is CoreEntityType.Module,
                is CoreEntityType.Instance,
                -> invalidPreparation("unsupported core module export $name").let { error -> Err(error).bind() }
            }
            exports[name] = external
        }
        exports
    }
}

internal data class PreparedImportReference(
    val import: PreparedComponentImport?,
    val reference: PlannerExternalReference,
)

private const val ABSENT_CANONICAL_SLOT = -1
private const val ABSENT_COMPONENT_INSTANCE = -1
