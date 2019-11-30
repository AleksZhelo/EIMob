package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.blocks.*
import com.alekseyzhelo.eimob.objects.*

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
open class MobVisitor {
    // @formatter:off
    open fun visitDiplomacyBlock(value: DiplomacyBlock) {}
    open fun visitObjectsBlock(value: ObjectsBlock) {}
    open fun visitScriptBlock(value: ScriptBlock) {}
    open fun visitUnknownBlock(value: UnknownBlock) {}
    open fun visitWorldSetBlock(value: WorldSetBlock) {}
    open fun visitMobMapEntity(value: MobMapEntity) {}
    open fun visitMobFlame(value: MobFlame) { visitMobMapEntity(value) }
    open fun visitMobLever(value: MobLever) { visitMobMapEntity(value) }
    open fun visitMobLight(value: MobLight) { visitMobMapEntity(value) }
    open fun visitMobObject(value: MobObject) { visitMobMapEntity(value) }
    open fun visitMobParticle(value: MobParticle) { visitMobMapEntity(value) }
    open fun visitMobSound(value: MobSound) { visitMobMapEntity(value) }
    open fun visitMobTrap(value: MobTrap) { visitMobMapEntity(value) }
    open fun visitMobUnit(value: MobUnit) { visitMobMapEntity(value) }
    // @formatter:on
}