package cn.charlotte.pit.addon

interface Addon {

    fun enableList(): Set<String>

    fun enable()

}