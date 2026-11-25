package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
class GlobalOperations(
    private val checks: ReferenceChecks,
) {

    fun readGlobal(global: GlobalInstance): Long {

        require(global.type.valueType !is ValueType.Reference) { "the global holds a reference" }

        return global.value
    }

    fun writeGlobal(
        global: GlobalInstance,
        value: Long,
    ) {

        require(global.type.valueType !is ValueType.Reference) { "the global holds a reference" }
        require(global.type.mutability == Mutability.Var) { "the global is immutable" }

        global.value = value
    }

    fun readGlobalReference(global: ReferenceGlobal): Any? {
        return global.value
    }

    fun writeGlobalReference(
        global: ReferenceGlobal,
        value: Any?,
    ) {

        require(global.instance.type.mutability == Mutability.Var) { "the global is immutable" }
        require(check(global).accepts(value)) { "the value does not match the global's type" }

        global.value = value
    }

    fun unsafeWriteGlobalReference(
        global: ReferenceGlobal,
        value: Any?,
    ) {
        global.value = value
    }

    private fun check(global: ReferenceGlobal): ReferenceCheck {
        return global.check ?: checks.check((global.instance.type.valueType as ValueType.Reference).referenceType, null).also { check -> global.check = check }
    }
}
