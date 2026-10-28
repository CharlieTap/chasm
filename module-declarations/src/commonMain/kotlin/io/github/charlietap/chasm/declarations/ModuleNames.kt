package io.github.charlietap.chasm.declarations

class ModuleNames(
    val moduleName: String?,
    val functionNameIndices: IntArray,
    val functionNames: Array<String>,
    val exportedLocalNames: Map<Int, List<String>>,
    val trapNameIndices: IntArray = IntArray(0),
    val trapNames: Array<String> = emptyArray(),
) {

    init {
        require(functionNameIndices.size == functionNames.size) {
            "function name columns differ in length: ${functionNameIndices.size} indices, ${functionNames.size} names"
        }
        require(trapNameIndices.size == trapNames.size) {
            "trap name columns differ in length: ${trapNameIndices.size} indices, ${trapNames.size} names"
        }
    }

    fun functionName(
        functionIndex: Int,
    ): String? = find(functionNameIndices, functionNames, functionIndex)

    fun trapFunctionName(
        functionIndex: Int,
    ): String? =
        find(trapNameIndices, trapNames, functionIndex) ?: functionName(functionIndex)

    override fun equals(
        other: Any?,
    ): Boolean {
        if (this === other) return true
        if (other !is ModuleNames) return false
        return moduleName == other.moduleName &&
            functionNameIndices.contentEquals(other.functionNameIndices) &&
            functionNames.contentEquals(other.functionNames) &&
            exportedLocalNames == other.exportedLocalNames &&
            trapNameIndices.contentEquals(other.trapNameIndices) &&
            trapNames.contentEquals(other.trapNames)
    }

    override fun hashCode(): Int {
        var result = moduleName.hashCode()
        result = 31 * result + functionNameIndices.contentHashCode()
        result = 31 * result + functionNames.contentHashCode()
        result = 31 * result + exportedLocalNames.hashCode()
        result = 31 * result + trapNameIndices.contentHashCode()
        result = 31 * result + trapNames.contentHashCode()
        return result
    }

    companion object {
        val NONE = ModuleNames(null, IntArray(0), emptyArray(), emptyMap())

        private fun find(
            indices: IntArray,
            names: Array<String>,
            functionIndex: Int,
        ): String? {
            var low = 0
            var high = indices.size - 1
            while (low <= high) {
                val middle = (low + high) ushr 1
                val index = indices[middle]
                when {
                    index < functionIndex -> low = middle + 1
                    index > functionIndex -> high = middle - 1
                    else -> return names[middle]
                }
            }
            return null
        }
    }
}
