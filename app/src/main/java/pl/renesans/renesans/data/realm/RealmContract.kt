package pl.renesans.renesans.data.realm

import io.realm.RealmList
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleItem
import pl.renesans.renesans.data.ArticlesList
import pl.renesans.renesans.data.PhotoArticle

interface RealmContract {

    interface RealmDao {

        fun onCreate()

        fun refreshRealmDatabase(firstDownload: Boolean = false)

        fun checkRealm()

        fun checkRealmLists()

        fun realmDatabaseIsEmpty(): Boolean

        fun getCityWithCityKey(cityKey: String): String?

        fun getAllArticles(): List<Article>

        fun getArticlesLists(): List<ArticlesList>

        fun getPhotoArticles(withCities: Boolean = true): List<PhotoArticle>

        fun getArticlesListWithId(id: String): ArticlesList

        fun getArticleWithId(id: String): Article

        fun getArticlesFromListWithId(id: String): List<Article>

        fun getArticlesItemsFromListWithId(id: String): List<ArticleItem>

        fun getArticlesItemsFromSearchHistory(): List<ArticleItem>

        fun addItemToSearchHistoryRealm(id: String)

        fun deleteItemFromSearchHistoryRealm(id: String)
    }

    interface RealmInterractor {

        fun downloadSuccessful()

        fun downloadFailure(connectionProblem: Boolean = false)

        fun startedLoading()

        fun databaseIsUpToDate()
    }

}

