package io.github.charlietap.chasm.type

class RTT(
    val type: DefinedType,
    val cache: Map<DefinedType, RTT>,
) {
    val superTypes: List<RTT> by lazy {
        var superType = type.parent
        buildList {
            while (superType != null) {
                add(cache[superType]!!)
                superType = superType.parent
            }
        }
    }

    //  Force evaluation of lazy property AOT
    fun hydrate() {
        check(superTypes.isNotEmpty() || superTypes.isEmpty()) // Blackhole
    }
}
