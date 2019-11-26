package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.blocks.*
import com.alekseyzhelo.eimob.objects.*

@ExperimentalUnsignedTypes
interface MobVisitor {
    fun visitDiplomacyBlock(value: DiplomacyBlock)
    fun visitObjectsBlock(value: ObjectsBlock)
    fun visitScriptBlock(value: ScriptBlock)
    fun visitUnknownBlock(value: UnknownBlock)
    fun visitWorldSetBlock(value: WorldSetBlock)
    fun visitMobFlame(value: MobFlame)
    fun visitMobLever(value: MobLever)
    fun visitMobLight(value: MobLight)
    fun visitMobObject(value: MobObject)
    fun visitMobParticle(value: MobParticle)
    fun visitMobSound(value: MobSound)
    fun visitMobTrap(value: MobTrap)
    fun visitMobUnit(value: MobUnit)
}