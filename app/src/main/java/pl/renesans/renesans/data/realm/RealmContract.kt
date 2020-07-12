package pl.renesans.renesans.data.realm

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticlesList
import pl.renesans.renesans.data.PhotoArticle

interface RealmContract {

    interface RealmDao {

        fun onCreate()

        fun refreshRealmDatabase()

        fun checkRealm()

        fun checkRealmLists()

        fun realmDatabaseIsEmpty(): Boolean

        fun getArticlesLists(): List<ArticlesList>

        fun getPhotoArticles(fromYear: Int? = null, toYear: Int? = null): List<PhotoArticle>

        fun getArticlesListWithId(id: String): ArticlesList

        fun getArticleWithId(id: String): Article

        fun getArticlesFromListWithId(id: String): List<Article>
    }

    interface RealmInterractor {

        fun downloadSuccessful()

        fun downloadFailure(connectionProblem: Boolean = false)
    }

}

