package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluatorImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun TableInitializerImpl(
    store: Store,
    instance: ModuleInstance,
    module: Module,
): Result<Unit, InvocationError> =
    TableInitializerImpl(
        store = store,
        instance = instance,
        module = module,
        evaluator = ::ExpressionEvaluatorImpl,
    )

internal fun TableInitializerImpl(
    store: Store,
    instance: ModuleInstance,
    module: Module,
    evaluator: ExpressionEvaluator,
): Result<Unit, InvocationError> = binding {

    module.elementSegments.filter { segment ->
        segment.mode is ElementSegment.Mode.Active
    }.forEach { segment ->
        val expressionCount = segment.initExpressions.size
        val mode = segment.mode as ElementSegment.Mode.Active
        val expression = Expression(
            instructions = mode.offsetExpr.instructions + listOf(
                NumericInstruction.I32Const(0),
                NumericInstruction.I32Const(expressionCount),
                TableInstruction.TableInit(segment.idx, mode.tableIndex),
                TableInstruction.ElemDrop(segment.idx),
            ),
        )
        evaluator(store, instance, expression, Arity.SIDE_EFFECT).bind()
    }

    module.elementSegments.filter { segment ->
        segment.mode is ElementSegment.Mode.Declarative
    }.forEach { segment ->
        val expression = Expression(
            instructions = listOf(TableInstruction.ElemDrop(segment.idx)),
        )
        evaluator(store, instance, expression, Arity.SIDE_EFFECT).bind()
    }
}
