package pl.renesans.renesans.data.realm.bookmark

import android.content.Context
import io.realm.Realm
import pl.renesans.renesans.R
import pl.renesans.renesans.data.ArticleItem
import pl.renesans.renesans.data.Bookmark
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.data.realm.RealmUtility

class BookmarkDaoImpl(private val context: Context): BookmarkDao {

    private var realm: Realm
    private val realmDao: RealmContract.RealmDao
    private val allBookmarksArticlesItems: List<ArticleItem>
    private val mapBookmarksArticlesItems: List<ArticleItem>

    init {
        Realm.init(context)
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        realmDao = RealmDaoImpl(context)
        allBookmarksArticlesItems = realmDao.getArticlesItemsFromLocalList(RealmDaoImpl.MARKED_ARTICLES)
        mapBookmarksArticlesItems = realmDao.getArticlesItemsFromLocalList(RealmDaoImpl.MARKED_ARTICLES, true)
    }

    override fun bookmarksAreAvailable() = allBookmarksArticlesItems.isNotEmpty()

    override fun mapBookmarksAreAvailable() = mapBookmarksArticlesItems.isNotEmpty()

    override fun getListsOfBookmarks(): List<Bookmark>? {
        val allArticlesBookmark = Bookmark(ALL_ARTICLES_MODE, "Z1_0",
            context.getString(R.string.all), null,
            allBookmarksArticlesItems.size.toString())
        val mapBookmarks = Bookmark(PLACES_MODE, "Z1_0",
            context.getString(R.string.saved_places), null,
            mapBookmarksArticlesItems.size.toString())
        return listOf(allArticlesBookmark, mapBookmarks)
    }

    override fun getAllBookmarks(): List<Bookmark>? {
        val allBookmarks = mutableListOf<Bookmark>()
        allBookmarksArticlesItems.forEach{
            allBookmarks.add(Bookmark(ALL_ARTICLES_MODE,
                "${it.objectId!!}_0", it.title, it.objectId))
        }
        return allBookmarks
    }

    override fun getMapBookmarks(): List<Bookmark>? {
        val mapBookmarks = mutableListOf<Bookmark>()
        mapBookmarksArticlesItems.forEach{
            mapBookmarks.add(Bookmark(PLACES_MODE,
                "${it.objectId!!}_0", it.title, it.objectId))
        }
        return mapBookmarks
    }

    companion object {
        const val LISTS_MODE = "lists mode"
        const val ALL_ARTICLES_MODE = "all articles mode"
        const val PLACES_MODE = "places mode"
    }
}