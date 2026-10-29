package io.github.charlietap.chasm.compiler.backend.abi

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
    fun `only global accessors are instance methods`() {
        methods().forEach { method ->
            val expectedStatic = method.owner != GLOBAL_INSTANCE
            val actualStatic = Modifier.isStatic(resolve(method).modifiers)
            assertEquals(expectedStatic, actualStatic, method.toString())
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

    private fun methods(): List<MethodRef> = Class.forName("io.github.charlietap.chasm.compiler.backend.abi.RuntimeAbiKt").declaredFields
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
}
