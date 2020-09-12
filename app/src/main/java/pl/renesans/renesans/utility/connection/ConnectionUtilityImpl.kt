package pl.renesans.renesans.utility.connection

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.ConnectivityManager
import android.net.Uri
import pl.renesans.renesans.utility.connection.ConnectionUtility
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

    override fun startUrlActivity(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        launchBrowser.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchBrowser)
    }
}