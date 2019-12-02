package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_WORLD_SET
import com.alekseyzhelo.eimob.types.Unit3
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.toByteArraySkipHeader
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class WorldSetBlock(
    bytes: ByteArray
) : Block {

    override val signature: UInt = SIG_WORLD_SET
    var windDirection: Unit3
    var windStrength: Float = 0.0f
        set(value) {
            require((value in 0.0..1.0)) { "Wind strength must be between 0.0 and 1.0" }
            field = value
        }
    var worldTime: Float = 0.0f
        set(value) {
            require((value in 0.0..24.0)) { "Time must be between 0.0 and 24.0" }
            field = value
        }
    var worldAmbient: Float = 0.0f
        set(value) {
            require((value in 0.0..1.0)) { "Ambient ? amount must be between 0.0 and 1.0" }
            field = value
        }
    var worldSunlight: Float = 0.0f
        set(value) {
            require((value in 0.0..1.0)) { "Sunlight amount must be between 0.0 and 1.0" }
            field = value
        }

    init {
        with(bytes.binaryStream()) {
            windDirection = Unit3(readMobFloat3(SIG_WIND_DIR, "Failed to read wind direction in WorldSet block"))
            windStrength = readMobFloat(SIG_WIND_STR, "Failed to read wind strength in WorldSet block")
            worldTime = readMobFloat(SIG_WS_TIME, "Failed to read world time in WorldSet block")
            worldAmbient = readMobFloat(SIG_WS_AMBIENT, "Failed to read ambient ? in WorldSet block")
            worldSunlight = readMobFloat(SIG_WS_SUNLIGHT, "Failed to read sunlight in WorldSet block")
        }
    }

    override fun getSize(): Int = 76  // not expected to change

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobFloat3(SIG_WIND_DIR, windDirection.toFloat3())
            writeMobFloat(SIG_WIND_STR, windStrength)
            writeMobFloat(SIG_WS_TIME, worldTime)
            writeMobFloat(SIG_WS_AMBIENT, worldAmbient)
            writeMobFloat(SIG_WS_SUNLIGHT, worldSunlight)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitWorldSetBlock(this)
    }

    override fun clone(): WorldSetBlock = WorldSetBlock(toByteArraySkipHeader())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorldSetBlock

        if (windDirection != other.windDirection) return false
        if (windStrength != other.windStrength) return false
        if (worldTime != other.worldTime) return false
        if (worldAmbient != other.worldAmbient) return false
        if (worldSunlight != other.worldSunlight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = windDirection.hashCode()
        result = 31 * result + windStrength.hashCode()
        result = 31 * result + worldTime.hashCode()
        result = 31 * result + worldAmbient.hashCode()
        result = 31 * result + worldSunlight.hashCode()
        return result
    }

    companion object {
        const val SIG_WIND_DIR = 0x0000ABD1u
        const val SIG_WIND_STR = 0x0000ABD2u
        const val SIG_WS_TIME = 0x0000ABD3u
        const val SIG_WS_AMBIENT = 0x0000ABD4u
        const val SIG_WS_SUNLIGHT = 0x0000ABD5u
    }
}