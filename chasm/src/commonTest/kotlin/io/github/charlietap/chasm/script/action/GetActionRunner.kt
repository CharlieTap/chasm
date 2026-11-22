package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.type.AbstractHeapType
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
        ?: return ActionResult.Failure(command, "exported global not found")

    return ActionResult.Success(listOf(readGlobalValue(global, global.type.valueType)))
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
): ExecutionValue = when {
    reference == null -> ReferenceValue.Null(type.referenceType.heapType)
    type.referenceType.heapType == AbstractHeapType.Extern -> ReferenceValue.Extern(reference)
    else -> objectValue(reference)
}

private fun objectValue(reference: Any): ExecutionValue = when (reference) {
    is StructReference -> ReferenceValue.Struct(reference)
    is ArrayReference -> ReferenceValue.Array(reference)
    is I31Reference -> ReferenceValue.I31(reference)
    is FunctionReference -> ReferenceValue.Function(reference)
    is ExceptionReference -> ReferenceValue.Exception(reference)
    else -> ReferenceValue.Extern(reference)
}
