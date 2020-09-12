package pl.renesans.renesans.utility.connection

interface ConnectionUtility {

    fun isConnectionAvailable(): Boolean

    fun startUrlActivity(url: String)
}