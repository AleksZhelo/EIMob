package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobParticle(
    bytes: ByteArray
) : MobMapEntity() {

    override val signature: UInt = SIG_PARTICLE
    var comment: String
    var type: Int
    var scale: Float

    init {
        with(bytes.binaryStream()) {
            id = readMobInt(SIG_ID, "Failed to read id in MobParticle block")
            location = readMobFloat3(SIG_LOCATION, "Failed to read location in MobParticle block")
            name = readMobString(SIG_NAME, "Failed to read name in MobParticle block")
            comment = readMobString(SIG_COMMENT, "Failed to read comment in MobParticle block")
            type = readMobInt(SIG_TYPE, "Failed to read type in MobParticle block")
            scale = readMobFloat(SIG_SCALE, "Failed to read scale in MobParticle block")
        }
    }

    override fun getSize(): Int = 7 * entryHeaderSize + 4 + 12 + name.length + comment.length + 4 + 4

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobInt(SIG_ID, id)
            writeMobFloat3(SIG_LOCATION, location)
            writeMobString(SIG_NAME, name)
            writeMobString(SIG_COMMENT, comment)
            writeMobInt(SIG_TYPE, type)
            writeMobFloat(SIG_SCALE, scale)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobParticle(this)
    }

    override fun clone(): MobParticle = MobParticle(toByteArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MobParticle

        if (comment != other.comment) return false
        if (type != other.type) return false
        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + type
        result = 31 * result + scale.hashCode()
        return result
    }

    companion object {
        const val SIG_ID = 0x0000DD02u
        const val SIG_LOCATION = 0x0000DD03u
        const val SIG_NAME = 0x0000DD05u
        const val SIG_COMMENT = 0x0000DD04u
        const val SIG_TYPE = 0x0000DD06u
        const val SIG_SCALE = 0x0000DD07u
    }
}