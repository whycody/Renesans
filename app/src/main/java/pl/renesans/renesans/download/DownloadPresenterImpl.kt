package pl.renesans.renesans.download

import android.os.CountDownTimer
import pl.renesans.renesans.data.curiosity.CuriosityPresenterImpl
import pl.renesans.renesans.data.realm.RealmContract

class DownloadPresenterImpl(private val realmDao: RealmContract.RealmDao,
                            private val view: DownloadContract.DownloadView):
    DownloadContract.DownloadPresenter, RealmContract.RealmInterractor {

    private val curiosityPresenter = CuriosityPresenterImpl()
    private var downloadCountDownTimer: CountDownTimer? = null
    private var curiosityCountDownTimer: CountDownTimer? = null
    private var timesOfAlertDialogShowed = 0
    private var downloadTimerShouldStop = false
    private var curiosityTimerShouldStop = false

    init {
        realmDao.setRealmInterractor(this)
        downloadDb()
        initializeCuriosityTimer()
        showNextCuriosity()
    }

    override fun downloadDb() {
        view.setDownloadProgressBarProgress(0)
        realmDao.refreshRealmDatabase(true)
        view.setRetryBtnEnabled(false)
        view.showDownloadProgressBar()
        initializeDownloadTimer()
    }

    override fun alertDialogDismissed() {
        view.showRetryBtn()
        view.setRetryBtnEnabled(true)
        view.hideDownloadProgressBar()
    }

    private fun initializeDownloadTimer() {
        downloadCountDownTimer = object : CountDownTimer(15000, 500) {
            override fun onTick(l: Long) {
                if(downloadTimerShouldStop) stopDownloadTimer()
            }

            override fun onFinish() = handleDownloadTimerFinished()
        }
        downloadCountDownTimer?.start()
    }

    private fun handleDownloadTimerFinished() {
        view.showWaitView()
        view.hideRetryBtn()
    }

    private fun stopDownloadTimer() {
        downloadCountDownTimer?.cancel()
        downloadTimerShouldStop = false
    }

    private fun initializeCuriosityTimer(){
        curiosityCountDownTimer = object : CountDownTimer(8000, 1000) {
            override fun onTick(p0: Long) {
                if(curiosityTimerShouldStop) curiosityCountDownTimer?.cancel()
            }

            override fun onFinish() = handleCuriosityTimerFinished()
        }
        curiosityCountDownTimer?.start()
    }

    private fun handleCuriosityTimerFinished() {
        curiosityCountDownTimer?.start()
        showNextCuriosity()
    }

    private fun showNextCuriosity() = view.setCuriosity(curiosityPresenter.getRandomCuriosity())

    override fun downloadSuccessful() {
        view.startMainActivity()
        view.finishActivity()
    }

    override fun downloadFailure(connectionProblem: Boolean) {
        view.hideDownloadProgressBar()
        view.hideWaitView()
        downloadTimerShouldStop = true
        showAlertDialog(connectionProblem)
    }

    private fun showAlertDialog(connectionProblem: Boolean){
        timesOfAlertDialogShowed++
        if(timesOfAlertDialogShowed <= 2 && !connectionProblem) view.showWarningDialog()
        else view.showRetryLaterDialog(connectionProblem)
    }

    override fun startedLoading() { }

    override fun downloadedProgress(percentages: Int) =
        view.setDownloadProgressBarProgress(percentages)

    override fun databaseIsUpToDate() { }
}