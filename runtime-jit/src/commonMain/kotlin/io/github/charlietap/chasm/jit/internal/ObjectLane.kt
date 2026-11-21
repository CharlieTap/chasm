@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.internal.ExportedFunction
import io.github.charlietap.chasm.embedding.internal.exportedFunction
import io.github.charlietap.chasm.embedding.internal.exportedTrap
import io.github.charlietap.chasm.embedding.internal.functionAddress
import io.github.charlietap.chasm.embedding.internal.storeDropped
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.HostResources
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.GcReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedArray
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedFunction
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedObjects
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedReferenceBridge
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStruct
import io.github.charlietap.chasm.runtime.compiled.gc.ObjectLaneHostFunction
import io.github.charlietap.chasm.runtime.compiled.gc.PendingExceptions
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceCheck
import io.github.charlietap.chasm.runtime.compiled.gc.WasmException
import io.github.charlietap.chasm.runtime.compiled.gc.enterObjects
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import java.util.concurrent.atomic.AtomicReference
import io.github.charlietap.chasm.runtime.error.InvocationError as RuntimeInvocationError
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
fun interface ObjectLaneCallback {

    fun call(
        stack: LongArray,
        references: Array<Any?>,
        parameters: Int,
        results: Int,
        caller: HostModuleInstance,
        resources: HostResources,
    )
}

@InternalChasmApi
fun _objectLaneFunction(
    store: Store,
    type: FunctionType,
    callback: ObjectLaneCallback,
): Function {
    managedBridge(store.store)
    return function(store, type, CallbackHostFunction(type, callback))
}

@InternalChasmApi
fun _prepareObjectCall(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<_PreparedObjectCall, ExecutionError.Rejected> {
    val exported = when (val lookup = exportedFunction(store, instance, name)) {
        is ChasmResult.Success -> lookup.result
        is ChasmResult.Error -> return ChasmResult.Error(ExecutionError.Rejected(lookup.error.error))
    }
    val bridge = managedBridge(exported.store)
    val owner = bridge.definedType(HostGcType(exported.function.rtt.value))
    val parameters = exported.function.functionType.params.types
    val referenceParameters = parameters.indices.filter { index -> parameters[index] is ValueType.Reference }.toIntArray()
    val parameterChecks = Array(referenceParameters.size) { position ->
        bridge.checks.check((parameters[referenceParameters[position]] as ValueType.Reference).referenceType, owner)
    }

    return ChasmResult.Success(_PreparedObjectCall(store, instance, exported, referenceParameters, parameterChecks, bridge))
}

@InternalChasmApi
class _PreparedObjectCall internal constructor(
    private val embedding: Store,
    private val instance: Instance,
    private val exported: ExportedFunction,
    private val referenceParameters: IntArray,
    private val parameterChecks: Array<ReferenceCheck>,
    private val exceptions: PendingExceptions,
) {

    val type: FunctionType = exported.function.functionType

    val slots: Int = maxOf(type.params.types.size, type.results.types.size)

    private val carriesReferences = (type.params.types + type.results.types).any { value -> value is ValueType.Reference }

    private val idle = AtomicReference<ExecutionContext?>()

    fun call(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {

        val rejection = rejection(stack, references)
        if (rejection != null) {
            return rejection
        }

        for (position in referenceParameters.indices) {
            val index = referenceParameters[position]
            if (!parameterChecks[position].accepts(references[index])) {
                return ChasmResult.Error(ExecutionError.Rejected("argument $index does not match its type"))
            }
        }

        return enter(stack, references)
    }

    fun unsafeCall(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return rejection(stack, references) ?: enter(stack, references)
    }

    private fun rejection(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult.Error<ExecutionError.Rejected>? {

        val message = when {
            storeDropped(embedding) -> RuntimeInvocationError.StoreDropped.toString()
            instance.instance.deallocated -> RuntimeInvocationError.InvocationOfADeinstantiatedInstance.toString()
            exported.store.functions.getOrNull(exported.address.address) !== exported.function -> RuntimeInvocationError.FunctionLookupFailed(exported.address).toString()
            else -> null
        }
        if (message != null) {
            return ChasmResult.Error(ExecutionError.Rejected(message))
        }

        require(stack.size >= slots) { "the stack holds ${stack.size} slots but the call needs $slots" }
        require(!carriesReferences || references.size >= slots) { "the references hold ${references.size} slots but the call needs $slots" }

        return null
    }

    private fun enter(
        stack: LongArray,
        references: Array<Any?>,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {

        val context = idle.getAndSet(null) ?: try {
            context()
        } catch (exception: InvocationException) {
            return failure(exception.error)
        }

        val error = enterObjects(context, exported.function, stack, references)
        if (error != null) {
            return failure(error)
        }

        idle.set(context)
        return SUCCESS
    }

    private fun context(): ExecutionContext {
        val context = ExecutionContext(ValueStack(slots + 1, exported.store.maxStackSlots), exported.store, exported.instance, exported.config)
        context.nestedInterpreter = exported.store.interpreter
        return context
    }

    private fun failure(error: RuntimeInvocationError): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return if (error == RuntimeInvocationError.ThrownException) {
            ChasmResult.Error(ExecutionError.Thrown(exceptions.take()))
        } else {
            ChasmResult.Error(ExecutionError.Trapped(exportedTrap(embedding, error)))
        }
    }

    private companion object {
        val SUCCESS = ChasmResult.Success(Unit)
    }
}

@InternalChasmApi
fun _objects(store: Store): ManagedObjects {
    return managedBridge(store.store).objects
}

@InternalChasmApi
fun _externReference(
    value: Any,
): Any {
    return value
}

@InternalChasmApi
fun _hostValue(
    store: Store,
    reference: Any?,
): Any? {
    return when (reference) {
        null, is GcReference -> null
        else -> managedBridge(store.store).hostValue(reference)
    }
}

@InternalChasmApi
fun _referenceKind(
    reference: Any?,
): Int {
    return when (reference) {
        null -> REFERENCE_NULL
        is ManagedFunction -> REFERENCE_FUNCTION
        is I31Reference -> REFERENCE_I31
        is ManagedStruct -> REFERENCE_STRUCT
        is ManagedArray -> REFERENCE_ARRAY
        is WasmException -> REFERENCE_EXCEPTION
        else -> REFERENCE_HOST_VALUE
    }
}

@InternalChasmApi
fun _functionReference(
    store: Store,
    function: Function,
): Any {
    return managedBridge(store.store).functions.at(functionAddress(store, function))
}

@InternalChasmApi
fun _referenceValue(
    store: Store,
    reference: Any?,
    extern: Boolean,
): ReferenceValue {
    return managedBridge(store.store).toHost(reference, extern).toReferenceValue()
}

@InternalChasmApi
fun _wasmReference(
    store: Store,
    value: ReferenceValue,
): Any? {
    return managedBridge(store.store).fromHost(value.toLongFromBoxed())
}

@InternalChasmApi
const val REFERENCE_NULL = 0

@InternalChasmApi
const val REFERENCE_HOST_VALUE = 1

@InternalChasmApi
const val REFERENCE_FUNCTION = 2

@InternalChasmApi
const val REFERENCE_I31 = 3

@InternalChasmApi
const val REFERENCE_STRUCT = 4

@InternalChasmApi
const val REFERENCE_ARRAY = 5

@InternalChasmApi
const val REFERENCE_EXCEPTION = 6

internal fun managedBridge(
    store: RuntimeStore,
): ManagedReferenceBridge {
    synchronized(store) {
        return store.referenceBridge as? ManagedReferenceBridge ?: ManagedReferenceBridge(store).also { bridge -> store.referenceBridge = bridge }
    }
}

private class CallbackHostFunction(
    type: FunctionType,
    private val callback: ObjectLaneCallback,
) : ObjectLaneHostFunction(type) {

    override fun call(
        stack: LongArray,
        references: Array<Any?>,
        parameters: Int,
        results: Int,
        caller: HostModuleInstance,
        resources: HostResources,
    ) {
        callback.call(stack, references, parameters, results, caller, resources)
    }
}
