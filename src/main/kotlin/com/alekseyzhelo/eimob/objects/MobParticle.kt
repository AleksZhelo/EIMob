package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.blocks.ObjectsBlock.Companion.SIG_PARTICLE
import com.alekseyzhelo.eimob.util.Float3
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobParticle(
    bytes: ByteArray
) : Block {

    override val signature: UInt = SIG_PARTICLE
    val id: Int
    val location: Float3
    var name: String
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

    companion object {
        const val SIG_ID = 0x0000DD02u
        const val SIG_LOCATION = 0x0000DD03u
        const val SIG_NAME = 0x0000DD05u
        const val SIG_COMMENT = 0x0000DD04u
        const val SIG_TYPE = 0x0000DD06u
        const val SIG_SCALE = 0x0000DD07u
    }
}