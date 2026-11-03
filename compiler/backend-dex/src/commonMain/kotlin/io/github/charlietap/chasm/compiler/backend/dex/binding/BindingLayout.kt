package io.github.charlietap.chasm.compiler.backend.dex.binding

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.isValue

internal class BindingLayout {

    private val objectList = ArrayList<Resource>()
    private val valueList = ArrayList<Resource>()
    private val objectSlots = HashMap<Resource, Int>()
    private val valueSlots = HashMap<Resource, Int>()

    val objects: List<Resource> get() = objectList
    val values: List<Resource> get() = valueList

    fun objectSlot(
        resource: Resource,
    ): Int {
        require(!resource.isValue) { "$resource is a value" }
        return objectSlots.getOrPut(resource) {
            objectList += resource
            objectList.size
        }
    }

    fun valueSlot(
        resource: Resource,
    ): Int {
        require(resource.isValue) { "$resource is not a value" }
        return valueSlots.getOrPut(resource) {
            valueList += resource
            valueList.size - 1
        }
    }
}
