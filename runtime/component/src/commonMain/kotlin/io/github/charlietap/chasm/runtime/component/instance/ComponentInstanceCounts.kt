package io.github.charlietap.chasm.runtime.component.instance

data class ComponentInstanceCounts(
    val componentInstances: Int = 0,
    val coreInstances: Int = 0,
    val coreFunctions: Int = 0,
    val memories: Int = 0,
    val reallocs: Int = 0,
    val postReturns: Int = 0,
    val resourceTypes: Int = 0,
    val hostFunctions: Int = 0,
) {
    init {
        require(componentInstances >= 0)
        require(coreInstances >= 0)
        require(coreFunctions >= 0)
        require(memories >= 0)
        require(reallocs >= 0)
        require(postReturns >= 0)
        require(resourceTypes >= 0)
        require(hostFunctions >= 0)
    }
}
