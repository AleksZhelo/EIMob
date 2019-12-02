package com.alekseyzhelo.eimob.types

import com.alekseyzhelo.eimob.util.norm3
import kotlin.math.abs

data class Float2(var x: Float, var y: Float) {
    override fun toString(): String {
        return "x=$x, y=$y"
    }
}

data class Float3(var x: Float, var y: Float, var z: Float) {
    override fun toString(): String {
        return "x=$x, y=$y, z=$z"
    }
}

data class Float4(var x: Float, var y: Float, var z: Float, var w: Float) {
    override fun toString(): String {
        return "x=$x, y=$y, z=$z, w=$w"
    }
}

class Unit3(x: Float, y: Float, z: Float) {

    var x: Float = x
        private set
    var y: Float = y
        private set
    var z: Float = z
        private set

    init {
        normalize()
    }

    constructor(triplet: Float3) : this(triplet.x, triplet.y, triplet.z) {
        normalize()
    }

    fun toFloat3(): Float3 =
        Float3(x, y, z)

    private fun normalize() {
        val norm = norm3(x.toDouble(), y.toDouble(), z.toDouble())
        if (abs(1.0f - norm) > 1e-7f) {
            x /= norm.toFloat()
            y /= norm.toFloat()
            z /= norm.toFloat()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Unit3

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString(): String {
        return "x=$x, y=$y, z=$z"
    }
}