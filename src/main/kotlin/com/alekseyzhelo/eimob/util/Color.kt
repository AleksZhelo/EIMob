package com.alekseyzhelo.eimob.util

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

    override fun toString(): String {
        return "r=$r, g=$g, b=$b"
    }
}