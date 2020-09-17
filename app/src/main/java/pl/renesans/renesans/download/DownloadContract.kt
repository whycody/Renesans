package pl.renesans.renesans.download

interface DownloadContract {

    interface DownloadView {

        fun setCuriosity(curiosity: String)

        fun setDownloadProgressBarProgress(progress: Int)

        fun showDownloadProgressBar()

        fun hideDownloadProgressBar()

        fun showWaitView()

        fun hideWaitView()

        fun showRetryBtn()

        fun hideRetryBtn()

        fun setRetryBtnEnabled(enabled: Boolean)

        fun showWarningDialog()

        fun showRetryLaterDialog(connectionProblem: Boolean)

        fun startMainActivity()

        fun finishActivity()
    }

    interface DownloadPresenter {

        fun downloadDb()

        fun alertDialogDismissed()
    }
}