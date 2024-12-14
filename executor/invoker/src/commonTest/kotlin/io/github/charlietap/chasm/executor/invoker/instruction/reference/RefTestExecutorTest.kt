package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.ast.type.referenceType
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.refTestRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.executor.runtime.value.referenceValue
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor
import kotlin.test.Test
import kotlin.test.assertEquals

class RefTestExecutorTest {

    @Test
    fun `can execute the RefTest instruction and return the reference value when types match`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val referenceType = referenceType()
        val typeOfReferenceType = refNullReferenceType(AbstractHeapType.I31)

        val frame = frame()

        stack.push(frame)

        val referenceValue = referenceValue()

        stack.pushValue(referenceValue)

        val referenceTypeSubstitutor: TypeSubstitutor<ReferenceType> = { _referenceType, _ ->
            assertEquals(referenceType, _referenceType)

            _referenceType
        }

        val referenceTypeMatcher: TypeMatcher<ReferenceType> = { t1, t2, _ ->
            assertEquals(typeOfReferenceType, t1)
            assertEquals(referenceType, t2)

            true
        }

        val typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType> = { _refVal, _store, _instance ->
            assertEquals(referenceValue, _refVal)
            assertEquals(store, _store)
            assertEquals(frame.instance, _instance)

            typeOfReferenceType
        }

        val actual = RefTestExecutor(
            context = context,
            instruction = refTestRuntimeInstruction(referenceType),
            referenceTypeSubstitutor = referenceTypeSubstitutor,
            referenceTypeMatcher = referenceTypeMatcher,
            typeOfReferenceValue = typeOfReferenceValue,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(1), stack.popValueOrNull()?.value)
    }

    @Test
    fun `can execute the RefTest instruction and error when types do not match`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val referenceType = referenceType()
        val typeOfReferenceType = refNullReferenceType(AbstractHeapType.I31)

        val frame = frame()

        stack.push(frame)

        val referenceValue = referenceValue()

        stack.pushValue(referenceValue)

        val referenceTypeSubstitutor: TypeSubstitutor<ReferenceType> = { _referenceType, _ ->
            assertEquals(referenceType, _referenceType)

            _referenceType
        }

        val referenceTypeMatcher: TypeMatcher<ReferenceType> = { t1, t2, _ ->
            assertEquals(typeOfReferenceType, t1)
            assertEquals(referenceType, t2)

            false
        }

        val typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType> = { _refVal, _store, _instance ->
            assertEquals(referenceValue, _refVal)
            assertEquals(store, _store)
            assertEquals(frame.instance, _instance)

            typeOfReferenceType
        }

        val actual = RefTestExecutor(
            context = context,
            instruction = refTestRuntimeInstruction(referenceType),
            referenceTypeSubstitutor = referenceTypeSubstitutor,
            referenceTypeMatcher = referenceTypeMatcher,
            typeOfReferenceValue = typeOfReferenceValue,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(i32(0), stack.popValueOrNull()?.value)
    }
}
