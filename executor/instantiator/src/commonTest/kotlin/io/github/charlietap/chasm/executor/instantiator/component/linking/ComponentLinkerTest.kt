package io.github.charlietap.chasm.executor.instantiator.component.linking

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.fixture.compiledModule
import io.github.charlietap.chasm.executor.instantiator.component.fixture.coreModulePreparedComponentImportValue
import io.github.charlietap.chasm.executor.instantiator.component.fixture.functionPreparedComponentImportValue
import io.github.charlietap.chasm.executor.instantiator.component.fixture.instancePreparedComponentImportValue
import io.github.charlietap.chasm.executor.instantiator.component.fixture.preparedComponent
import io.github.charlietap.chasm.executor.instantiator.component.fixture.preparedComponentImport
import io.github.charlietap.chasm.executor.instantiator.component.fixture.resolvedCoreModuleImport
import io.github.charlietap.chasm.executor.instantiator.component.fixture.resourceTypePreparedComponentImportValue
import io.github.charlietap.chasm.fixture.executor.instantiator.component.linking.coreModuleComponentLinkInput
import io.github.charlietap.chasm.fixture.executor.instantiator.component.linking.functionComponentLinkInput
import io.github.charlietap.chasm.fixture.executor.instantiator.component.linking.instanceComponentLinkInput
import io.github.charlietap.chasm.fixture.executor.instantiator.component.linking.namedComponentLinkInput
import io.github.charlietap.chasm.fixture.executor.instantiator.component.linking.resourceTypeComponentLinkInput
import io.github.charlietap.chasm.fixture.ir.module.export
import io.github.charlietap.chasm.fixture.ir.module.memory
import io.github.charlietap.chasm.fixture.ir.module.memoryExportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.memoryImportDescriptor
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.error.importTypeMismatchComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.component.error.missingImportComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.component.function.runtimeComponentHostFunction
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.fixture.type.component.componentResourceTypeId
import io.github.charlietap.chasm.fixture.type.component.coreMemoryEntityType
import io.github.charlietap.chasm.fixture.type.component.coreModuleType
import io.github.charlietap.chasm.fixture.type.component.labeledComponentValueType
import io.github.charlietap.chasm.fixture.type.component.ownComponentValueType
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.ir.value.NameValue
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.CoreImportName
import io.github.charlietap.chasm.type.component.CoreModuleType
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.fixture.ir.module.import as moduleImport

class ComponentLinkerTest {

    @Test
    fun `resolves a core module through hierarchical instance inputs`() {
        val compiled = compiledModule()
        val moduleType = coreModuleType()
        val resolved = resolvedCoreModuleImport(module = compiled)
        val component = preparedComponent(
            imports = listOf(
                preparedComponentImport(
                    name = "wasi",
                    value = instancePreparedComponentImportValue(
                        imports = listOf(
                            preparedComponentImport(
                                name = "module",
                                value = coreModulePreparedComponentImportValue(type = moduleType),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val imports = listOf(
            namedComponentLinkInput(
                name = "wasi",
                value = instanceComponentLinkInput(
                    listOf(
                        namedComponentLinkInput(
                            name = "module",
                            value = coreModuleComponentLinkInput(compiled),
                        ),
                    ),
                ),
            ),
        )
        val resolverCalls = mutableListOf<CoreModuleResolutionObservation>()
        val coreModuleResolver: CoreModuleResolver = { expected, module, path ->
            resolverCalls += CoreModuleResolutionObservation(expected, module, path)
            Ok(resolved)
        }
        val subject = componentLinker(coreModuleResolver)

        val linked = subject(component, imports).unwrap()
        val actual = ComponentLinkingObservation(
            modules = linked.coreModules.map { module -> module.module },
            resolverCalls = resolverCalls,
        )

        val expected = ComponentLinkingObservation(
            modules = listOf(compiled),
            resolverCalls = listOf(
                CoreModuleResolutionObservation(moduleType, compiled, listOf("wasi", "module")),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `reports the complete path for a missing nested input`() {
        val component = preparedComponent(
            imports = listOf(
                preparedComponentImport(
                    name = "wasi",
                    value = instancePreparedComponentImportValue(
                        imports = listOf(
                            preparedComponentImport(
                                name = "module",
                                value = coreModulePreparedComponentImportValue(),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val coreModuleResolver: CoreModuleResolver = { _, _, _ ->
            error("core module resolution must not run")
        }
        val subject = componentLinker(coreModuleResolver)

        val actual = subject(
            component,
            listOf(
                namedComponentLinkInput(
                    name = "wasi",
                    value = instanceComponentLinkInput(),
                ),
            ),
        )

        val expected = Err(missingImportComponentInstantiationError(listOf("wasi", "module")))
        assertEquals(expected, actual)
    }

    @Test
    fun `resolves a component host function into its dense runtime slot`() {
        val component = preparedComponent(
            imports = listOf(
                preparedComponentImport(
                    name = "function",
                    value = functionPreparedComponentImportValue(),
                ),
            ),
        )
        val coreModuleResolver: CoreModuleResolver = { _, _, _ ->
            error("core module resolution must not run")
        }
        val function = runtimeComponentHostFunction()
        val subject = componentLinker(coreModuleResolver)

        val linked = subject(
            component,
            listOf(
                namedComponentLinkInput(
                    name = "function",
                    value = functionComponentLinkInput(function),
                ),
            ),
        ).unwrap()
        val actual = linked.functions.single() === function

        val expected = true
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects a linked component function with an incompatible type`() {
        val expectedType = componentFunctionType(
            params = listOf(
                labeledComponentValueType(type = primitiveComponentValueType(ComponentPrimitiveType.U32)),
            ),
        )
        val actualType = componentFunctionType(
            params = listOf(
                labeledComponentValueType(type = primitiveComponentValueType(ComponentPrimitiveType.String)),
            ),
        )
        val component = preparedComponent(
            imports = listOf(
                preparedComponentImport(
                    name = "function",
                    value = functionPreparedComponentImportValue(type = expectedType),
                ),
            ),
        )
        val imports = listOf(
            namedComponentLinkInput(
                name = "function",
                value = functionComponentLinkInput(type = actualType),
            ),
        )
        val coreModuleResolver: CoreModuleResolver = { _, _, _ ->
            error("core module resolution must not run")
        }
        val subject = componentLinker(coreModuleResolver)

        val actual = subject(component, imports)

        val expected = Err(importTypeMismatchComponentInstantiationError(listOf("function")))
        assertEquals(expected, actual)
    }

    @Test
    fun `matches linked function resources by runtime identity`() {
        val expectedResource = componentResourceTypeId(1u)
        val actualResource = componentResourceTypeId(2u)
        val expectedType = componentFunctionType(
            params = listOf(labeledComponentValueType(type = ownComponentValueType(resource = expectedResource))),
        )
        val actualType = componentFunctionType(
            params = listOf(labeledComponentValueType(type = ownComponentValueType(resource = actualResource))),
        )
        val resourceTypeAddress = runtimeResourceTypeAddress(7)
        val component = preparedComponent(
            imports = listOf(
                preparedComponentImport(
                    name = "resource",
                    value = resourceTypePreparedComponentImportValue(expectedResource),
                ),
                preparedComponentImport(
                    name = "function",
                    value = functionPreparedComponentImportValue(
                        type = expectedType,
                        resourceTypes = mapOf(expectedResource to RuntimeResourceTypeIndex(0)),
                    ),
                ),
            ),
        )
        val function = runtimeComponentHostFunction()
        val imports = listOf(
            namedComponentLinkInput(
                name = "resource",
                value = resourceTypeComponentLinkInput(resourceTypeAddress),
            ),
            namedComponentLinkInput(
                name = "function",
                value = functionComponentLinkInput(
                    function = function,
                    type = actualType,
                    resourceTypes = mapOf(actualResource to resourceTypeAddress),
                ),
            ),
        )
        val coreModuleResolver: CoreModuleResolver = { _, _, _ ->
            error("core module resolution must not run")
        }
        val subject = componentLinker(coreModuleResolver)

        val actual = subject(component, imports).unwrap().functions.single() === function

        val expected = true
        assertEquals(expected, actual)
    }

    @Test
    fun `production core module resolution applies import and export subtyping`() {
        val actualMemoryType = memoryType(limits = limits(min = 2u, max = 3u))
        val expectedMemoryType = memoryType(limits = limits(min = 1u, max = 4u))
        val compiled = compiledModule(
            module(
                imports = listOf(
                    moduleImport(
                        moduleName = NameValue("environment"),
                        entityName = NameValue("required-memory"),
                        descriptor = memoryImportDescriptor(actualMemoryType),
                    ),
                ),
                memories = listOf(memory(type = actualMemoryType)),
                exports = listOf(
                    export(NameValue("memory"), memoryExportDescriptor()),
                    export(NameValue("extra"), memoryExportDescriptor()),
                ),
            ),
        )
        val expectedType = coreModuleType(
            imports = mapOf(
                CoreImportName("environment", "optional-memory") to coreMemoryEntityType(expectedMemoryType),
                CoreImportName("environment", "required-memory") to coreMemoryEntityType(actualMemoryType),
            ),
            exports = mapOf("memory" to coreMemoryEntityType(expectedMemoryType)),
        )

        val result = ResolveCoreModuleImport(expectedType, compiled, listOf("module")).unwrap()
        val actual = ResolvedModuleObservation(
            module = result.module,
            importIndexes = result.importIndexes.toList(),
            exportIndexes = result.exportIndexes.toList(),
        )

        val expected = ResolvedModuleObservation(
            module = compiled,
            importIndexes = listOf(1),
            exportIndexes = listOf(0),
        )
        assertEquals(expected, actual)
    }
}

private typealias CoreModuleResolver = (
    CoreModuleType,
    CompiledModule,
    List<String>,
) -> Result<ResolvedCoreModuleImport, ComponentInstantiationError>

private fun componentLinker(
    coreModuleResolver: CoreModuleResolver,
): ComponentLinker = { component, imports ->
    ComponentLinker(
        component = component,
        imports = imports,
        coreModuleResolver = coreModuleResolver,
    )
}

private data class ComponentLinkingObservation(
    val modules: List<CompiledModule>,
    val resolverCalls: List<CoreModuleResolutionObservation>,
)

private data class CoreModuleResolutionObservation(
    val type: CoreModuleType,
    val module: CompiledModule,
    val path: List<String>,
)

private data class ResolvedModuleObservation(
    val module: CompiledModule,
    val importIndexes: List<Int>,
    val exportIndexes: List<Int>,
)
