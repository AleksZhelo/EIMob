package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_DIPLOMACY
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.mobEntrySize
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
// TODO: detect loyalty cascade
class DiplomacyBlock(
    bytes: ByteArray
) : Block {

    override val signature: UInt = SIG_DIPLOMACY
    val diplomacyMatrix: Array<IntArray>
    val playerNames: Array<String>

    init {
        with(bytes.binaryStream()) {
            diplomacyMatrix = readMobIntSquareMatrix(
                SIG_DIPLOMACY_MATRIX,
                "Failed to read diplomacy matrix in diplomacy block"
            )
            playerNames = readMobStringArray(
                SIG_PLAYER_NAMES,
                "Failed to read player names in diplomacy block",
                "Unexpected signature in player names array"
            )
            if (!isEof) {
                throw MobException("$bytesAvailable extra bytes in diplomacy block, aborting")
            }
        }
    }

    override fun getSize() = entryHeaderSize * 3 + diplomacyMatrix.mobEntrySize() + playerNames.mobEntrySize()

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobIntSquareMatrix(SIG_DIPLOMACY_MATRIX, diplomacyMatrix)
            writeMobStringArray(SIG_PLAYER_NAMES, playerNames)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitDiplomacyBlock(this)
    }

    companion object {
        const val SIG_DIPLOMACY_MATRIX = 0xDDDDDDD2u
        const val SIG_PLAYER_NAMES = 0xDDDDDDD3u
    }
}
