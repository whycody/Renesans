package pl.renesans.renesans.bookmark

interface BookmarkContract {

    interface BookmarkView {

        fun notifyBookmarksDataSetChanged(mode: String)

        fun startArticleActivity(articleId: String)

        fun openPhotoBottomSheet(articleId: String)

        fun showNoBookmarksView()

        fun getState(): Int?
    }

    interface BookmarkPresenter {

        fun onBackPressed(): Boolean

        fun setMode(mode: String)

        fun getCurrentMode(): String

        fun onResume()
    }

}