package pl.renesans.renesans.utility.bookmark

import android.content.Context
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl

class BookmarkUtilityImpl(val context: Context,
                          private val bookmarkView: BookmarkUtilityInterractor.BookmarkView,
                          private val articleId: String):
    BookmarkUtilityInterractor.BookmarkUtility {

    private var bookmarkActive = false
    private val realmDao: RealmContract.RealmDao

    init {
        realmDao = RealmDaoImpl(context)
        bookmarkActive = realmDao.articleIsInLocalList(RealmDaoImpl.MARKED_ARTICLES, articleId)
        bookmarkView.changeColorOfBookmark(bookmarkActive)
    }

    override fun handleBookmarkOnClick() {
        bookmarkActive = !bookmarkActive
        bookmarkView.changeColorOfBookmark(bookmarkActive)
        refreshArticleInDatabase()
    }

    private fun refreshArticleInDatabase(){
        if(bookmarkActive) realmDao.addItemToLocalArticlesList(RealmDaoImpl.MARKED_ARTICLES, articleId)
        else realmDao.deleteItemFromLocalArticlesList(RealmDaoImpl.MARKED_ARTICLES, articleId)
    }
}