package pl.renesans.renesans.download

import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_download.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl

class DownloadActivity : AppCompatActivity(), RealmContract.RealmInterractor {

    private lateinit var realmDao: RealmContract.RealmDao
    private var timesOfAlertDialogShow = 0
    private var countDownTimer: CountDownTimer? = null
    private var stopTimer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        changeStatusBarColor()
        realmDao = RealmDaoImpl(this, this)
        realmDao.onCreate()
        retryBtn.setOnClickListener{ downloadDb() }
        downloadDb()
        downloadProgressBar.progressDrawable.setColorFilter(ContextCompat
            .getColor(applicationContext, R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
    }

    private fun changeStatusBarColor(){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    override fun downloadSuccessful() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        stopTimer = true
        finish()
    }

    override fun downloadFailure(connectionProblem: Boolean) {
        downloadProgressBar.visibility = View.INVISIBLE
        waitView.visibility = View.GONE
        stopTimer = true
        showAlertDialog(connectionProblem)
    }

    override fun startedLoading() {

    }

    override fun downloadedProgress(percentages: Int) {
        downloadProgressBar.progress = percentages
    }

    override fun databaseIsUpToDate() {

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
        downloadProgressBar.progress = 0
        realmDao.refreshRealmDatabase(true)
        retryBtn.isEnabled = false
        downloadProgressBar.visibility = View.VISIBLE
        initializeTimer()
    }

    private fun initializeTimer(){
        countDownTimer = object : CountDownTimer(15000, 500) {
            override fun onTick(l: Long) {
                if(stopTimer){
                    countDownTimer?.cancel()
                    stopTimer = false
                }
            }

            override fun onFinish() {
                waitView.visibility = View.VISIBLE
            }
        }
        countDownTimer?.start()
    }
}