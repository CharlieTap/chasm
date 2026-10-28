package io.github.charlietap.chasm.executor.instantiator.runtime

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.globalIndex
import io.github.charlietap.chasm.fixture.executor.instantiator.constantExpression
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.HeapTypeEncoder
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantExpressionEvaluatorTest {

    private val store = store(globals = mutableListOf(globalInstance(value = 11L), globalInstance(value = -5L)))
    private val instance = moduleInstance(
        functionAddresses = mutableListOf(Address.Function(40), Address.Function(41)),
        globalAddresses = mutableListOf(Address.Global(1), Address.Global(0)),
    )
    private val types = ModuleTypeResolver(emptyList(), IntArray(0))

    private fun evaluate(
        vararg instructions: ConstantInstruction,
    ) =
        ConstantExpressionEvaluator(store, instance, types, constantExpression(*instructions))

    @Test
    fun `integer arithmetic wraps and keeps operand order`() {
        assertEquals(
            Ok(Int.MIN_VALUE.toLong()),
            evaluate(ConstantInstruction.I32Const(Int.MAX_VALUE), ConstantInstruction.I32Const(1), ConstantInstruction.I32Add),
        )
        assertEquals(
            Ok(-3L),
            evaluate(ConstantInstruction.I32Const(2), ConstantInstruction.I32Const(5), ConstantInstruction.I32Sub),
        )
        assertEquals(
            Ok(Long.MIN_VALUE),
            evaluate(ConstantInstruction.I64Const(Long.MIN_VALUE / 2), ConstantInstruction.I64Const(2), ConstantInstruction.I64Mul),
        )
        assertEquals(
            Ok(3L),
            evaluate(ConstantInstruction.I64Const(5), ConstantInstruction.I64Const(2), ConstantInstruction.I64Sub),
        )
    }

    @Test
    fun `float constants keep their exact bits`() {
        val negativeZero = (-0.0f).toRawBits()
        val nanPayload = 0x7FF4000000000001

        assertEquals(Ok(negativeZero.toLong()), evaluate(ConstantInstruction.F32Const(negativeZero)))
        assertEquals(Ok(nanPayload), evaluate(ConstantInstruction.F64Const(nanPayload)))
    }

    @Test
    fun `globals and function references resolve through the instance`() {
        assertEquals(Ok(-5L), evaluate(ConstantInstruction.GlobalGet(globalIndex(0u))))
        assertEquals(
            Ok((41L shl RV_SHIFT_BITS) or RV_TYPE_FUNCTION),
            evaluate(ConstantInstruction.RefFunc(functionIndex(1u))),
        )
    }

    @Test
    fun `references encode as the runtime stores them`() {
        val nullFunction = (HeapTypeEncoder(AbstractHeapType.Func).toLong() shl RV_SHIFT_BITS) or RV_TYPE_NULL
        assertEquals(Ok(nullFunction), evaluate(ConstantInstruction.RefNull(AbstractHeapType.Func)))
        assertEquals(
            Ok((0x7FFFFFFFL shl RV_SHIFT_BITS) or RV_TYPE_I31),
            evaluate(ConstantInstruction.I32Const(-1), ConstantInstruction.RefI31),
        )
    }

    @Test
    fun `missing entities and values fail without throwing`() {
        assertEquals(Err(InvocationError.GlobalAddressLookupFailed(5)), evaluate(ConstantInstruction.GlobalGet(globalIndex(5u))))
        assertEquals(Err(InvocationError.FunctionAddressLookupFailed(9)), evaluate(ConstantInstruction.RefFunc(functionIndex(9u))))
        assertEquals(Err(InvocationError.MissingStackValue), evaluate())
    }

    @Test
    fun `a vector constant evaluates to zero`() {
        assertEquals(Ok(0L), evaluate(ConstantInstruction.V128Const(low = 1, high = 2)))
    }
}
