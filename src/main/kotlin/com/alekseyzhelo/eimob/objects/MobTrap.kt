package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.util.Float2
import com.alekseyzhelo.eimob.util.Float3
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobTrap(
    bytes: ByteArray
) : MobObjectBase() {

    override val signature: UInt = SIG_TRAP
    var unknownTrapInt: Int
    var trapSpell: String
    var castInterval: Int
    var castOnce: Boolean
    val areas: ArrayList<Float3>
    val targets: ArrayList<Float2>

    init {
        with(bytes.binaryStream()) {
            unknownTrapInt = readMobInt(SIG_UNKNOWN_INT, "Failed to read unknownTrapInt in MobTrap block")
            trapSpell = readMobString(SIG_TRAP_SPELL, "Failed to read trapSpell in MobTrap block")
            castInterval = readMobInt(SIG_CAST_INTERVAL, "Failed to read castInterval in MobTrap block")
            castOnce = readMobBoolean(SIG_CAST_ONCE, "Failed to read castOnce in MobTrap block")
            areas = readMobFloat3Array(
                SIG_AREAS, "Failed to read areas in MobTrap block",
                "Unexpected signature in areas array"
            ).toCollection(ArrayList())
            targets = readMobFloat2Array(
                SIG_TARGETS, "Failed to read targets in MobTrap block",
                "Unexpected signature in targets array"
            ).toCollection(ArrayList())
            readCommonObjectData(this)
        }
    }

    override fun getSize(): Int = entryHeaderSize * 7 + 4 + trapSpell.length + 4 + 1 +
            areas.toTypedArray().mobEntrySize() + targets.toTypedArray().mobEntrySize() + getCommonObjectDataSize()

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobInt(SIG_UNKNOWN_INT, unknownTrapInt)
            writeMobString(SIG_TRAP_SPELL, trapSpell)
            writeMobInt(SIG_CAST_INTERVAL, castInterval)
            writeMobBoolean(SIG_CAST_ONCE, castOnce)
            writeMobFloat3Array(SIG_AREAS, areas.toTypedArray())
            writeMobFloat2Array(SIG_TARGETS, targets.toTypedArray())
            writeCommonObjectData(this)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobTrap(this)
    }

    override fun clone(): MobTrap = MobTrap(toByteArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MobTrap

        if (unknownTrapInt != other.unknownTrapInt) return false
        if (trapSpell != other.trapSpell) return false
        if (castInterval != other.castInterval) return false
        if (castOnce != other.castOnce) return false
        if (areas != other.areas) return false
        if (targets != other.targets) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + unknownTrapInt
        result = 31 * result + trapSpell.hashCode()
        result = 31 * result + castInterval
        result = 31 * result + castOnce.hashCode()
        result = 31 * result + areas.hashCode()
        result = 31 * result + targets.hashCode()
        return result
    }

    companion object {
        const val SIG_UNKNOWN_INT = 0xBBAB0001u
        const val SIG_TRAP_SPELL = 0xBBAB0002u
        const val SIG_CAST_INTERVAL = 0xBBAB0005u
        const val SIG_CAST_ONCE = 0xBBAC0005u
        const val SIG_AREAS = 0xBBAB0003u
        const val SIG_TARGETS = 0xBBAB0004u
    }
}