package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RuntimeAbiTest {

    @Test
    fun `every runtime method compiled code calls exists`() {
        val missing = methods().filterNot(::exists).map(MethodRef::toString)

        assertEquals(emptyList(), missing)
    }

    @Test
    fun `only global, memory, unsafe and byte buffer accessors are instance methods`() {
        methods().forEach { method ->
            val expectedStatic = method.owner !in setOf(GLOBAL_INSTANCE, MEMORY_INSTANCE, UNSAFE_TYPE, BYTE_BUFFER_TYPE)
            val actualStatic = Modifier.isStatic(resolve(method).modifiers)
            assertEquals(expectedStatic, actualStatic, method.toString())
        }
    }

    @Test
    fun `every installation method compiled code calls exists`() {
        val missing = methods(INSTALLATION).filterNot(::exists).map(MethodRef::toString)

        assertEquals(emptyList(), missing)
    }

    @Test
    fun `a memory instance has the fields compiled code reads`() {

        val memory = Class.forName(binaryName(MEMORY_INSTANCE))
        val fields = mapOf(
            MEMORY_BYTE_ARRAY_FIELD to "[B",
            MEMORY_BYTE_BUFFER_FIELD to OBJECT_TYPE,
        )

        fields.forEach { (name, type) ->
            val field = memory.getField(name)
            assertTrue(Modifier.isPublic(field.modifiers), name)
            assertFalse(Modifier.isStatic(field.modifiers), name)
            assertEquals(type, descriptor(field.type), name)
        }
    }

    @Test
    fun `the execution context has the fields compiled code reads`() {
        val context = Class.forName(binaryName(CONTEXT_TYPE))
        val fields = mapOf(
            STACK_LIMIT_FIELD to "I",
            CALL_DEPTH_FIELD to "I",
            UNWINDING_FIELD to "Z",
            PENDING_TAIL_FIELD to FUNCTION_INSTANCE,
        )

        fields.forEach { (name, type) ->
            val field = context.getField(name)
            assertTrue(Modifier.isPublic(field.modifiers), name)
            assertFalse(Modifier.isStatic(field.modifiers), name)
            assertEquals(type, descriptor(field.type), name)
        }
    }

    @Test
    fun `compiled code tests for a null reference with the runtime's tag and mask`() {
        assertEquals(RV_TYPE_MASK, REFERENCE_TAG_MASK.toLong())
        assertEquals(RV_TYPE_NULL, REFERENCE_NULL_TAG.toLong())
    }

    private fun methods(
        file: String = RUNTIME_ABI,
    ): List<MethodRef> = Class.forName(file).declaredFields
        .filter { field -> field.type == MethodRef::class.java }
        .map { field -> field.apply { isAccessible = true }.get(null) as MethodRef }

    private fun exists(
        method: MethodRef,
    ): Boolean = candidates(method).any()

    private fun resolve(
        method: MethodRef,
    ): java.lang.reflect.Method = candidates(method).single()

    private fun candidates(
        method: MethodRef,
    ): List<java.lang.reflect.Method> = Class.forName(binaryName(method.owner)).methods.filter { candidate ->
        candidate.name == method.name && descriptor(candidate) == method.descriptor
    }

    private fun descriptor(
        method: java.lang.reflect.Method,
    ): String =
        method.parameterTypes.joinToString("", "(", ")") { descriptor(it) } + descriptor(method.returnType)

    private fun descriptor(
        type: Class<*>,
    ): String = when {
        type == Void.TYPE -> "V"
        type == Integer.TYPE -> "I"
        type == java.lang.Long.TYPE -> "J"
        type == java.lang.Float.TYPE -> "F"
        type == java.lang.Double.TYPE -> "D"
        type == java.lang.Boolean.TYPE -> "Z"
        type == java.lang.Byte.TYPE -> "B"
        type == java.lang.Short.TYPE -> "S"
        type == Character.TYPE -> "C"
        type.isArray -> type.name.replace('.', '/')
        else -> "L" + type.name.replace('.', '/') + ";"
    }

    private companion object {
        const val RUNTIME_ABI = "io.github.charlietap.chasm.compiler.backend.abi.RuntimeAbiKt"
        const val INSTALLATION = "io.github.charlietap.chasm.compiler.backend.abi.InstallationKt"
    }
}
