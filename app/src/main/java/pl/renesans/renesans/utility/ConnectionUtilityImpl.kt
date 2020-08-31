package pl.renesans.renesans.utility

import android.content.Context
import android.net.ConnectivityManager
import java.lang.Exception

class ConnectionUtilityImpl(private val context: Context): ConnectionUtility {

    override fun isConnectionAvailable(): Boolean {
        return try {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (_: Exception) { false }
    }
}