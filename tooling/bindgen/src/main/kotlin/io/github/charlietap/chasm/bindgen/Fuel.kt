package io.github.charlietap.chasm.bindgen

@JvmInline
value class Fuel(
    val units: Long,
) : Comparable<Fuel> {
    init {
        require(units >= 0) { "Fuel cannot be negative" }
    }

    override fun compareTo(
        other: Fuel,
    ): Int = units.compareTo(other.units)

    operator fun plus(
        other: Fuel,
    ): Fuel = Fuel(if (units > Long.MAX_VALUE - other.units) Long.MAX_VALUE else units + other.units)

    override fun toString(): String = "$units fuel"
}

val Int.fuel: Fuel get() = Fuel(toLong())

val Long.fuel: Fuel get() = Fuel(this)
