package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.asPredecodingContext
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.module.DataSegment
import io.github.charlietap.chasm.predecoder.ExpressionPredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.runtime.Arity
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression

internal typealias MemoryInitializer = (InstantiationContext, ModuleInstance) -> Result<Unit, ModuleTrapError>

internal fun MemoryInitializer(
    context: InstantiationContext,
    instance: ModuleInstance,
): Result<Unit, ModuleTrapError> =
    MemoryInitializer(
        context = context,
        instance = instance,
        evaluator = ::ExpressionEvaluator,
        expressionPredecoder = ::ExpressionPredecoder,
    )

internal inline fun MemoryInitializer(
    context: InstantiationContext,
    instance: ModuleInstance,
    crossinline evaluator: ExpressionEvaluator,
    crossinline expressionPredecoder: Predecoder<Expression, RuntimeExpression>,
): Result<Unit, ModuleTrapError> = binding {

    val module = context.module
    module.dataSegments
        .filter { segment ->
            segment.mode is DataSegment.Mode.Active
        }.forEach { segment ->
            val mode = segment.mode as DataSegment.Mode.Active
            val size = segment.initData.size
            val expression = Expression(
                mode.offset.instructions + listOf(
                    NumericInstruction.I32Const(0),
                    NumericInstruction.I32Const(size),
                    MemoryInstruction.MemoryInit(mode.memoryIndex, segment.idx),
                    MemoryInstruction.DataDrop(segment.idx),
                ),
            )
            val runtimeExpression = expressionPredecoder(context.asPredecodingContext(), expression).bind()

            evaluator(context.config, context.store, instance, runtimeExpression, Arity.Return.SIDE_EFFECT).bind()
        }
}
