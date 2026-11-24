package io.github.charlietap.chasm.compiler.backend.analysis.gc

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.AbstractHeapType
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.EQ_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_TYPE
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals

class ReferenceTestsTest {

    private val referenceTests = ReferenceTests(module("reference_tests").definedTypes)

    @Test
    fun `any matches everything but functions`() {
        assertEquals(ReferenceTest.NotFunction, abstractTest(AbstractHeapType.Any))
    }

    @Test
    fun `extern matches every reference`() {
        assertEquals(ReferenceTest.Always, abstractTest(AbstractHeapType.Extern))
    }

    @Test
    fun `eq tests against the eq base class`() {
        assertEquals(ReferenceTest.InstanceOf(EQ_REFERENCE), abstractTest(AbstractHeapType.Eq))
    }

    @Test
    fun `struct tests against the struct base class`() {
        assertEquals(ReferenceTest.InstanceOf(MANAGED_STRUCT), abstractTest(AbstractHeapType.Struct))
    }

    @Test
    fun `array tests against the array base class`() {
        assertEquals(ReferenceTest.InstanceOf(MANAGED_ARRAY), abstractTest(AbstractHeapType.Array))
    }

    @Test
    fun `i31 tests against the i31 class`() {
        assertEquals(ReferenceTest.InstanceOf(I31_REFERENCE), abstractTest(AbstractHeapType.I31))
    }

    @Test
    fun `func tests against the function base class`() {
        assertEquals(ReferenceTest.InstanceOf(MANAGED_FUNCTION), abstractTest(AbstractHeapType.Func))
    }

    @Test
    fun `exn tests against the exception class`() {
        assertEquals(ReferenceTest.InstanceOf(WASM_EXCEPTION_TYPE), abstractTest(AbstractHeapType.Exception))
    }

    @Test
    fun `none matches no reference`() {
        assertEquals(ReferenceTest.Never, abstractTest(AbstractHeapType.None))
    }

    @Test
    fun `nofunc matches no reference`() {
        assertEquals(ReferenceTest.Never, abstractTest(AbstractHeapType.NoFunc))
    }

    @Test
    fun `noextern matches no reference`() {
        assertEquals(ReferenceTest.Never, abstractTest(AbstractHeapType.NoExtern))
    }

    @Test
    fun `noexn matches no reference`() {
        assertEquals(ReferenceTest.Never, abstractTest(AbstractHeapType.NoException))
    }

    @Test
    fun `a concrete struct type tests against its defined type`() {
        assertEquals(ReferenceTest.Defined(POINT), referenceTests.test(HeapType.Concrete(TypeIndex(POINT))))
    }

    @Test
    fun `a concrete array type tests against its defined type`() {
        assertEquals(ReferenceTest.Defined(POINTS), referenceTests.test(HeapType.Concrete(TypeIndex(POINTS))))
    }

    @Test
    fun `a function type tests by runtime type`() {
        assertEquals(ReferenceTest.FunctionType(CALLBACK), referenceTests.test(HeapType.Concrete(TypeIndex(CALLBACK))))
    }

    private fun abstractTest(type: AbstractHeapType): ReferenceTest {
        return referenceTests.test(HeapType.Abstract(type))
    }

    private fun module(name: String): Module {

        val bytes = checkNotNull(javaClass.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
            .use { stream -> stream.readBytes() }
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }

        return WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }
    }

    private companion object {
        const val POINT = 0
        const val CALLBACK = 1
        const val POINTS = 2
    }
}
