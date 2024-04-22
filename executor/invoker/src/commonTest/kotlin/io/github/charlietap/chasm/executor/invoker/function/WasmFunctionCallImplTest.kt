package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.expect
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.invoker.flow.ReturnException
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.type.ext.definedType
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.label
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.module.local
import io.github.charlietap.chasm.fixture.returnArity
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.value
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallImplTest {

    @Test
    fun `can execute a function call and return a result`() {

        val store = store()
        val stack = stack()

        val functionType = functionType(
            params = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                ),
            ),
            results = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val function = function(
            locals = listOf(
                local(type = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func))),
            ),
            body = Expression(
                listOf(
                    NumericInstruction.I32GeS,
                    NumericInstruction.I32GeU,
                ),
            ),
        )

        val functionInstance = FunctionInstance.WasmFunction(
            type = definedType,
            module = moduleInstance(),
            function = function,
        )

        val label = label(
            arity = returnArity(functionType.params.types.size),
        )

        val params = listOf(
            NumberValue.I32(1),
            NumberValue.I32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = Stack.Entry.ActivationFrame(
            arity = returnArity(functionType.results.types.size),
            state = Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = functionInstance.module,
            ),
        )

        val instructionBlockExecutor: InstructionBlockExecutor = { _store, _stack, _label, _instructions, _params ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions, _instructions)
            assertEquals(emptyList(), _params)

            assertEquals(frame, stack.peekFrameOrNull())
            assertEquals(1, stack.framesDepth())

            Ok(Unit)
        }

        val actual = WasmFunctionCallImpl(
            store = store,
            stack = stack,
            instance = functionInstance,
            tailRecursion = false,
            instructionBlockExecutor = instructionBlockExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.framesDepth())
    }

    @Test
    fun `can execute a tail recursive function call and return a result`() {

        val store = store()
        val stack = stack()

        val functionType = functionType(
            params = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                ),
            ),
            results = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val function = function(
            locals = listOf(
                local(type = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func))),
            ),
            body = Expression(
                listOf(
                    NumericInstruction.I32GeS,
                    NumericInstruction.I32GeU,
                ),
            ),
        )

        val functionInstance = FunctionInstance.WasmFunction(
            type = definedType,
            module = moduleInstance(),
            function = function,
        )

        val label = label(
            arity = returnArity(functionType.params.types.size),
        )

        val params = listOf(
            NumberValue.I32(1),
            NumberValue.I32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = Stack.Entry.ActivationFrame(
            arity = returnArity(functionType.results.types.size),
            state = Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = functionInstance.module,
            ),
        )

        stack.push(frame)

        val instructionBlockExecutor: InstructionBlockExecutor = { _store, _stack, _label, _instructions, _params ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions, _instructions)
            assertEquals(emptyList(), _params)

            assertEquals(frame, stack.peekFrameOrNull())
            assertEquals(1, stack.framesDepth())

            Ok(Unit)
        }

        val actual = WasmFunctionCallImpl(
            store = store,
            stack = stack,
            instance = functionInstance,
            tailRecursion = true,
            instructionBlockExecutor = instructionBlockExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
    }

    @Test
    fun `can catch a return exception then clean up the stack and return a result`() {

        val store = store()
        val stack = stack()

        val functionType = functionType(
            params = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                ),
            ),
            results = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I64),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val function = function(
            locals = listOf(
                local(type = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func))),
            ),
            body = Expression(
                listOf(
                    NumericInstruction.I32GeS,
                    NumericInstruction.I32GeU,
                ),
            ),
        )

        val functionInstance = FunctionInstance.WasmFunction(
            type = definedType,
            module = moduleInstance(),
            function = function,
        )

        val label = label(
            arity = returnArity(functionType.params.types.size),
        )

        val params = listOf(
            NumberValue.I32(1),
            NumberValue.I32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = Stack.Entry.ActivationFrame(
            arity = returnArity(functionType.results.types.size),
            state = Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = functionInstance.module,
            ),
        )

        val results = listOf(
            NumberValue.I64(117),
        )
        val instructionBlockExecutor: InstructionBlockExecutor = { _store, _stack, _label, _instructions, _params ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions, _instructions)
            assertEquals(emptyList(), _params)

            assertEquals(frame, stack.peekFrameOrNull())

            repeat(2) { _ ->
                stack.push(label())
            }

            repeat(3) { _ ->
                stack.push(value())
            }

            throw ReturnException(results)
        }

        val actual = WasmFunctionCallImpl(
            store = store,
            stack = stack,
            instance = functionInstance,
            tailRecursion = false,
            instructionBlockExecutor = instructionBlockExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(0, stack.framesDepth())
        assertEquals(0, stack.labelsDepth())
        assertEquals(1, stack.valuesDepth())
        assertEquals(results[0], stack.popValueOrNull()?.value)
    }
}
