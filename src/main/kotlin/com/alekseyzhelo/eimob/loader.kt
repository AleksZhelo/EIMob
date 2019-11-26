package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.blocks.*
import com.alekseyzhelo.eimob.objects.*
import java.io.File
import java.nio.file.Paths

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val path = args[0]

    val inputFile = Paths.get(path).toFile()
    if (inputFile.isDirectory) {
        inputFile.list { _: File, name: String -> name.endsWith(".mob") }?.forEach {
            val file = MobFile(inputFile.resolve(it).absolutePath)
            println(file.filePath)
            file.accept(object : MobVisitor {
                override fun visitDiplomacyBlock(value: DiplomacyBlock) {
                }

                override fun visitObjectsBlock(value: ObjectsBlock) {
                    value.acceptChildren(this)
                }

                override fun visitScriptBlock(value: ScriptBlock) {
                }

                override fun visitUnknownBlock(value: UnknownBlock) {
                }

                override fun visitWorldSetBlock(value: WorldSetBlock) {
//                    println("Wind direction: ${value.windDirection}")
//                    println("Wind strength: ${value.windStrength}")
//                    println("Time: ${value.worldTime}")
//                    println("Ambient: ${value.worldAmbient}")
//                    println("Sunlight: ${value.worldSunlight}")
                }

                override fun visitMobFlame(value: MobFlame) {
                }

                override fun visitMobLever(value: MobLever) {
                }

                override fun visitMobLight(value: MobLight) {
//                    println("Light ${value.name} with id ${value.id}, color: ${value.color}")
                }

                override fun visitMobObject(value: MobObject) {
//                    if (value.unknownStr != "none") {
//                        println("Object ${value.name} with id ${value.id}, int: ${value.unknownInt}")
//                    }
                }

                override fun visitMobParticle(value: MobParticle) {
                }

                override fun visitMobSound(value: MobSound) {
                }

                override fun visitMobTrap(value: MobTrap) {
                }

                override fun visitMobUnit(value: MobUnit) {
                    println("Unit ${value.name} with id ${value.id}, stats: ${value.stats}")
                }
            });
        }
    } else {
        val mob = MobFile(path)
    }
}