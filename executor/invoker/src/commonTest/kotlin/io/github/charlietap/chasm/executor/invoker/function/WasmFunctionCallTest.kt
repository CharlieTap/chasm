package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.expect
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.label
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.module.local
import io.github.charlietap.chasm.fixture.returnArity
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallTest {

    @Test
    fun `can execute a function call and return a result`() {

        val store = store()
        val stack = stack()

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
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
            i32(1),
            i32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = frame(
            arity = returnArity(functionType.results.types.size),
            state = Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = functionInstance.module,
            ),
        )

        val instructionBlockExecutor: InstructionBlockExecutor = { _stack, _label, _instructions, _params, _handler ->
            assertEquals(stack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions.map(::ModuleInstruction), _instructions)
            assertEquals(emptyList(), _params)

            assertEquals(frame, stack.peekFrameOrNull())
            assertEquals(1, stack.framesDepth())

            Ok(Unit)
        }

        val actual = WasmFunctionCall(
            store = store,
            stack = stack,
            instance = functionInstance,
            instructionBlockExecutor = instructionBlockExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
    }

    @Test
    fun `can execute a tail recursive function call and return a result`() {

        val store = store()
        val stack = stack()

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
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
            i32(1),
            i32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = frame(
            arity = returnArity(functionType.results.types.size),
            state = Stack.Entry.ActivationFrame.State(
                locals = locals,
                module = functionInstance.module,
            ),
        )

        stack.push(frame)

        val instructionBlockExecutor: InstructionBlockExecutor = { _stack, _label, _instructions, _params, _handler ->
            assertEquals(stack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions.map(::ModuleInstruction), _instructions)
            assertEquals(emptyList(), _params)

            assertEquals(frame, stack.peekFrameOrNull())
            assertEquals(1, stack.framesDepth())

            Ok(Unit)
        }

        val actual = ReturnWasmFunctionCall(
            store = store,
            stack = stack,
            instance = functionInstance,
            instructionBlockExecutor = instructionBlockExecutor,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
    }
}
