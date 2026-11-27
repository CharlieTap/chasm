package io.github.charlietap.chasm.jvm

import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import kotlinx.coroutines.test.runTest
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import kotlin.test.Test
import kotlin.test.assertFalse

class ConsumerSurfaceTest {

    private val loader = ConsumerSurfaceTest::class.java.classLoader

    @Test
    fun `the module class of a module with GC types names nothing from the internal runtime`() = runTest {

        val output = ClassOutput.memory()
        compile(module("managed_objects"), output, CompileConfig(ENTRY_CLASS)).expect("compiles")

        val node = ClassNode()
        ClassReader(output.classFiles.getValue(ENTRY_CLASS)).accept(node, 0)
        val runtimeCompiledJar = CompiledStruct::class.java.protectionDomain.codeSource.location
        val named = surface(node).flatMap { name -> withSupertypes(Class.forName(Type.getObjectType(name).className, false, loader)) }

        named.forEach { type ->
            val inInternalRuntime = type.protectionDomain?.codeSource?.location == runtimeCompiledJar && type.name.startsWith(INTERNAL_RUNTIME_PACKAGE)
            assertFalse(inInternalRuntime, "${node.name} exposes ${type.name}, which belongs to the internal runtime")
        }
    }

    private fun surface(node: ClassNode): List<String> {

        val publicMethods = node.methods.filter { method -> method.access and Opcodes.ACC_PUBLIC != 0 }
        val methodTypes = publicMethods.flatMap { method ->
            val type = Type.getMethodType(method.desc)
            type.argumentTypes.toList() + type.returnType
        }
        val fieldTypes = node.fields.filter { field -> field.access and Opcodes.ACC_PUBLIC != 0 }.map { field -> Type.getType(field.desc) }

        val named = listOf(node.superName) + node.interfaces + (methodTypes + fieldTypes).mapNotNull(::objectName)

        return named.filter { name -> name != node.name }
    }

    private fun objectName(type: Type): String? {

        val element = if (type.sort == Type.ARRAY) type.elementType else type

        return if (element.sort == Type.OBJECT) element.internalName else null
    }

    private fun withSupertypes(type: Class<*>): List<Class<*>> {
        return listOf(type) + (listOfNotNull(type.superclass) + type.interfaces).flatMap(::withSupertypes)
    }

    private fun module(name: String): ByteArray {
        return checkNotNull(ConsumerSurfaceTest::class.java.getResourceAsStream("/modules/$name.wasm")) { "missing module $name" }.use { stream ->
            stream.readBytes()
        }
    }

    private companion object {
        const val ENTRY_CLASS = "test.surface.Module"
        const val INTERNAL_RUNTIME_PACKAGE = "io.github.charlietap.chasm.runtime.compiled."
    }
}
