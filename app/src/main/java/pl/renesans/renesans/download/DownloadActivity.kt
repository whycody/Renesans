package pl.renesans.renesans.download

import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_download.*
import pl.renesans.renesans.MainActivity
import pl.renesans.renesans.R
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.utility.alert.dialog.AlertDialogUtilityImpl

class DownloadActivity : AppCompatActivity(), DownloadContract.DownloadView {

    private lateinit var realmDao: RealmContract.RealmDao
    private lateinit var downloadPresenter: DownloadContract.DownloadPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        changeStatusBarColor()
        startAnimations()
        realmDao = RealmDaoImpl(applicationContext)
        downloadPresenter = DownloadPresenterImpl(realmDao, this)
        retryBtn.setOnClickListener{ downloadPresenter.downloadDb() }
        downloadProgressBar.progressDrawable.colorFilter = PorterDuffColorFilter(ContextCompat
            .getColor(applicationContext, R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
    }

    private fun changeStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    private fun startAnimations() {
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        downloadLayout.startAnimation(animation)
    }

    override fun onBackPressed() { }

    override fun finishActivity() = finish()

    private val dialogUtility = AlertDialogUtilityImpl()

    override fun showWarningDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.something_went_wrong))
            .setMessage(getString(R.string.try_again))
            .setPositiveButton(android.R.string.ok ) { _,_ -> downloadPresenter.downloadDb() }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                downloadPresenter.alertDialogDismissed()
            }.create()
        dialogUtility.setColorsOfButtonsOfDialog(dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun showRetryLaterDialog(connectionProblem: Boolean) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.something_went_wrong))
            .setMessage(getMessageOfRetryDialog(connectionProblem))
            .setPositiveButton(android.R.string.ok){_,_ ->
                downloadPresenter.alertDialogDismissed()
            }.create()
        dialogUtility.setColorsOfButtonsOfDialog(dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun getMessageOfRetryDialog(connectionProblem: Boolean) =
        if(!connectionProblem) getString(R.string.try_again_later)
        else getString(R.string.retry_with_connection)

    override fun startMainActivity() =
        startActivity(Intent(applicationContext, MainActivity::class.java))

    override fun showRetryBtn() {
        retryBtn.visibility = View.VISIBLE
    }

    override fun hideRetryBtn() {
        retryBtn.visibility = View.INVISIBLE
    }

    override fun setRetryBtnEnabled(enabled: Boolean) {
        retryBtn.isEnabled = enabled
    }

    override fun hideDownloadProgressBar() {
        downloadProgressBar.visibility = View.INVISIBLE
    }

    override fun setCuriosity(curiosity: String) {
        curiosityView.text = curiosity
    }

    override fun setDownloadProgressBarProgress(progress: Int) {
        downloadProgressBar.progress = progress
    }

    override fun showDownloadProgressBar() {
        downloadProgressBar.visibility = View.VISIBLE
    }

    override fun showWaitView() {
        startWaitViewAnimation()
        waitView.visibility = View.VISIBLE
    }

    private fun startWaitViewAnimation() =
        waitView.startAnimation(AnimationUtils
            .loadAnimation(applicationContext, android.R.anim.fade_in))

    override fun hideWaitView() {
        waitView.visibility = View.GONE
    }
}