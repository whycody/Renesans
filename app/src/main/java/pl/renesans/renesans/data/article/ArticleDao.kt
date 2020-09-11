package pl.renesans.renesans.data.article

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.PhotoArticle

interface ArticleDao {

    fun getRelatedArticlesList(article: Article): List<Article>

    fun articleHasSources(article: Article): Boolean

    fun getObjectTypeFromObjectId(objectID: String): Int

    fun getPhotoArticlesListBuiltToYear(year: Int): List<PhotoArticle>

    fun getPhotoArticlesListBuiltInYears(fromYear: Int, toYear: Int): List<PhotoArticle>

    fun getPhotoArticlesList(): List<PhotoArticle>
}