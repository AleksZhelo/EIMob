package com.alekseyzhelo.eimob.types

fun array2dOfInt(sizeOuter: Int, sizeInner: Int): Array<IntArray> = Array(sizeOuter) { IntArray(sizeInner) }

fun Array<IntArray>.mobEntrySize() = fold(0, { acc, row -> acc + row.size * Int.SIZE_BYTES })

fun Array<IntArray>.print() {
    this.forEach { row ->
        println(row.joinToString(separator = " "))
    }
}

fun Array<IntArray>.isSymmetric(): Boolean {
    if (!this.all { it.size == this.size }) {
        return false  // not even square
    }

    for (row in 0 until (this.size - 1)) {
        for (column in (row + 1) until this.size) {
            if (this[row][column] != this[column][row]) {
                return false
            }
        }
    }
    return true
}