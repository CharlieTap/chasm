package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.fieldIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AggregateInstructionExecutorTest {

    @Test
    fun `delegate the StructNew instruction to the structNewExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.StructNew(typeIndex)

        val structNewExecutor: StructNewExecutor = { _store, _stack, _typeIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor,
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructNewDefault instruction to the structNewExecutorDefault`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.StructNewDefault(typeIndex)

        val structNewDefaultExecutor: StructNewDefaultExecutor = { _store, _stack, _typeIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor,
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructGet instruction to the structGetExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.StructGet(typeIndex(), fieldIndex())

        val structGetExecutor: StructGetExecutor = { _store, _stack, _typeIndex, _fieldIndex, _signedUnpack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction.typeIndex, _typeIndex)
            assertEquals(instruction.fieldIndex, _fieldIndex)
            assertEquals(true, _signedUnpack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor,
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructGetSigned instruction to the structGetExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.StructGetSigned(typeIndex(), fieldIndex())

        val structGetExecutor: StructGetExecutor = { _store, _stack, _typeIndex, _fieldIndex, _signedUnpack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction.typeIndex, _typeIndex)
            assertEquals(instruction.fieldIndex, _fieldIndex)
            assertEquals(true, _signedUnpack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor,
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructGetUnsigned instruction to the structGetExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.StructGetUnsigned(typeIndex(), fieldIndex())

        val structGetExecutor: StructGetExecutor = { _store, _stack, _typeIndex, _fieldIndex, _signedUnpack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction.typeIndex, _typeIndex)
            assertEquals(instruction.fieldIndex, _fieldIndex)
            assertEquals(false, _signedUnpack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor,
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the StructSetUnsigned instruction to the structSetExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.StructSet(typeIndex(), fieldIndex())

        val structSetExecutor: StructSetExecutor = { _store, _stack, _instruction ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor,
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewFixed instruction to the arrayNewFixedExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()
        val size = 0u

        val instruction = AggregateInstruction.ArrayNewFixed(typeIndex, size)

        val arrayNewFixedExecutor: ArrayNewFixedExecutor = { _store, _stack, _typeIndex, _size ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(size, _size)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor,
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewDefault instruction to the arrayNewDefaultExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.ArrayNewDefault(typeIndex)

        val arrayNewDefaultExecutor: ArrayNewDefaultExecutor = { _store, _stack, _typeIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor,
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewData instruction to the arrayNewDataExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()
        val dataIndex = dataIndex()

        val instruction = AggregateInstruction.ArrayNewData(typeIndex, dataIndex)

        val arrayNewDataExecutor: ArrayNewDataExecutor = { _store, _stack, _typeIndex, _dataIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(dataIndex, _dataIndex)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor,
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayNewElement instruction to the arrayNewElementExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()
        val elementIndex = elementIndex()

        val instruction = AggregateInstruction.ArrayNewElement(typeIndex, elementIndex)

        val arrayNewElementExecutor: ArrayNewElementExecutor = { _store, _stack, _typeIndex, _elementIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(elementIndex, _elementIndex)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor,
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayGet instruction to the arrayGetExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.ArrayGet(typeIndex)

        val arrayGetExecutor: ArrayGetExecutor = { _store, _stack, _typeIndex, _signedUnpack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(true, _signedUnpack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor,
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayGetSigned instruction to the arrayGetExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.ArrayGetSigned(typeIndex)

        val arrayGetExecutor: ArrayGetExecutor = { _store, _stack, _typeIndex, _signedUnpack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(true, _signedUnpack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor,
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayGetUnsigned instruction to the arrayGetExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.ArrayGetUnsigned(typeIndex)

        val arrayGetExecutor: ArrayGetExecutor = { _store, _stack, _typeIndex, _signedUnpack ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(false, _signedUnpack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor,
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArraySetUnsigned instruction to the arraySetExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.ArraySet(typeIndex)

        val arraySetExecutor: ArraySetExecutor = { _store, _stack, _typeIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor,
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the RefI31 instruction to the refI31Executor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.RefI31

        val refI31Executor: RefI31Executor = { _stack ->
            assertEquals(stack, _stack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor,
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the I31GetSigned instruction to the I31GetExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.I31GetSigned

        val i31GetExecutor: I31GetExecutor = { _stack, _signedExtension ->
            assertEquals(stack, _stack)
            assertEquals(true, _signedExtension)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor,
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the I31GetUnsigned instruction to the I31GetExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.I31GetUnsigned

        val i31GetExecutor: I31GetExecutor = { _stack, _signedExtension ->
            assertEquals(stack, _stack)
            assertEquals(false, _signedExtension)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor,
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ArrayFill instruction to the ArrayFillExecutor`() {

        val store = store()
        val stack = stack()
        val typeIndex = typeIndex()

        val instruction = AggregateInstruction.ArrayFill(typeIndex)

        val arrayFillExecutor: ArrayFillExecutor = { _store, _stack, _typeIndex ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor,
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the AnyConvertExtern instruction to the AnyConvertExternExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.AnyConvertExtern

        val anyConvertExternExecutor: AnyConvertExternExecutor = { _stack ->
            assertEquals(stack, _stack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor,
            externConvertAnyExecutor = externConvertAnyExecutor(),
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `delegate the ExternConvertAny instruction to the ExternConvertAnyExecutor`() {

        val store = store()
        val stack = stack()

        val instruction = AggregateInstruction.ExternConvertAny

        val externConvertAnyExecutor: ExternConvertAnyExecutor = { _stack ->
            assertEquals(stack, _stack)

            Ok(Unit)
        }

        val actual = AggregateInstructionExecutor(
            instruction = instruction,
            store = store,
            stack = stack,
            structNewExecutor = structNewExecutor(),
            structNewDefaultExecutor = structNewDefaultExecutor(),
            structGetExecutor = structGetExecutor(),
            structSetExecutor = structSetExecutor(),
            arrayNewFixedExecutor = arrayNewFixedExecutor(),
            arrayNewExecutor = arrayNewExecutor(),
            arrayNewDefaultExecutor = arrayNewDefaultExecutor(),
            arrayNewDataExecutor = arrayNewDataExecutor(),
            arrayNewElementExecutor = arrayNewElementExecutor(),
            arrayGetExecutor = arrayGetExecutor(),
            arraySetExecutor = arraySetExecutor(),
            arrayLenExecutor = arrayLenExecutor(),
            refI31Executor = refI31Executor(),
            i31GetExecutor = i31GetExecutor(),
            arrayFillExecutor = arrayFillExecutor(),
            arrayCopyExecutor = arrayCopyExecutor(),
            arrayInitDataExecutor = arrayInitDataExecutor(),
            arrayInitElementExecutor = arrayInitElementExecutor(),
            anyConvertExternExecutor = anyConvertExternExecutor(),
            externConvertAnyExecutor = externConvertAnyExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun structNewExecutor(): StructNewExecutor = { _, _, _ ->
            fail("StructNewExecutor should not be called in this scenario")
        }

        fun structNewDefaultExecutor(): StructNewDefaultExecutor = { _, _, _ ->
            fail("StructNewDefaultExecutor should not be called in this scenario")
        }

        fun structGetExecutor(): StructGetExecutor = { _, _, _, _, _ ->
            fail("StructGetExecutor should not be called in this scenario")
        }

        fun structSetExecutor(): StructSetExecutor = { _, _, _ ->
            fail("StructSetExecutor should not be called in this scenario")
        }

        fun arrayNewFixedExecutor(): ArrayNewFixedExecutor = { _, _, _, _ ->
            fail("ArrayNewFixedExecutor should not be called in this scenario")
        }

        fun arrayNewExecutor(): ArrayNewExecutor = { _, _, _ ->
            fail("ArrayNewExecutor should not be called in this scenario")
        }

        fun arrayNewDefaultExecutor(): ArrayNewDefaultExecutor = { _, _, _ ->
            fail("ArrayNewDefaultExecutor should not be called in this scenario")
        }

        fun arrayNewDataExecutor(): ArrayNewDataExecutor = { _, _, _, _ ->
            fail("ArrayNewDataExecutor should not be called in this scenario")
        }

        fun arrayNewElementExecutor(): ArrayNewElementExecutor = { _, _, _, _ ->
            fail("ArrayNewElementExecutor should not be called in this scenario")
        }

        fun arrayGetExecutor(): ArrayGetExecutor = { _, _, _, _ ->
            fail("ArrayGetExecutor should not be called in this scenario")
        }

        fun arraySetExecutor(): ArraySetExecutor = { _, _, _ ->
            fail("ArraySetExecutor should not be called in this scenario")
        }

        fun arrayLenExecutor(): ArrayLenExecutor = { _, _ ->
            fail("ArrayLenExecutor should not be called in this scenario")
        }

        fun refI31Executor(): RefI31Executor = { _ ->
            fail("RefI31Executor should not be called in this scenario")
        }

        fun i31GetExecutor(): I31GetExecutor = { _, _ ->
            fail("I31GetExecutor should not be called in this scenario")
        }

        fun arrayFillExecutor(): ArrayFillExecutor = { _, _, _ ->
            fail("ArrayFillExecutor should not be called in this scenario")
        }

        fun arrayCopyExecutor(): ArrayCopyExecutor = { _, _, _, _ ->
            fail("ArrayCopyExecutor should not be called in this scenario")
        }

        fun arrayInitDataExecutor(): ArrayInitDataExecutor = { _, _, _, _ ->
            fail("ArrayInitDataExecutor should not be called in this scenario")
        }

        fun arrayInitElementExecutor(): ArrayInitElementExecutor = { _, _, _, _ ->
            fail("ArrayInitElementExecutor should not be called in this scenario")
        }

        fun anyConvertExternExecutor(): AnyConvertExternExecutor = { _ ->
            fail("AnyConvertExternExecutor should not be called in this scenario")
        }

        fun externConvertAnyExecutor(): ExternConvertAnyExecutor = { _ ->
            fail("ExternConvertAnyExecutor should not be called in this scenario")
        }
    }
}
