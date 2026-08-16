package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import kotlin.test.Test

class ConstructionCodegenTest {

    @Test
    fun `generates synchronous construction`() = assertConstruction("SynchronousConstruction")

    @Test
    fun `generates suspending construction`() = assertConstruction(
        scenario = "SuspendingConstruction",
        generateSuspendingFactories = true,
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
        interfaceVisibility = TypeVisibility.INTERNAL,
        implementationVisibility = TypeVisibility.INTERNAL,
    )

    private fun assertConstruction(
        scenario: String,
        generateSuspendingFactories: Boolean = false,
    ) = assertGenerates(
        category = "construction",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = "com.test",
        ),
        generateSuspendingFactories = generateSuspendingFactories,
    )
}
