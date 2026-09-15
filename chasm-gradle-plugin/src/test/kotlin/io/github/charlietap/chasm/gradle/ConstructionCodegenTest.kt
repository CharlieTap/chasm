package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.gradle.fixture.codegenConfig
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.globalProxy
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.memoryBinding
import io.github.charlietap.chasm.gradle.fixture.property
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith

class ConstructionCodegenTest {

    @Test
    fun `generates synchronous construction`() = assertConstruction("SynchronousConstruction")

    @Test
    fun `generates suspending construction from codegen config`() = assertConstruction(
        scenario = "SuspendingConstruction",
        config = codegenConfig(generateSuspendingFactories = true),
    )

    @Test
    fun `generates initializers`() = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = "Initializers",
            packageName = "com.test",
            initializers = linkedSetOf("initialize", "start"),
        ),
    )

    @Test
    fun `generates internal visibility`() = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = "InternalVisibility",
            packageName = "com.test",
        ),
        interfaceVisibility = InterfaceVisibility.INTERNAL,
        factoryVisibility = FactoryVisibility.INTERNAL,
        implementationVisibility = ImplementationVisibility.INTERNAL,
    )

    @Test
    fun `generates suspending internal visibility`() = assertConstruction(
        scenario = "SuspendingInternalVisibility",
        interfaceVisibility = InterfaceVisibility.INTERNAL,
        factoryVisibility = FactoryVisibility.INTERNAL,
        implementationVisibility = ImplementationVisibility.INTERNAL,
        config = codegenConfig(generateSuspendingFactories = true),
    )

    @Test
    fun `generates non-empty suspending service`() = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = "SuspendingService",
            packageName = "com.test",
            initializers = linkedSetOf("initialize", "start"),
            functions = listOf(
                function(
                    name = "readString",
                    returns = functionReturn(
                        type = stringScalarType(),
                        stringEncodingStrategy = StringEncodingStrategy.NULL_TERMINATED,
                    ),
                    resultTypes = listOf(i32ValueType()),
                    implementation = functionProxy("read_string"),
                ),
            ),
            properties = listOf(
                property(
                    name = "counter",
                    type = integerScalarType(),
                    const = false,
                    implementation = globalProxy("counter", WasmVirtualMachine.Value.I32::class),
                ),
            ),
            memories = listOf(memoryBinding("memory", "memory")),
        ),
        config = codegenConfig(generateSuspendingFactories = true),
    )

    @Test
    fun `rejects public factory for internal interface`() {
        val error = assertFailsWith<IllegalStateException> {
            WasmInterfaceGenerator()(
                interfaceVisibility = InterfaceVisibility.INTERNAL,
                factoryVisibility = FactoryVisibility.PUBLIC,
                wasmInterface = wasmInterface(interfaceName = "InternalService"),
            )
        }

        assertContains(error.message.orEmpty(), "public factory for internal interface InternalService")
    }

    @Test
    fun `implementation visibility is independent of factory visibility in both construction modes`() {
        listOf(false, true).forEach { suspending ->
            ImplementationVisibility.entries.forEach { visibility ->
                FactoryVisibility.entries.forEach { factoryVisibility ->
                    val files = WasmInterfaceGenerator()(
                        interfaceVisibility = InterfaceVisibility.PUBLIC,
                        implementationVisibility = visibility,
                        factoryVisibility = factoryVisibility,
                        wasmInterface = wasmInterface(interfaceName = "Service"),
                        config = codegenConfig(generateSuspendingFactories = suspending),
                    )
                    val implementation = files.last().toString()
                    val suspendModifier = if (suspending) "suspend " else ""
                    assertContains(implementation, "${visibility.name.lowercase()} class ServiceImpl(")
                    assertContains(implementation, "${factoryVisibility.name.lowercase()} ${suspendModifier}fun service(")
                }
            }
        }
    }

    @Test
    fun `rejects public implementation for internal interface`() {
        val error = assertFailsWith<IllegalStateException> {
            WasmInterfaceGenerator()(
                interfaceVisibility = InterfaceVisibility.INTERNAL,
                implementationVisibility = ImplementationVisibility.PUBLIC,
                factoryVisibility = FactoryVisibility.INTERNAL,
                wasmInterface = wasmInterface(interfaceName = "InternalService"),
            )
        }

        assertContains(error.message.orEmpty(), "public implementation for internal interface InternalService")
    }

    private fun assertConstruction(
        scenario: String,
        interfaceVisibility: InterfaceVisibility = InterfaceVisibility.PUBLIC,
        factoryVisibility: FactoryVisibility = FactoryVisibility.PUBLIC,
        implementationVisibility: ImplementationVisibility = ImplementationVisibility.PRIVATE,
        config: CodegenConfig = codegenConfig(),
    ) = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = "com.test",
        ),
        interfaceVisibility = interfaceVisibility,
        factoryVisibility = factoryVisibility,
        implementationVisibility = implementationVisibility,
        config = config,
    )
}
