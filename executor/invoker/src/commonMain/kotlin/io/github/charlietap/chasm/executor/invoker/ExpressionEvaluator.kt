package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

typealias ExpressionEvaluator = (Store, ModuleInstance, Expression, Arity.Return) -> Result<ExecutionValue?, InvocationError>
