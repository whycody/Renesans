package pl.renesans.renesans

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkFirstLogin()
    }

    private fun checkFirstLogin(){
        val prefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        if(prefs.getBoolean(firstLogin, false)) startActivity(Intent(this, StartupActivity::class.java))
        else startActivity(Intent(this, MainActivity::class.java))
    }

    companion object{
        val firstLogin = "FIRST LOGIN"
    }
}
