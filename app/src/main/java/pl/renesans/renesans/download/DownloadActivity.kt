package pl.renesans.renesans.download

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_download.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl

class DownloadActivity : AppCompatActivity(), RealmContract.RealmInterractor {

    private lateinit var realmDao: RealmContract.RealmDao
    private var timesOfAlertDialogShow = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        changeStatusBarColor()
        realmDao = RealmDaoImpl(this, this)
        realmDao.onCreate()
        retryBtn.setOnClickListener{ downloadDb() }
        downloadDb()
    }

    private fun changeStatusBarColor(){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    override fun downloadSuccessful() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    override fun downloadFailure(connectionProblem: Boolean) {
        downloadProgressBar.visibility = View.INVISIBLE
        showAlertDialog(connectionProblem)
    }

    private fun showAlertDialog(connectionProblem: Boolean){
        timesOfAlertDialogShow++
        if(timesOfAlertDialogShow <= 2 && !connectionProblem) showWarningDialog()
        else showRetryLaterDialog(connectionProblem)
    }

    private fun showWarningDialog(){
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.something_went_wrong))
            .setMessage(getString(R.string.try_again))
            .setPositiveButton(android.R.string.ok ) { _,_ -> downloadDb() }
            .setNegativeButton(android.R.string.cancel) {_,_ ->
                retryBtn.isEnabled = true
                retryBtn.visibility = View.VISIBLE
                downloadProgressBar.visibility = View.INVISIBLE
            }.create()

        dialog.setOnShowListener{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark))
        }

        dialog.show()
    }

    private fun showRetryLaterDialog(connectionProblem: Boolean){
        val message = if(!connectionProblem) getString(R.string.try_again_later)
        else getString(R.string.retry_with_connection)

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.something_went_wrong))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok){_,_ ->
                retryBtn.isEnabled = true
                retryBtn.visibility = View.VISIBLE
                downloadProgressBar.visibility = View.INVISIBLE
            }.create()

        dialog.setOnShowListener{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark))
        }

        dialog.show()
    }

    private fun downloadDb(){
        realmDao.refreshRealmDatabase()
        retryBtn.isEnabled = false
        downloadProgressBar.visibility = View.VISIBLE
    }
}