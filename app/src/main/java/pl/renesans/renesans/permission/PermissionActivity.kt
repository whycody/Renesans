package pl.renesans.renesans.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_permission.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R

class PermissionActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sharedPrefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        sharedPrefsEditor = sharedPrefs.edit()
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryVeryDark)
        endBtn.setOnClickListener{askPermission()}
        skipNowView.setOnClickListener{
            sharedPrefsEditor.putBoolean(SKIP_PERMISSION, true)
            sharedPrefsEditor.apply()
            startNewActivity()
        }
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val SKIP_PERMISSION = "skip permission"
    }
}
