package pl.renesans.renesans.data.realm

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleItem
import pl.renesans.renesans.data.ArticlesList
import pl.renesans.renesans.data.PhotoArticle

interface RealmContract {

    interface RealmDao {

        fun refreshRealmDatabase(firstDownload: Boolean = false)

        fun getDatabaseVersion(): String

        fun realmDatabaseIsEmpty(): Boolean

        fun getCityWithCityKey(cityKey: String): String?

        fun getAllArticles(): List<Article>

        fun getArticlesLists(): List<ArticlesList>

        fun getPhotoArticles(withCities: Boolean = true): List<PhotoArticle>

        fun getArticlesListWithId(id: String): ArticlesList

        fun getArticleWithId(id: String): Article

        fun getArticlesFromListWithId(id: String): List<Article>

        fun getArticlesItemsFromListWithId(id: String): List<ArticleItem>

        fun getArticlesItemsFromLocalList(localListId: String,
                                          onlyPhotoArticles: Boolean = false): List<ArticleItem>

        fun addItemToLocalArticlesList(localListId: String, articleId: String)

        fun articleIsInLocalList(localListId: String, articleId: String): Boolean

        fun deleteItemFromLocalArticlesList(localListId: String, articleId: String)

        fun getPhotoArticleWithId(id: String): PhotoArticle

        fun getTypeOfArticle(id: String): String
    }

    interface RealmInterractor {

        fun downloadSuccessful()

        fun downloadFailure(connectionProblem: Boolean = false)

        fun startedLoading()

        fun downloadedProgress(percentages: Int)

        fun databaseIsUpToDate()
    }

}

