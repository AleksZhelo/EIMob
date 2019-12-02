package com.alekseyzhelo.eimob.types

@Suppress("MemberVisibilityCanBePrivate")
class Color(r: Float, g: Float, b: Float) {

    var r: Float = r
        set(value) {
            require(value in 0.0..1.0) { "Bad color value $value" }
            field = value
        }
    var g: Float = g
        set(value) {
            require(value in 0.0..1.0) { "Bad color value $value" }
            field = value
        }
    var b: Float = b
        set(value) {
            require(value in 0.0..1.0) { "Bad color value $value" }
            field = value
        }

    constructor(triplet: Float3) : this(triplet.x, triplet.y, triplet.z)

    fun toFloat3(): Float3 = Float3(r, g, b)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color

        if (r != other.r) return false
        if (g != other.g) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = r.hashCode()
        result = 31 * result + g.hashCode()
        result = 31 * result + b.hashCode()
        return result
    }

    override fun toString(): String {
        return "r=$r, g=$g, b=$b"
    }
}