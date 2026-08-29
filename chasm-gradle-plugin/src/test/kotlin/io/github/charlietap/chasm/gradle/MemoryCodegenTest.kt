package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.memoryBinding
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import kotlin.test.Test

class MemoryCodegenTest {

    @Test
    fun `generates memory`() = assertMemory(
        scenario = "SingleMemory",
        memories = listOf(memoryBinding("memory", "memory")),
    )

    @Test
    fun `generates multiple memories`() = assertMemory(
        scenario = "MultipleMemories",
        memories = listOf(
            memoryBinding("memory1", "memory1"),
            memoryBinding("memory2", "memory2"),
        ),
    )

    @Test
    fun `generates unexposed string memory`() = assertMemory(
        scenario = "StringMemory",
        memories = listOf(memoryBinding("memory", "memory", exposed = false)),
        functions = listOf(stringFunction()),
    )

    @Test
    fun `shares exposed memory with string marshalling`() = assertMemory(
        scenario = "SharedStringMemory",
        memories = listOf(memoryBinding("memory", "memory")),
        functions = listOf(stringFunction()),
    )

    @Test
    fun `generates collision safe memory names`() = assertMemory(
        scenario = "CollidingMemoryNames",
        memories = listOf(
            memoryBinding(name = "memory", source = "memory", backingName = "_memory"),
            memoryBinding(name = "_memory_", source = "_memory", backingName = "__memory"),
            memoryBinding(name = "store_", source = "store", backingName = "_store"),
        ),
    )

    private fun assertMemory(
        scenario: String,
        memories: List<MemoryBinding>,
        functions: List<Function> = emptyList(),
    ) = assertGenerates(
        category = "memory",
        wasmInterface = wasmInterface(
            interfaceName = scenario,
            packageName = "com.test",
            functions = functions,
            memories = memories,
        ),
    )

    private fun stringFunction() = function(
        name = "readString",
        returns = functionReturn(
            type = stringScalarType(),
            stringEncodingStrategy = StringEncodingStrategy.NULL_TERMINATED,
        ),
        resultTypes = listOf(i32ValueType()),
        implementation = functionProxy("read_string"),
    )
}
