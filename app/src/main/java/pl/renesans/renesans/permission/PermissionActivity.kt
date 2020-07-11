package pl.renesans.renesans.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_permission.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.download.DownloadActivity

class PermissionActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sharedPrefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        changeStatusBarColor()
        sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        sharedPrefsEditor = sharedPrefs.edit()
        endBtn.setOnClickListener{askPermission()}
        skipNowView.setOnClickListener{
            sharedPrefsEditor.putBoolean(SKIP_PERMISSION, true)
            sharedPrefsEditor.apply()
            startNewActivity()
        }
    }

    private fun changeStatusBarColor(){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==1 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startNewActivity()
        }
    }

    private fun askPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    private fun startNewActivity(){
        val realmDao = RealmDaoImpl(this)
        realmDao.onCreate()
        if(!realmDao.realmDatabaseIsEmpty())
            startActivity(Intent(applicationContext, MainActivity::class.java))
        else startActivity(Intent(applicationContext, DownloadActivity::class.java))
        finish()
    }

    companion object {
        const val SKIP_PERMISSION = "skip permission"
    }
}
