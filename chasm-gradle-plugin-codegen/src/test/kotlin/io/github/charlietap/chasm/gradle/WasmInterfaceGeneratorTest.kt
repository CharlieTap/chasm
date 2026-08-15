package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionParameter
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.memoryBinding
import io.github.charlietap.chasm.gradle.fixture.stringScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class WasmInterfaceGeneratorTest {

    @Test
    fun `interface and implementation expose bound memories`() {
        val specs = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.INTERNAL,
            wasmInterface = wasmInterface(
                interfaceName = "TestService",
                packageName = "com.example",
                memories = listOf(
                    memoryBinding(
                        name = "frameBuffer",
                        source = "frame_buffer",
                        exposed = true,
                    ),
                ),
            ),
        )

        val interfaceSpec = specs.first().toString()
        assertContains(interfaceSpec, "public interface Memory")
        assertContains(interfaceSpec, "public fun read(")
        assertContains(interfaceSpec, "bufferPointer: Int = 0")
        assertContains(interfaceSpec, "bytesToRead: Int = buffer.size - bufferPointer")
        assertContains(interfaceSpec, "public fun write(")
        assertContains(interfaceSpec, "bytesToWrite: Int = buffer.size - bufferPointer")
        assertContains(interfaceSpec, "public val frameBuffer: Memory")

        val implementationSpec = specs.last().toString()
        assertContains(implementationSpec, "private val _frameBuffer: Memory")
        assertContains(implementationSpec, "exportMemory(instance, \"frame_buffer\")")
        assertContains(implementationSpec, "override val frameBuffer: TestService.Memory")
        assertContains(implementationSpec, "MemoryImpl(store, _frameBuffer, virtualMachine)")
        assertContains(implementationSpec, "private class MemoryImpl(")
        assertContains(implementationSpec, ": TestService.Memory")
        assertContains(implementationSpec, "memoryReadBytes(")
        assertContains(implementationSpec, "memoryWriteBytes(")
    }

    @Test
    fun `string functions and typesafe properties share the same memory binding`() {
        val specs = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.INTERNAL,
            wasmInterface = wasmInterface(
                interfaceName = "TestService",
                packageName = "com.example",
                functions = listOf(
                    function(
                        name = "readString",
                        returns = functionReturn(
                            type = stringScalarType(),
                            stringEncodingStrategy = StringEncodingStrategy.POINTER_AND_LENGTH,
                        ),
                        resultTypes = listOf(i32ValueType(), i32ValueType()),
                        implementation = functionProxy("read_string"),
                    ),
                ),
                memories = listOf(
                    memoryBinding(
                        name = "memory",
                        source = "memory",
                        exposed = true,
                    ),
                ),
            ),
        )

        val implementationSpec = specs.last().toString()
        assertContains(implementationSpec, "private val _memory: Memory")
        assertContains(implementationSpec, "MemoryImpl(store, _memory, virtualMachine)")
        assertContains(implementationSpec, "memoryReadUtf8String(store, _memory,")
        assertEquals(1, implementationSpec.split("exportMemory(instance, \"memory\")").size - 1)
    }

    @Test
    fun `unexposed string memory does not generate the typesafe memory API`() {
        val specs = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.INTERNAL,
            wasmInterface = wasmInterface(
                interfaceName = "TestService",
                packageName = "com.example",
                functions = listOf(
                    function(
                        name = "readString",
                        returns = functionReturn(
                            type = stringScalarType(),
                            stringEncodingStrategy = StringEncodingStrategy.POINTER_AND_LENGTH,
                        ),
                        resultTypes = listOf(i32ValueType(), i32ValueType()),
                        implementation = functionProxy("read_string"),
                    ),
                ),
                memories = listOf(
                    memoryBinding(
                        name = "memory",
                        source = "memory",
                        exposed = false,
                    ),
                ),
            ),
        )

        val interfaceSpec = specs.first().toString()
        assertFalse(interfaceSpec.contains("interface Memory"))
        assertFalse(interfaceSpec.contains("val memory:"))

        val implementationSpec = specs.last().toString()
        assertContains(implementationSpec, "private val _memory: Memory")
        assertContains(implementationSpec, "memoryReadUtf8String(store, _memory,")
        assertFalse(implementationSpec.contains("MemoryImpl"))
    }

    @Test
    fun `string parameters write through the default memory binding`() {
        val implementationSpec = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.INTERNAL,
            wasmInterface = wasmInterface(
                interfaceName = "TestService",
                packageName = "com.example",
                allocator = ExportedAllocator("alloc", "free"),
                functions = listOf(
                    function(
                        name = "writeString",
                        params = listOf(
                            functionParameter(
                                name = "value",
                                type = stringScalarType(),
                                stringEncodingStrategy = StringEncodingStrategy.POINTER_AND_LENGTH,
                            ),
                        ),
                        implementation = functionProxy("write_string"),
                    ),
                ),
                memories = listOf(
                    memoryBinding(
                        name = "memory",
                        source = "memory",
                        exposed = false,
                    ),
                ),
            ),
        ).last().toString()

        assertContains(implementationSpec, "private val _memory: Memory")
        assertContains(implementationSpec, "memoryWriteBytes(store, _memory,")
    }

    @Test
    fun `implementation prepares exports once and invokes the prepared function`() {
        val spec = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.PUBLIC,
            wasmInterface = wasmInterface(
                interfaceName = "AnswerService",
                packageName = "com.example",
                functions = listOf(
                    function(
                        name = "answer",
                        params = listOf(functionParameter("input", integerScalarType())),
                        returns = functionReturn(integerScalarType()),
                        resultTypes = listOf(i32ValueType()),
                        implementation = functionProxy("answer_export"),
                    ),
                ),
            ),
        ).last().toString()

        assertContains(spec, "private val answerPreparedFunction: PreparedFunction")
        assertContains(spec, "virtualMachine.prepareFunction(")
        assertContains(spec, "\"answer_export\"")
        assertContains(spec, "answerPreparedFunction(args)")
        assertFalse(spec.contains("functionInvokeTyped("))
        assertFalse(spec.contains("suspend fun create("))
    }

    @Test
    fun `implementation can expose a suspending factory without replacing its constructor`() {
        val spec = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.PUBLIC,
            wasmInterface = wasmInterface(
                interfaceName = "AnswerService",
                packageName = "com.example",
            ),
            generateSuspendingFactories = true,
        ).last().toString()

        assertContains(spec, "public constructor(")
        assertContains(spec, "public suspend fun create(")
        assertContains(spec, "virtualMachine.moduleDecodeSuspending(binary)")
        assertContains(spec, "virtualMachine.moduleInstantiateSuspending(")
        assertContains(spec, "RuntimeState(store, module, allocatedImports, instance)")
    }
}
