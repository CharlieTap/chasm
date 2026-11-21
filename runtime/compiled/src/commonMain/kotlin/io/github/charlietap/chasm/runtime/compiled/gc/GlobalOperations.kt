package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType

class GlobalOperations(
    private val storage: ReferenceStorage,
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

    fun readGlobalReference(global: GlobalInstance): Any? {

        require(global.type.valueType is ValueType.Reference) { "the global holds a number" }

        return storage.referenceGlobal(global).value
    }

    fun writeGlobalReference(
        global: GlobalInstance,
        value: Any?,
    ) {

        val type = requireNotNull(global.type.valueType as? ValueType.Reference) { "the global holds a number" }
        val reference = storage.referenceGlobal(global)
        val check = reference.check ?: checks.check(type.referenceType, null).also { check -> reference.check = check }

        require(global.type.mutability == Mutability.Var) { "the global is immutable" }
        require(check.accepts(value)) { "the value does not match the global's type" }

        reference.value = value
    }

    fun unsafeWriteGlobalReference(
        global: GlobalInstance,
        value: Any?,
    ) {
        storage.referenceGlobal(global).value = value
    }
}
