package pl.renesans.renesans.data

interface ArticleDao {

    fun getArticlesList(articleId: Int): List<Article>

    fun getImportantPeoples(): List<Article>

    fun getImportantArts(): List<Article>

    fun getImportantEvents(): List<Article>

    fun getOtherEras(): List<Article>
}