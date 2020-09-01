package pl.renesans.renesans.utility

interface ConnectionUtility {

    fun isConnectionAvailable(): Boolean

    fun startUrlActivity(url: String)
}