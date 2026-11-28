package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.CheckClassAdapter
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Modifier
import java.util.jar.JarFile
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageFamilyTest {

    private val loader = CompiledStruct::class.java.classLoader

    @Test
    fun `every class in STORAGE_FAMILY exists in the runtime family`() {

        STORAGE_FAMILY.classes.forEach { storageClass ->
            val runtimeClass = Class.forName(binaryName(storageClass.name), false, loader)
            val superclass = storageClass.superclass?.let { parent -> binaryName(parent.name) } ?: CompiledStruct::class.java.name

            assertEquals(superclass, runtimeClass.superclass.name, storageClass.name)
            assertEquals(storageClass.final, Modifier.isFinal(runtimeClass.modifiers), storageClass.name)
        }
    }

    @Test
    fun `every runtime storage class holds the slots its name states`() {

        STORAGE_FAMILY.classes.forEach { storageClass ->
            val runtimeClass = Class.forName(binaryName(storageClass.name), false, loader)
            val fields = generateSequence(runtimeClass) { current -> current.superclass }
                .takeWhile { current -> current != CompiledStruct::class.java }
                .flatMap { current -> current.declaredFields.asSequence() }
                .filter { field -> !field.isSynthetic && Modifier.isPublic(field.modifiers) }
                .toList()
            val ints = fields.count { field -> field.type == Int::class.javaPrimitiveType }
            val references = fields.count { field -> field.type == Any::class.java }

            assertEquals("Struct_I${ints}_R$references", runtimeClass.simpleName.substringBefore("__"), storageClass.name)
        }
    }

    @Test
    fun `the storage package holds the family and the array classes and nothing else`() {

        val location = File(CompiledStruct::class.java.protectionDomain.codeSource.location.toURI())
        val entries = if (location.isDirectory) {
            location.walk().filter { file -> file.isFile }.map { file -> file.relativeTo(location).invariantSeparatorsPath }.toList()
        } else {
            JarFile(location).use { jar -> jar.entries().asSequence().map { entry -> entry.name }.toList() }
        }
        val storageClasses = entries
            .filter { entry -> entry.startsWith(STORAGE_PACKAGE_PATH) && entry.endsWith(".class") && '$' !in entry }
            .map { entry -> entry.removePrefix(STORAGE_PACKAGE_PATH).removeSuffix(".class") }
            .filter { name -> '/' !in name }
            .toSet()
        val familyClasses = STORAGE_FAMILY.classes.map { storageClass -> binaryName(storageClass.name).substringAfterLast('.') }.toSet()

        assertEquals(familyClasses + ARRAY_CLASSES, storageClasses)
    }

    @Test
    fun `the storage family passes the verifier`() {

        STORAGE_FAMILY.classes.forEach { storageClass ->
            val resource = binaryName(storageClass.name).replace('.', '/') + ".class"
            val bytes = checkNotNull(loader.getResourceAsStream(resource)) { "missing $resource" }.use { stream -> stream.readBytes() }
            val output = StringWriter()

            CheckClassAdapter.verify(ClassReader(bytes), loader, false, PrintWriter(output))

            assertEquals("", output.toString(), storageClass.name)
        }
    }

    private companion object {
        const val STORAGE_PACKAGE_PATH = "io/github/charlietap/chasm/runtime/compiled/gc/storage/"
        val ARRAY_CLASSES = setOf("I8Array", "I16Array", "I32Array", "I64Array", "F32Array", "F64Array", "RefArray")
    }
}
