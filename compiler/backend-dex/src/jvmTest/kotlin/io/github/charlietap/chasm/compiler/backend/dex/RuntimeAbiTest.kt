package io.github.charlietap.chasm.compiler.backend.dex

import kotlin.test.Test
import kotlin.test.assertEquals

class RuntimeAbiTest {

    @Test
    fun `every runtime method generated code calls exists`() {
        val missing = FIXTURES.flatMap { name -> runtimeMethods(generate(decodeModule(fixture(name))).dex) }
            .toSortedSet()
            .filterNot(::methodExists)

        assertEquals(emptyList(), missing)
    }

    @Test
    fun `every runtime field generated code reads exists`() {
        val missing = FIXTURES.flatMap { name -> runtimeFields(generate(decodeModule(fixture(name))).dex) }
            .toSortedSet()
            .filterNot(::fieldExists)

        assertEquals(emptyList(), missing)
    }

    private fun methodExists(
        reference: RuntimeMember,
    ): Boolean {
        val owner = runtimeClass(reference.owner) ?: return false
        if (reference.name == "<init>") {
            return owner.declaredConstructors.any { constructor -> descriptor(constructor.parameterTypes, Void.TYPE) == reference.descriptor }
        }
        return hierarchy(owner).any { type ->
            type.declaredMethods.any { method ->
                method.name == reference.name && descriptor(method.parameterTypes, method.returnType) == reference.descriptor
            }
        }
    }

    private fun fieldExists(
        reference: RuntimeMember,
    ): Boolean {
        val owner = runtimeClass(reference.owner) ?: return false
        return hierarchy(owner).any { type ->
            type.declaredFields.any { field -> field.name == reference.name && typeDescriptor(field.type) == reference.descriptor }
        }
    }

    private fun runtimeClass(
        descriptor: String,
    ): Class<*>? = try {
        Class.forName(descriptor.removePrefix("L").removeSuffix(";").replace('/', '.'), false, javaClass.classLoader)
    } catch (_: ClassNotFoundException) {
        null
    }

    private fun hierarchy(
        type: Class<*>,
    ): Sequence<Class<*>> = generateSequence(type) { it.superclass } + type.interfaces.asSequence()

    private fun descriptor(
        parameters: Array<Class<*>>,
        returns: Class<*>,
    ): String = parameters.joinToString("", "(", ")") { typeDescriptor(it) } + typeDescriptor(returns)

    private fun typeDescriptor(
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
        type.isArray -> "[" + typeDescriptor(type.componentType)
        else -> "L" + type.name.replace('.', '/') + ";"
    }

    private companion object {
        val FIXTURES = listOf("scalar", "control", "memory", "calls", "gc", "eh", "tail", "wide", "bench_gc", "bench")
    }
}
