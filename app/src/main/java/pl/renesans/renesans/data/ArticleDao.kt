package pl.renesans.renesans.data

interface ArticleDao {

    fun getRelatedArticlesList(article: Article): List<Article>

    fun articleHasSources(article: Article): Boolean

    fun getAllArticlesWithTextInTitle(text: String): List<Article>

    fun getAllArticles(): List<Article>

    fun getArticlesList(articleId: Int): List<Article>

    fun getImportantPeoples(): List<Article>

    fun getImportantArts(): List<Article>

    fun getImportantEvents(): List<Article>

    fun getOtherEras(): List<Article>

    fun getPhotoArticlesList(): List<PhotoArticle>
}