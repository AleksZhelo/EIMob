package com.alekseyzhelo.eimob

import com.alekseyzhelo.eimob.blocks.Block

@ExperimentalUnsignedTypes
interface MobListener {
    fun onBlockAdded(block: Block)
    fun onBlockRemoved(block: Block)
}