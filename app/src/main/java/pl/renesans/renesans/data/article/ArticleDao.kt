package pl.renesans.renesans.data.article

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleItem
import pl.renesans.renesans.data.PhotoArticle

interface ArticleDao {

    fun getRelatedArticlesList(article: Article): List<Article>

    fun articleHasSources(article: Article): Boolean

    fun getAllArticlesWithTextInTitle(text: String): List<Article>

    fun getArticlesListTitle(id: String): String

    fun getAllArticles(): List<Article>

    fun getArticleFromId(objectId: String): Article

    fun getObjectTypeFromObjectId(objectID: String): Int

    fun getArticlesList(articleId: String): List<Article>

    fun getArticlesItemsList(articleId: String): List<ArticleItem>

    fun getPhotoArticlesListBuiltToYear(year: Int): List<PhotoArticle>

    fun getPhotoArticlesListBuiltInYears(fromYear: Int, toYear: Int): List<PhotoArticle>

    fun getPhotoArticlesList(): List<PhotoArticle>
}