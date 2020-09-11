package pl.renesans.renesans

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.download.DownloadActivity
import pl.renesans.renesans.permission.PermissionActivity
import pl.renesans.renesans.startup.StartupActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var realmDao: RealmContract.RealmDao
    private lateinit var sharedPrefs: SharedPreferences
    private var permissionGranted = false
    private var skipPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realmDao = RealmDaoImpl(applicationContext)
        sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        skipPermission = sharedPrefs.getBoolean(PermissionActivity.SKIP_PERMISSION, false)
        permissionGranted = (ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        checkFirstLogin()
    }

    private fun checkFirstLogin(){
        if(sharedPrefs.getBoolean(firstLogin, true))
            startActivity(Intent(this, StartupActivity::class.java))
        else if(!permissionGranted && !skipPermission)
            startActivity(Intent(this, PermissionActivity::class.java))
        else if(realmDao.realmDatabaseIsEmpty())
            startActivity(Intent(this, DownloadActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))
    }

    companion object{
        const val firstLogin = "FIRST LOGIN"
    }
}
