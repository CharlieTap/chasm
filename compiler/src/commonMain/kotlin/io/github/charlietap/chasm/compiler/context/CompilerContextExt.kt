package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.ext.tag
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType

internal fun CompilerContext.function(index: Index.FunctionIndex): FunctionInstance =
    store.function(instance.functionAddresses[index.toInt()])

internal fun CompilerContext.table(index: Index.TableIndex): TableInstance =
    store.table(instance.tableAddresses[index.toInt()])

internal fun CompilerContext.memory(index: Index.MemoryIndex): MemoryInstance =
    store.memory(instance.memAddresses[index.toInt()])

internal fun CompilerContext.tag(index: Index.TagIndex): TagInstance =
    store.tag(instance.tagAddresses[index.toInt()])

internal fun CompilerContext.global(index: Index.GlobalIndex): GlobalInstance =
    store.global(instance.globalAddresses[index.toInt()])

internal fun CompilerContext.element(index: Index.ElementIndex): ElementInstance =
    store.element(instance.elemAddresses[index.toInt()])

internal fun CompilerContext.data(index: Index.DataIndex): DataInstance =
    store.data(instance.dataAddresses[index.toInt()])

internal fun CompilerContext.rtt(index: Index.TypeIndex): RTT = runtimeTypes[index.toInt()]

internal fun CompilerContext.arrayType(index: Index.TypeIndex): ArrayType =
    module.definedTypes[index.toInt()].asSubType.compositeType.arrayType()
        ?: error("type ${index.idx} is not an array type")

internal fun CompilerContext.structType(index: Index.TypeIndex): StructType =
    module.definedTypes[index.toInt()].asSubType.compositeType.structType()
        ?: error("type ${index.idx} is not a struct type")
