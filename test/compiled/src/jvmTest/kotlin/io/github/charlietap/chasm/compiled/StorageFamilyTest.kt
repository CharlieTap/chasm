package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledStruct
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.CheckClassAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.Modifier
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
    fun `the storage family passes the verifier`() {

        STORAGE_FAMILY.classes.forEach { storageClass ->
            val resource = binaryName(storageClass.name).replace('.', '/') + ".class"
            val bytes = checkNotNull(loader.getResourceAsStream(resource)) { "missing $resource" }.use { stream -> stream.readBytes() }
            val output = StringWriter()

            CheckClassAdapter.verify(ClassReader(bytes), loader, false, PrintWriter(output))

            assertEquals("", output.toString(), storageClass.name)
        }
    }
}
