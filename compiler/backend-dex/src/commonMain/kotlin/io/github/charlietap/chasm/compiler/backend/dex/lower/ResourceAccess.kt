package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.dex.analysis.CachedConstant
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.Conversion

internal fun LowerContext.loadObject(
    register: Int,
    resource: Resource,
    type: String,
) {
    val cached = cachedConstantIndex[resource]
    if (cached != null && cachesLoaded) return emit.moveObject(register, registers.useCachedConstant(cached))
    emit.const(Registers.I1, module.layout.objectSlot(resource))
    emit.arrayGet(ArrayKind.Object, register, registers.binding, Registers.I1)
    emit.checkCast(register, type)
}

internal fun LowerContext.loadCachedConstant(
    constant: CachedConstant,
) {
    when (constant) {
        is CachedConstant.ObjectResource -> loadObject(Registers.OBJECT, constant.resource, constant.type)
        is CachedConstant.Values -> loadValues()
    }
}

private fun LowerContext.loadValues() {
    emit.const(Registers.I1, 0)
    emit.arrayGet(ArrayKind.Object, Registers.OBJECT, registers.binding, Registers.I1)
    emit.checkCast(Registers.OBJECT, "[J")
}

internal fun LowerContext.loadValue(
    pair: Int,
    resource: Resource,
) {
    val cached = cachedValuesIndex
    if (cached != null && cachesLoaded) emit.moveObject(Registers.OBJECT, registers.useCachedConstant(cached)) else loadValues()
    emit.const(Registers.I1, module.layout.valueSlot(resource))
    emit.arrayGet(ArrayKind.Wide, pair, Registers.OBJECT, Registers.I1)
}

internal fun LowerContext.loadValueInt(
    register: Int,
    resource: Resource,
) {
    loadValue(Registers.W2, resource)
    emit.convert(Conversion.LongToInt, register, Registers.W2)
}
