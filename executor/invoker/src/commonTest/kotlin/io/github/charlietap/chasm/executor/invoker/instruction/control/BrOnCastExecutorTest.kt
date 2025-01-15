package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.type.heapType
import io.github.charlietap.chasm.fixture.ast.type.referenceType
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor
import kotlin.test.Test
import kotlin.test.assertEquals

class BrOnCastExecutorTest {

    @Test
    fun `can execute a broncast and break when reference types match and breakifmatches is set to true`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()

        val frame = frame()

        stack.push(frame)

        val referenceValue = ReferenceValue.Null(heapType())
        stack.push(referenceValue)

        val expectedReferenceTypes = sequenceOf(srcReferenceType, dstReferenceType).iterator()
        val referenceTypeSubstitutor: TypeSubstitutor<ReferenceType> = { _referenceType, _ ->
            assertEquals(expectedReferenceTypes.next(), _referenceType)

            _referenceType
        }

        val referenceTypeMatcher: TypeMatcher<ReferenceType> = { t1, t2, _ ->
            assertEquals(srcReferenceType, t1)
            assertEquals(dstReferenceType, t2)

            true
        }

        val typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType> = { _refVal, _store, _instance ->
            assertEquals(referenceValue, _refVal)
            assertEquals(store, _store)
            assertEquals(frame.instance, _instance)

            referenceType()
        }

        var didBreak = false
        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)

            didBreak = true

            Ok(Unit)
        }

        val actual =
            BrOnCastExecutor(
                context,
                labelIndex,
                srcReferenceType,
                dstReferenceType,
                true,
                referenceTypeSubstitutor,
                referenceTypeMatcher,
                typeOfReferenceValue,
                breakExecutor,
            )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(referenceValue, stack.popValueOrNull())
        assertEquals(true, didBreak)
    }

    @Test
    fun `can execute a broncast and not break when reference types match and breakifmatches is set to false`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()

        val frame = frame()
        stack.push(frame)

        val referenceValue = ReferenceValue.Null(heapType())
        stack.push(referenceValue)

        val expectedReferenceTypes = sequenceOf(srcReferenceType, dstReferenceType).iterator()
        val referenceTypeSubstitutor: TypeSubstitutor<ReferenceType> = { _referenceType, _ ->
            assertEquals(expectedReferenceTypes.next(), _referenceType)

            _referenceType
        }

        val referenceTypeMatcher: TypeMatcher<ReferenceType> = { t1, t2, _ ->
            assertEquals(srcReferenceType, t1)
            assertEquals(dstReferenceType, t2)

            true
        }

        val typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType> = { _refVal, _store, _instance ->
            assertEquals(referenceValue, _refVal)
            assertEquals(store, _store)
            assertEquals(frame.instance, _instance)

            referenceType()
        }

        var didBreak = false
        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)

            didBreak = true

            Ok(Unit)
        }

        val actual =
            BrOnCastExecutor(
                context,
                labelIndex,
                srcReferenceType,
                dstReferenceType,
                false,
                referenceTypeSubstitutor,
                referenceTypeMatcher,
                typeOfReferenceValue,
                breakExecutor,
            )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(referenceValue, stack.popValueOrNull())
        assertEquals(false, didBreak)
    }

    @Test
    fun `can execute a broncast and not break when reference types do not match and breakifmatches is set to true`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()

        val frame = frame()
        stack.push(frame)

        val referenceValue = ReferenceValue.Null(heapType())
        stack.push(referenceValue)

        val expectedReferenceTypes = sequenceOf(srcReferenceType, dstReferenceType).iterator()
        val referenceTypeSubstitutor: TypeSubstitutor<ReferenceType> = { _referenceType, _ ->
            assertEquals(expectedReferenceTypes.next(), _referenceType)

            _referenceType
        }

        val referenceTypeMatcher: TypeMatcher<ReferenceType> = { t1, t2, _ ->
            assertEquals(srcReferenceType, t1)
            assertEquals(dstReferenceType, t2)

            false
        }

        val typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType> = { _refVal, _store, _instance ->
            assertEquals(referenceValue, _refVal)
            assertEquals(store, _store)
            assertEquals(frame.instance, _instance)

            referenceType()
        }

        var didBreak = false
        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)

            didBreak = true

            Ok(Unit)
        }

        val actual =
            BrOnCastExecutor(
                context,
                labelIndex,
                srcReferenceType,
                dstReferenceType,
                true,
                referenceTypeSubstitutor,
                referenceTypeMatcher,
                typeOfReferenceValue,
                breakExecutor,
            )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(referenceValue, stack.popValueOrNull())
        assertEquals(false, didBreak)
    }

    @Test
    fun `can execute a broncast and break when reference types do not match and breakifmatches is set to false`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)
        val labelIndex = labelIndex()
        val srcReferenceType = referenceType()
        val dstReferenceType = referenceType()

        val frame = frame()
        stack.push(frame)

        val referenceValue = ReferenceValue.Null(heapType())
        stack.push(referenceValue)

        val expectedReferenceTypes = sequenceOf(srcReferenceType, dstReferenceType).iterator()
        val referenceTypeSubstitutor: TypeSubstitutor<ReferenceType> = { _referenceType, _ ->
            assertEquals(expectedReferenceTypes.next(), _referenceType)

            _referenceType
        }

        val referenceTypeMatcher: TypeMatcher<ReferenceType> = { t1, t2, _ ->
            assertEquals(srcReferenceType, t1)
            assertEquals(dstReferenceType, t2)

            false
        }

        val typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType> = { _refVal, _store, _instance ->
            assertEquals(referenceValue, _refVal)
            assertEquals(store, _store)
            assertEquals(frame.instance, _instance)

            referenceType()
        }

        var didBreak = false
        val breakExecutor: BreakExecutor = { _stack, _labelIndex ->
            assertEquals(stack, _stack)
            assertEquals(labelIndex, _labelIndex)

            didBreak = true

            Ok(Unit)
        }

        val actual =
            BrOnCastExecutor(
                context,
                labelIndex,
                srcReferenceType,
                dstReferenceType,
                false,
                referenceTypeSubstitutor,
                referenceTypeMatcher,
                typeOfReferenceValue,
                breakExecutor,
            )

        assertEquals(Unit, actual)
        assertEquals(1, stack.valuesDepth())
        assertEquals(referenceValue, stack.popValueOrNull())
        assertEquals(true, didBreak)
    }
}
