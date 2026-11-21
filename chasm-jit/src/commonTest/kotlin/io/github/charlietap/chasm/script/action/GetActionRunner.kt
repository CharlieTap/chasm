package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.compiled.ExternalType
import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.readGlobal
import io.github.charlietap.chasm.embedding.compiled.readGlobalReference
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.sweet.lib.action.GetAction
import io.github.charlietap.sweet.lib.command.Command

typealias GetActionRunner = (ScriptContext, Command, GetAction) -> ActionResult

fun GetActionRunner(
    context: ScriptContext,
    command: Command,
    action: GetAction,
): ActionResult {

    val instance = context.instances[action.moduleName]!!
    val global = exports(instance).global(action.field)
    val globalType = context.instanceModules[instance]
        ?.exports
        ?.firstOrNull { export -> export.name == action.field }
        ?.type as? ExternalType.Global

    if (global == null || globalType == null) {
        return ActionResult.Failure(command, "exported global not found")
    }

    val value = readGlobalValue(global, globalType.globalType.valueType)

    return ActionResult.Success(listOf(value))
}

private fun readGlobalValue(
    global: Global,
    type: ValueType,
): ExecutionValue = when (type) {
    is ValueType.Number -> numberValue(type.numberType, readGlobal(global))
    is ValueType.Reference -> referenceValue(type, readGlobalReference(global))
    else -> throw IllegalStateException("global has an unsupported type $type")
}

private fun numberValue(
    type: NumberType,
    word: Long,
): ExecutionValue = when (type) {
    NumberType.I32 -> NumberValue.I32(word.toInt())
    NumberType.I64 -> NumberValue.I64(word)
    NumberType.F32 -> NumberValue.F32(Float.fromBits(word.toInt()))
    NumberType.F64 -> NumberValue.F64(Double.fromBits(word))
}

private fun referenceValue(
    type: ValueType.Reference,
    reference: Any?,
): ExecutionValue = when (reference) {
    null -> ReferenceValue.Null(type.referenceType.heapType)
    is StructReference -> ReferenceValue.Struct(reference)
    is ArrayReference -> ReferenceValue.Array(reference)
    is I31Reference -> ReferenceValue.I31(reference)
    is FunctionReference -> ReferenceValue.Function(reference)
    is ExceptionReference -> ReferenceValue.Exception(reference)
    else -> ReferenceValue.Extern(reference)
}
