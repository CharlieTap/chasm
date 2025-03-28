package io.github.charlietap.chasm.type

class RTT(
    val type: DefinedType,
    val cache: Map<DefinedType, RTT>,
) {
    val superTypes: List<RTT> by lazy {
        val superTypes = mutableListOf<RTT>()
        var superType = type.parent
        while (superType != null) {
            superTypes.add(cache[superType]!!)
            superType = superType.parent
        }
        superTypes
    }

    //  Force evaluation of lazy property AOT
    fun hydrate() {
        check(superTypes.isNotEmpty() || superTypes.isEmpty()) // Blackhole
    }
}
