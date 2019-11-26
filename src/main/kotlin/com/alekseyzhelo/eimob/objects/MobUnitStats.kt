package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.MobException
import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

// TODO: determine field meanings
@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobUnitStats(
    override val signature: UInt,
    bytes: ByteArray
) : Block {
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
        throw MobException("Should not be called");
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