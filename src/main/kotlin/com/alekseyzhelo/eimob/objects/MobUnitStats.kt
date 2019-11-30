package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.MobException
import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.toByteArraySkipHeader
import loggersoft.kotlin.streams.StreamOutput

// TODO: determine field meanings
@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobUnitStats(
    bytes: ByteArray
) : Block {
    override val signature: UInt = MobUnit.SIG_UNIT_STATS
    var hpMaxMaybe: Int
    var hpCurrentMaybe: Int
    var manaMaxMaybe: Int
    var manaCurrentMaybe: Int
    var unknownFloat1: Float
    var unknownFloat2: Float
    var runSpeed: Float
    var walkSpeed: Float
    var sneakSpeed: Float
    var crawlSpeed: Float
    var unknownAlmostZero1: Float
    var skillsPeripheralMaybe: Float
    var unknownAlmostZero2: Float
    var attackDistanceMaybe: Float
    val unknown8Bytes: Long // TODO: possibly AI class stay (byte), AI class lie (byte), and something else
    var toHit: Float
    var parry: Float
    var weaponWeightMaybe: Float
    var damageNubMaybe: Float
    var damageMaxMaybe: Float
    // TODO: not sure if these are indeed ints (and not floats)
    var attackPiercing: Int
    var attackSlashing: Int
    var attackBludgeoning: Int
    var attackThermal: Int
    var attackChemical: Int
    var attackElectric: Int
    var attackGeneral: Int
    // TODO end
    var absorbtion: Int  // armor value, basically?
    var sensesSight: Float
    var sensesInfra: Float
    var sensesSenseLife: Float
    var sensesHearing: Float
    var sensesSmell: Float
    var sensesTracking: Float
    var detectionSight: Float
    var detectionInfra: Float
    var detectionSenseLife: Float
    var detectionHearing: Float
    var detectionSmell: Float
    var detectionTracking: Float
    var skillsGeneralMaybe: Byte
    var skillsStealMaybe: Byte
    var skillsTameMaybe: Byte
    var unknownByte: Byte
    val unknown4Bytes: Int

    init {
        with(bytes.binaryStream()) {
            hpMaxMaybe = readInt()
            hpCurrentMaybe = readInt()
            manaMaxMaybe = readInt()
            manaCurrentMaybe = readInt()
            unknownFloat1 = readFloat()
            unknownFloat2 = readFloat()
            runSpeed = readFloat()
            walkSpeed = readFloat()
            sneakSpeed = readFloat()
            crawlSpeed = readFloat()
            unknownAlmostZero1 = readFloat()
            skillsPeripheralMaybe = readFloat()
            unknownAlmostZero2 = readFloat()
            attackDistanceMaybe = readFloat()
            unknown8Bytes = readLong()
            toHit = readFloat()
            parry = readFloat()
            weaponWeightMaybe = readFloat()
            damageNubMaybe = readFloat()
            damageMaxMaybe = readFloat()
            attackPiercing = readInt()
            attackSlashing = readInt()
            attackBludgeoning = readInt()
            attackThermal = readInt()
            attackChemical = readInt()
            attackElectric = readInt()
            attackGeneral = readInt()
            absorbtion = readInt()
            sensesSight = readFloat()
            sensesInfra = readFloat()
            sensesSenseLife = readFloat()
            sensesHearing = readFloat()
            sensesSmell = readFloat()
            sensesTracking = readFloat()
            detectionSight = readFloat()
            detectionInfra = readFloat()
            detectionSenseLife = readFloat()
            detectionHearing = readFloat()
            detectionSmell = readFloat()
            detectionTracking = readFloat()
            skillsGeneralMaybe = readByte()
            skillsStealMaybe = readByte()
            skillsTameMaybe = readByte()
            unknownByte = readByte()
            unknown4Bytes = readInt()
        }
    }

    override fun getSize() = entryHeaderSize + 172

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(out)
            writeInt(hpMaxMaybe)
            writeInt(hpCurrentMaybe)
            writeInt(manaMaxMaybe)
            writeInt(manaCurrentMaybe)
            writeFloat(unknownFloat1)
            writeFloat(unknownFloat2)
            writeFloat(runSpeed)
            writeFloat(walkSpeed)
            writeFloat(sneakSpeed)
            writeFloat(crawlSpeed)
            writeFloat(unknownAlmostZero1)
            writeFloat(skillsPeripheralMaybe)
            writeFloat(unknownAlmostZero2)
            writeFloat(attackDistanceMaybe)
            writeLong(unknown8Bytes)
            writeFloat(toHit)
            writeFloat(parry)
            writeFloat(weaponWeightMaybe)
            writeFloat(damageNubMaybe)
            writeFloat(damageMaxMaybe)
            writeInt(attackPiercing)
            writeInt(attackSlashing)
            writeInt(attackBludgeoning)
            writeInt(attackThermal)
            writeInt(attackChemical)
            writeInt(attackElectric)
            writeInt(attackGeneral)
            writeInt(absorbtion)
            writeFloat(sensesSight)
            writeFloat(sensesInfra)
            writeFloat(sensesSenseLife)
            writeFloat(sensesHearing)
            writeFloat(sensesSmell)
            writeFloat(sensesTracking)
            writeFloat(detectionSight)
            writeFloat(detectionInfra)
            writeFloat(detectionSenseLife)
            writeFloat(detectionHearing)
            writeFloat(detectionSmell)
            writeFloat(detectionTracking)
            writeByte(skillsGeneralMaybe)
            writeByte(skillsStealMaybe)
            writeByte(skillsTameMaybe)
            writeByte(unknownByte)
            writeInt(unknown4Bytes)
        }
    }

    override fun accept(visitor: MobVisitor) {
        throw MobException("Should not be called")
    }

    override fun clone(): MobUnitStats = MobUnitStats(toByteArraySkipHeader())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MobUnitStats

        if (hpMaxMaybe != other.hpMaxMaybe) return false
        if (hpCurrentMaybe != other.hpCurrentMaybe) return false
        if (manaMaxMaybe != other.manaMaxMaybe) return false
        if (manaCurrentMaybe != other.manaCurrentMaybe) return false
        if (unknownFloat1 != other.unknownFloat1) return false
        if (unknownFloat2 != other.unknownFloat2) return false
        if (runSpeed != other.runSpeed) return false
        if (walkSpeed != other.walkSpeed) return false
        if (sneakSpeed != other.sneakSpeed) return false
        if (crawlSpeed != other.crawlSpeed) return false
        if (unknownAlmostZero1 != other.unknownAlmostZero1) return false
        if (skillsPeripheralMaybe != other.skillsPeripheralMaybe) return false
        if (unknownAlmostZero2 != other.unknownAlmostZero2) return false
        if (attackDistanceMaybe != other.attackDistanceMaybe) return false
        if (unknown8Bytes != other.unknown8Bytes) return false
        if (toHit != other.toHit) return false
        if (parry != other.parry) return false
        if (weaponWeightMaybe != other.weaponWeightMaybe) return false
        if (damageNubMaybe != other.damageNubMaybe) return false
        if (damageMaxMaybe != other.damageMaxMaybe) return false
        if (attackPiercing != other.attackPiercing) return false
        if (attackSlashing != other.attackSlashing) return false
        if (attackBludgeoning != other.attackBludgeoning) return false
        if (attackThermal != other.attackThermal) return false
        if (attackChemical != other.attackChemical) return false
        if (attackElectric != other.attackElectric) return false
        if (attackGeneral != other.attackGeneral) return false
        if (absorbtion != other.absorbtion) return false
        if (sensesSight != other.sensesSight) return false
        if (sensesInfra != other.sensesInfra) return false
        if (sensesSenseLife != other.sensesSenseLife) return false
        if (sensesHearing != other.sensesHearing) return false
        if (sensesSmell != other.sensesSmell) return false
        if (sensesTracking != other.sensesTracking) return false
        if (detectionSight != other.detectionSight) return false
        if (detectionInfra != other.detectionInfra) return false
        if (detectionSenseLife != other.detectionSenseLife) return false
        if (detectionHearing != other.detectionHearing) return false
        if (detectionSmell != other.detectionSmell) return false
        if (detectionTracking != other.detectionTracking) return false
        if (skillsGeneralMaybe != other.skillsGeneralMaybe) return false
        if (skillsStealMaybe != other.skillsStealMaybe) return false
        if (skillsTameMaybe != other.skillsTameMaybe) return false
        if (unknownByte != other.unknownByte) return false
        if (unknown4Bytes != other.unknown4Bytes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hpMaxMaybe
        result = 31 * result + hpCurrentMaybe
        result = 31 * result + manaMaxMaybe
        result = 31 * result + manaCurrentMaybe
        result = 31 * result + unknownFloat1.hashCode()
        result = 31 * result + unknownFloat2.hashCode()
        result = 31 * result + runSpeed.hashCode()
        result = 31 * result + walkSpeed.hashCode()
        result = 31 * result + sneakSpeed.hashCode()
        result = 31 * result + crawlSpeed.hashCode()
        result = 31 * result + unknownAlmostZero1.hashCode()
        result = 31 * result + skillsPeripheralMaybe.hashCode()
        result = 31 * result + unknownAlmostZero2.hashCode()
        result = 31 * result + attackDistanceMaybe.hashCode()
        result = 31 * result + unknown8Bytes.hashCode()
        result = 31 * result + toHit.hashCode()
        result = 31 * result + parry.hashCode()
        result = 31 * result + weaponWeightMaybe.hashCode()
        result = 31 * result + damageNubMaybe.hashCode()
        result = 31 * result + damageMaxMaybe.hashCode()
        result = 31 * result + attackPiercing
        result = 31 * result + attackSlashing
        result = 31 * result + attackBludgeoning
        result = 31 * result + attackThermal
        result = 31 * result + attackChemical
        result = 31 * result + attackElectric
        result = 31 * result + attackGeneral
        result = 31 * result + absorbtion
        result = 31 * result + sensesSight.hashCode()
        result = 31 * result + sensesInfra.hashCode()
        result = 31 * result + sensesSenseLife.hashCode()
        result = 31 * result + sensesHearing.hashCode()
        result = 31 * result + sensesSmell.hashCode()
        result = 31 * result + sensesTracking.hashCode()
        result = 31 * result + detectionSight.hashCode()
        result = 31 * result + detectionInfra.hashCode()
        result = 31 * result + detectionSenseLife.hashCode()
        result = 31 * result + detectionHearing.hashCode()
        result = 31 * result + detectionSmell.hashCode()
        result = 31 * result + detectionTracking.hashCode()
        result = 31 * result + skillsGeneralMaybe
        result = 31 * result + skillsStealMaybe
        result = 31 * result + skillsTameMaybe
        result = 31 * result + unknownByte
        result = 31 * result + unknown4Bytes
        return result
    }

    override fun toString(): String {
        return "MobUnitStats(hpMaxMaybe=$hpMaxMaybe, hpCurrentMaybe=$hpCurrentMaybe, manaMaxMaybe=$manaMaxMaybe,\n" +
                "manaCurrentMaybe=$manaCurrentMaybe, unknownFloat1=$unknownFloat1, unknownFloat2=$unknownFloat2,\n" +
                "runSpeed=$runSpeed, walkSpeed=$walkSpeed, sneakSpeed=$sneakSpeed, crawlSpeed=$crawlSpeed,\n" +
                "unknownAlmostZero1=$unknownAlmostZero1, skillsPeripheralMaybe=$skillsPeripheralMaybe,\n" +
                "unknownAlmostZero2=$unknownAlmostZero2, attackDistanceMaybe=$attackDistanceMaybe,\n" +
                "unknown8Bytes=$unknown8Bytes, toHit=$toHit, parry=$parry, weaponWeightMaybe=$weaponWeightMaybe,\n" +
                "damageNubMaybe=$damageNubMaybe, damageMaxMaybe=$damageMaxMaybe, attackPiercing=$attackPiercing,\n" +
                "attackSlashing=$attackSlashing, attackBludgeoning=$attackBludgeoning, attackThermal=$attackThermal,\n" +
                "attackChemical=$attackChemical, attackElectric=$attackElectric, attackGeneral=$attackGeneral,\n" +
                "absorbtion=$absorbtion, sensesSight=$sensesSight, sensesInfra=$sensesInfra,\n" +
                "sensesSenseLife=$sensesSenseLife, sensesHearing=$sensesHearing, sensesSmell=$sensesSmell,\n" +
                "sensesTracking=$sensesTracking, detectionSight=$detectionSight, detectionInfra=$detectionInfra,\n" +
                "detectionSenseLife=$detectionSenseLife, detectionHearing=$detectionHearing,\n" +
                "detectionSmell=$detectionSmell, detectionTracking=$detectionTracking,\n" +
                "skillsGeneralMaybe=$skillsGeneralMaybe, skillsStealMaybe=$skillsStealMaybe,\n" +
                "skillsTameMaybe=$skillsTameMaybe, unknownByte=$unknownByte, unknown4Bytes=$unknown4Bytes)"
    }
}