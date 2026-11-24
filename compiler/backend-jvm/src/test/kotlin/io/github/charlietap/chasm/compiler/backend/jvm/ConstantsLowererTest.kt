@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceGlobal
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import io.github.charlietap.chasm.runtime.compiled.gc.storage.I32Array
import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame

class ConstantsLowererTest {

    @Test
    fun `a module without GC constant expressions gets no constants class`() {

        val generated = lower(decodeModule(fixture("scalar")))

        assertFalse(generated.classes.keys.any { name -> name.endsWith(CONSTANTS_SUFFIX) })
    }

    @Test
    fun `a global initialised with array_new_fixed is evaluated into its reference global`() {

        val generated = lower(decodeModule(fixture("constants")))
        val loader = GeneratedClassLoader(generated)
        val installation = ConstantsInstallation()
        val bindingClass = loader.loadClass(ENTRY_CLASS + BINDING_SUFFIX)
        val binding = bindingClass.getConstructor(CodeInstallation::class.java).newInstance(installation)

        loader.loadClass(ENTRY_CLASS + CONSTANTS_SUFFIX)
            .getMethod("initialize", CodeInstallation::class.java, bindingClass)
            .invoke(null, installation, binding)

        val numbers = assertIs<I32Array>(installation.built.value)
        assertSame(installation.numbers, numbers.rtt)
        assertContentEquals(intArrayOf(1, 2, 7), numbers.data)
    }

    private fun lower(module: Module): Generated {

        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
        val generated = Generated()
        ModuleLowerer(module, code, JvmOptions(entryClass = ENTRY_CLASS), generated, ModuleDeclarationsFactory(module))

        return generated
    }

    private class ConstantsInstallation : CodeInstallation() {

        private val plain = GlobalInstance(GlobalType(ValueType.Number(NumberType.I32), Mutability.Const), 7L)

        val numbers = Rtt(0, "numbers", unusedEngine(), null, null)

        val built = ReferenceGlobal(null, GlobalInstance(GlobalType(ValueType.Number(NumberType.I32), Mutability.Const), 0L))

        override val instance: ModuleInstance
            get() {
                error("the constants read no instance")
            }

        override fun function(index: Int): FunctionInstance {
            error("the constants read no function")
        }

        override fun memory(index: Int): MemoryInstance {
            error("the constants read no memory")
        }

        override fun table(index: Int): TableInstance {
            error("the constants read no table")
        }

        override fun global(index: Int): GlobalInstance {
            check(index == 0) { "global $index is not a number global" }
            return plain
        }

        override fun element(index: Int): ElementInstance {
            error("the constants read no element segment")
        }

        override fun data(index: Int): DataInstance {
            error("the constants read no data segment")
        }

        override fun referenceGlobal(index: Int): Any {
            check(index == 1) { "global $index is not a reference global" }
            return built
        }

        override fun referenceTable(index: Int): Any {
            error("the constants write no table")
        }

        override fun referenceSegment(index: Int): Any {
            error("the constants write no element segment")
        }

        override fun runtimeType(index: Int): Int {
            error("the constants read no runtime type")
        }

        override fun tagAddress(index: Int): Int {
            error("the constants read no tag")
        }

        override fun functionReference(index: Int): Long {
            error("the constants read no function reference")
        }

        override fun managedFunction(index: Int): Any {
            error("the constants read no function")
        }

        override fun rtt(index: Int): Any {
            check(index == 0) { "type $index is not the array type" }
            return numbers
        }

        override fun typeTest(
            nullable: Boolean,
            heapType: Int,
        ): Any {
            error("the constants test no type")
        }

        override fun install(
            index: Int,
            entry: CompiledFunction,
        ) {
            error("the constants install no function")
        }
    }

    private companion object {
        const val ENTRY_CLASS = "com.example.Constants"
        const val BINDING_SUFFIX = "\$Binding"
        const val CONSTANTS_SUFFIX = "\$Constants"

        fun unusedEngine(): Engine {
            return Proxy.newProxyInstance(Engine::class.java.classLoader, arrayOf(Engine::class.java)) { _, method, _ ->
                throw UnsupportedOperationException("${method.name} is not used by the constants")
            } as Engine
        }
    }
}
