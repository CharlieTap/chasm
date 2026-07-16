package io.github.charlietap.chasm.executor.instantiator.component.linking

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.PreparedComponent
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.type.component.CoreEntityType
import io.github.charlietap.chasm.type.component.CoreInstanceType
import io.github.charlietap.chasm.type.component.CoreModuleType
import io.github.charlietap.chasm.type.component.CoreType
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher
import io.github.charlietap.chasm.type.matching.EmptyTypeMatcherContext
import io.github.charlietap.chasm.type.matching.FunctionTypeMatcher
import io.github.charlietap.chasm.type.matching.GlobalTypeMatcher
import io.github.charlietap.chasm.type.matching.MemoryTypeMatcher
import io.github.charlietap.chasm.type.matching.TableTypeMatcher

typealias ComponentLinker = (
    PreparedComponent,
    List<NamedComponentLinkInput>,
) -> Result<ResolvedComponentImports, ComponentInstantiationError>

fun ComponentLinker(
    component: PreparedComponent,
    imports: List<NamedComponentLinkInput>,
): Result<ResolvedComponentImports, ComponentInstantiationError> = ComponentLinker(
    component = component,
    imports = imports,
    coreModuleResolver = ::ResolveCoreModuleImport,
)

internal inline fun ComponentLinker(
    component: PreparedComponent,
    imports: List<NamedComponentLinkInput>,
    crossinline coreModuleResolver: (
        CoreModuleType,
        CompiledModule,
        List<String>,
    ) -> Result<ResolvedCoreModuleImport, ComponentInstantiationError>,
): Result<ResolvedComponentImports, ComponentInstantiationError> = linkComponent(
    component = component,
    imports = imports,
    coreModuleResolver = { expected, module, path -> coreModuleResolver(expected, module, path) },
)

@PublishedApi
internal fun linkComponent(
    component: PreparedComponent,
    imports: List<NamedComponentLinkInput>,
    coreModuleResolver: (
        CoreModuleType,
        CompiledModule,
        List<String>,
    ) -> Result<ResolvedCoreModuleImport, ComponentInstantiationError>,
): Result<ResolvedComponentImports, ComponentInstantiationError> = binding {
    val moduleCount = component.imports.maxOfOrNull { componentImport ->
        componentImport.maxCoreModuleImportIndex()
    }?.plus(1) ?: 0
    val modules = arrayOfNulls<ResolvedCoreModuleImport>(moduleCount)
    val functionCount = component.imports.maxOfOrNull { componentImport ->
        componentImport.maxFunctionImportIndex()
    }?.plus(1) ?: 0
    val functions = arrayOfNulls<RuntimeComponentHostFunction>(functionCount)
    val resourceTypeCount = component.imports.maxOfOrNull { componentImport ->
        componentImport.maxResourceTypeImportIndex()
    }?.plus(1) ?: 0
    val resourceTypes = IntArray(resourceTypeCount) { UNRESOLVED_RESOURCE_TYPE }

    resolveImports(
        expected = component.imports,
        actual = imports,
        path = emptyList(),
        modules = modules,
        functions = functions,
        resourceTypes = resourceTypes,
        coreModuleResolver = coreModuleResolver,
    ).bind()

    ResolvedComponentImports(
        coreModules = Array(modules.size) { index -> checkNotNull(modules[index]) },
        functions = Array(functions.size) { index -> checkNotNull(functions[index]) },
        resourceTypes = resourceTypes,
    )
}

private fun resolveImports(
    expected: List<PreparedComponentImport>,
    actual: List<NamedComponentLinkInput>,
    path: List<String>,
    modules: Array<ResolvedCoreModuleImport?>,
    functions: Array<RuntimeComponentHostFunction?>,
    resourceTypes: IntArray,
    coreModuleResolver: (
        CoreModuleType,
        CompiledModule,
        List<String>,
    ) -> Result<ResolvedCoreModuleImport, ComponentInstantiationError>,
): Result<Unit, ComponentInstantiationError> = binding {
    val actualByName = linkedMapOf<String, ComponentLinkInput>()
    actual.forEach { input ->
        val inputPath = path + input.name
        if (actualByName.put(input.name, input.value) != null) {
            Err(ComponentInstantiationError.UnexpectedImport(inputPath)).bind<Unit>()
        }
    }

    expected.forEach { componentImport ->
        val importPath = path + componentImport.name
        val input = actualByName.remove(componentImport.name)
        val expectedValue = componentImport.value
        if (
            input == null &&
            !expectedValue.mayBeOmitted() &&
            (
                expectedValue !is PreparedComponentImportValue.ResourceType ||
                    resourceTypes[expectedValue.resourceType.index] == UNRESOLVED_RESOURCE_TYPE
            )
        ) {
            Err(ComponentInstantiationError.MissingImport(importPath)).bind<Unit>()
        }

        when (expectedValue) {
            is PreparedComponentImportValue.CoreModule -> {
                val module = (input as? ComponentLinkInput.CoreModule)?.module
                    ?: Err(ComponentInstantiationError.ImportTypeMismatch(importPath)).bind()
                modules[expectedValue.importIndex] = coreModuleResolver(
                    expectedValue.type,
                    module,
                    importPath,
                ).bind()
            }
            is PreparedComponentImportValue.Instance -> {
                if (input == null) return@forEach
                val instance = input as? ComponentLinkInput.Instance
                    ?: Err(ComponentInstantiationError.ImportTypeMismatch(importPath)).bind()
                resolveImports(
                    expected = expectedValue.imports,
                    actual = instance.imports,
                    path = importPath,
                    modules = modules,
                    functions = functions,
                    resourceTypes = resourceTypes,
                    coreModuleResolver = coreModuleResolver,
                ).bind()
            }
            is PreparedComponentImportValue.Function -> {
                val function = input as? ComponentLinkInput.Function
                    ?: Err(ComponentInstantiationError.ImportTypeMismatch(importPath)).bind()
                val actualType = function.type
                if (
                    actualType != null &&
                    !ComponentFunctionTypeMatcher(actualType, expectedValue.type) { actual, expected ->
                        val actualAddress = function.resourceTypes[actual]
                        val expectedIndex = expectedValue.resourceTypes[expected]
                        actualAddress != null &&
                            expectedIndex != null &&
                            actualAddress.address == resourceTypes[expectedIndex.index]
                    }
                ) {
                    Err(ComponentInstantiationError.ImportTypeMismatch(importPath)).bind<Unit>()
                }
                functions[expectedValue.importIndex] = function.function
            }
            is PreparedComponentImportValue.ResourceType -> {
                val index = expectedValue.resourceType.index
                if (input == null && resourceTypes[index] != UNRESOLVED_RESOURCE_TYPE) return@forEach
                val resourceType = input as? ComponentLinkInput.ResourceType
                    ?: Err(ComponentInstantiationError.ImportTypeMismatch(importPath)).bind()
                val current = resourceTypes[index]
                if (current != UNRESOLVED_RESOURCE_TYPE && current != resourceType.address.address) {
                    Err(ComponentInstantiationError.ImportTypeMismatch(importPath)).bind<Unit>()
                }
                resourceTypes[index] = resourceType.address.address
            }
        }
    }
}

private fun PreparedComponentImportValue.mayBeOmitted(): Boolean =
    this is PreparedComponentImportValue.Instance && imports.all { componentImport ->
        componentImport.value.mayBeOmitted()
    }

internal fun ResolveCoreModuleImport(
    expected: CoreModuleType,
    module: CompiledModule,
    path: List<String>,
): Result<ResolvedCoreModuleImport, ComponentInstantiationError> {
    val actual = module.componentLinkShape
        ?: return Err(ComponentInstantiationError.ImportTypeMismatch(path))
    val actualType = actual.type
        ?: return Err(ComponentInstantiationError.ImportTypeMismatch(path))
    if (!coreModuleTypeMatches(actualType, expected)) {
        return Err(ComponentInstantiationError.ImportTypeMismatch(path))
    }

    val expectedImportIndexes = expected.imports.keys.withIndex().associate { (index, name) -> name to index }
    val importIndexes = IntArray(actualType.imports.size)
    actualType.imports.keys.forEachIndexed { index, name ->
        importIndexes[index] = expectedImportIndexes[name]
            ?: return Err(ComponentInstantiationError.ImportTypeMismatch(path))
    }
    val exportIndexes = IntArray(expected.exports.size)
    expected.exports.keys.forEachIndexed { index, name ->
        val export = actual.exports[name]
            ?: return Err(ComponentInstantiationError.ImportTypeMismatch(path))
        exportIndexes[index] = export.index()
    }

    return Ok(
        ResolvedCoreModuleImport(
            module = module,
            importIndexes = importIndexes,
            exportIndexes = exportIndexes,
        ),
    )
}

private fun PreparedComponentImport.maxCoreModuleImportIndex(): Int = when (val value = value) {
    is PreparedComponentImportValue.CoreModule -> value.importIndex
    is PreparedComponentImportValue.Instance -> value.imports.maxOfOrNull { componentImport ->
        componentImport.maxCoreModuleImportIndex()
    } ?: -1
    is PreparedComponentImportValue.Function,
    is PreparedComponentImportValue.ResourceType,
    -> -1
}

private fun PreparedComponentImport.maxFunctionImportIndex(): Int = when (val value = value) {
    is PreparedComponentImportValue.Function -> value.importIndex
    is PreparedComponentImportValue.Instance -> value.imports.maxOfOrNull { componentImport ->
        componentImport.maxFunctionImportIndex()
    } ?: -1
    is PreparedComponentImportValue.CoreModule,
    is PreparedComponentImportValue.ResourceType,
    -> -1
}

private fun PreparedComponentImport.maxResourceTypeImportIndex(): Int = when (val value = value) {
    is PreparedComponentImportValue.ResourceType -> value.resourceType.index
    is PreparedComponentImportValue.Instance -> value.imports.maxOfOrNull { componentImport ->
        componentImport.maxResourceTypeImportIndex()
    } ?: -1
    is PreparedComponentImportValue.CoreModule,
    is PreparedComponentImportValue.Function,
    -> -1
}

private fun coreModuleTypeMatches(
    actual: CoreModuleType,
    expected: CoreModuleType,
): Boolean {
    for ((name, actualImport) in actual.imports) {
        val expectedImport = expected.imports[name] ?: return false
        if (!coreEntityTypeMatches(expectedImport, actualImport)) return false
    }
    for ((name, expectedExport) in expected.exports) {
        val actualExport = actual.exports[name] ?: return false
        if (!coreEntityTypeMatches(actualExport, expectedExport)) return false
    }
    return true
}

private fun coreInstanceTypeMatches(
    actual: CoreInstanceType,
    expected: CoreInstanceType,
): Boolean {
    for ((name, expectedExport) in expected.exports) {
        val actualExport = actual.exports[name] ?: return false
        if (!coreEntityTypeMatches(actualExport, expectedExport)) return false
    }
    return true
}

private fun coreEntityTypeMatches(
    actual: CoreEntityType,
    expected: CoreEntityType,
): Boolean = when {
    actual is CoreEntityType.Function && expected is CoreEntityType.Function ->
        DefinedTypeMatcher(actual.type, expected.type, EmptyTypeMatcherContext)

    actual is CoreEntityType.Table && expected is CoreEntityType.Table ->
        actual.type.addressType == expected.type.addressType &&
            TableTypeMatcher(actual.type, expected.type, EmptyTypeMatcherContext)

    actual is CoreEntityType.Memory && expected is CoreEntityType.Memory ->
        actual.type.addressType == expected.type.addressType &&
            actual.type.shared == expected.type.shared &&
            MemoryTypeMatcher(actual.type, expected.type, EmptyTypeMatcherContext)

    actual is CoreEntityType.Global && expected is CoreEntityType.Global ->
        GlobalTypeMatcher(actual.type, expected.type, EmptyTypeMatcherContext)

    actual is CoreEntityType.Tag && expected is CoreEntityType.Tag ->
        actual.type.attribute == expected.type.attribute &&
            FunctionTypeMatcher(actual.type.functionType, expected.type.functionType, EmptyTypeMatcherContext)

    actual is CoreEntityType.Type && expected is CoreEntityType.Type -> {
        val actualType = actual.type
        val expectedType = expected.type
        when {
            actualType is CoreType.Defined && expectedType is CoreType.Defined ->
                DefinedTypeMatcher(actualType.type, expectedType.type, EmptyTypeMatcherContext)

            actualType is CoreType.Module && expectedType is CoreType.Module ->
                coreModuleTypeMatches(actualType.type, expectedType.type)

            else -> false
        }
    }

    actual is CoreEntityType.Module && expected is CoreEntityType.Module ->
        coreModuleTypeMatches(actual.type, expected.type)

    actual is CoreEntityType.Instance && expected is CoreEntityType.Instance ->
        coreInstanceTypeMatches(actual.type, expected.type)

    else -> false
}

private const val UNRESOLVED_RESOURCE_TYPE = -1
