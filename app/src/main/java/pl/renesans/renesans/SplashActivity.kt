package pl.renesans.renesans

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.renesans.renesans.startup.StartupActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkFirstLogin()
    }

    private fun checkFirstLogin(){
        val prefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        if(prefs.getBoolean(firstLogin, true)) startActivity(Intent(this, StartupActivity::class.java))
        else startActivity(Intent(this, MainActivity::class.java))
    }

    companion object{
        val firstLogin = "FIRST LOGIN"
    }
}
