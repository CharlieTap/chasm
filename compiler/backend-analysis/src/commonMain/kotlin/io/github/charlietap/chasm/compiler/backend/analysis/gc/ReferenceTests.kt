package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.cir.AbstractHeapType
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.compiler.backend.abi.EQ_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_TYPE
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType

sealed interface ReferenceTest {

    data object Always : ReferenceTest

    data object Never : ReferenceTest

    data object NotFunction : ReferenceTest

    data class InstanceOf(val descriptor: String) : ReferenceTest

    data class Defined(val typeIndex: Int) : ReferenceTest

    data class FunctionType(val typeIndex: Int) : ReferenceTest
}

class ReferenceTests(
    private val types: List<DefinedType>,
) {

    fun test(heapType: HeapType): ReferenceTest {
        return when (heapType) {
            is HeapType.Abstract -> abstractTest(heapType.type)
            is HeapType.Concrete -> concreteTest(heapType.type.index)
        }
    }

    private fun abstractTest(type: AbstractHeapType): ReferenceTest {
        return when (type) {
            AbstractHeapType.Any -> ReferenceTest.NotFunction
            AbstractHeapType.Extern -> ReferenceTest.Always
            AbstractHeapType.Eq -> ReferenceTest.InstanceOf(EQ_REFERENCE)
            AbstractHeapType.Struct -> ReferenceTest.InstanceOf(MANAGED_STRUCT)
            AbstractHeapType.Array -> ReferenceTest.InstanceOf(MANAGED_ARRAY)
            AbstractHeapType.I31 -> ReferenceTest.InstanceOf(I31_REFERENCE)
            AbstractHeapType.Func -> ReferenceTest.InstanceOf(MANAGED_FUNCTION)
            AbstractHeapType.Exception -> ReferenceTest.InstanceOf(WASM_EXCEPTION_TYPE)
            AbstractHeapType.None, AbstractHeapType.NoFunc, AbstractHeapType.NoExtern, AbstractHeapType.NoException -> ReferenceTest.Never
        }
    }

    private fun concreteTest(index: Int): ReferenceTest {

        val type = types[index]
        val composite = type.recursiveType.subTypes[type.recursiveTypeIndex].compositeType

        return if (composite is CompositeType.Function) {
            ReferenceTest.FunctionType(index)
        } else {
            ReferenceTest.Defined(index)
        }
    }
}
