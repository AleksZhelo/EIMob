package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.toByteArraySkipHeader
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobSound(
    bytes: ByteArray
) : MobMapEntity() {

    override val signature: UInt = SIG_SOUND
    val soundResources: ArrayList<String>
    var range: Int
    var range2: Int
    var comment: String
    var volumeMin: Int
    var volumeMax: Int
    var isAmbient: Boolean
    var isMusic: Boolean

    init {
        with(bytes.binaryStream()) {
            soundResources = readMobStringArray(
                SIG_SOUND_RESOURCES, "Failed to read sounds in MobSound block",
                "Unexpected signature in sounds array"
            ).toCollection(ArrayList())
            id = readMobInt(SIG_ID, "Failed to read id in MobSound block")
            location = readMobFloat3(SIG_LOCATION, "Failed to read location in MobSound block")
            range = readMobInt(SIG_RANGE, "Failed to read range in MobSound block")
            range2 = readMobInt(SIG_RANGE2, "Failed to read range2 in MobSound block")
            name = readMobString(SIG_NAME, "Failed to read name in MobSound block")
            comment = readMobString(SIG_COMMENT, "Failed to read comment in MobSound block")
            volumeMin = readMobInt(SIG_VOLUME_MIN, "Failed to read volumeMin in MobSound block")
            volumeMax = readMobInt(SIG_VOLUME_MAX, "Failed to read volumeMax in MobSound block")
            isAmbient = readMobBoolean(SIG_IS_AMBIENT, "Failed to read isAmbient in MobSound block")
            isMusic = readMobBoolean(SIG_IS_MUSIC, "Failed to read isMusic in MobSound block")
        }
    }

    override fun getSize(): Int = 12 * entryHeaderSize + soundResources.toTypedArray().mobEntrySize() +
            4 + 12 + 4 + 4 + name.length + comment.length + 4 + 4 + 1 + 1

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobStringArray(SIG_SOUND_RESOURCES, soundResources.toTypedArray())
            writeMobInt(SIG_ID, id)
            writeMobFloat3(SIG_LOCATION, location)
            writeMobInt(SIG_RANGE, range)
            writeMobInt(SIG_RANGE2, range2)
            writeMobString(SIG_NAME, name)
            writeMobString(SIG_COMMENT, comment)
            writeMobInt(SIG_VOLUME_MIN, volumeMin)
            writeMobInt(SIG_VOLUME_MAX, volumeMax)
            writeMobBoolean(SIG_IS_AMBIENT, isAmbient)
            writeMobBoolean(SIG_IS_MUSIC, isMusic)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobSound(this)
    }

    override fun clone(): MobSound = MobSound(toByteArraySkipHeader())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MobSound

        if (soundResources != other.soundResources) return false
        if (range != other.range) return false
        if (range2 != other.range2) return false
        if (comment != other.comment) return false
        if (volumeMin != other.volumeMin) return false
        if (volumeMax != other.volumeMax) return false
        if (isAmbient != other.isAmbient) return false
        if (isMusic != other.isMusic) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + soundResources.hashCode()
        result = 31 * result + range
        result = 31 * result + range2
        result = 31 * result + comment.hashCode()
        result = 31 * result + volumeMin
        result = 31 * result + volumeMax
        result = 31 * result + isAmbient.hashCode()
        result = 31 * result + isMusic.hashCode()
        return result
    }

    companion object {
        const val SIG_SOUND_RESOURCES = 0x0000CC0Au
        const val SIG_ID = 0x0000CC02u
        const val SIG_LOCATION = 0x0000CC03u
        const val SIG_RANGE = 0x0000CC04u
        const val SIG_RANGE2 = 0x0000CC0Bu
        const val SIG_NAME = 0x0000CC05u
        const val SIG_COMMENT = 0x0000CC08u
        const val SIG_VOLUME_MIN = 0x0000CC06u
        const val SIG_VOLUME_MAX = 0x0000CC07u
        const val SIG_IS_AMBIENT = 0x0000CC0Du
        const val SIG_IS_MUSIC = 0x0000CC0Eu
    }
}