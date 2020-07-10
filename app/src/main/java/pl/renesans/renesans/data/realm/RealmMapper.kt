package pl.renesans.renesans.data.realm

import pl.renesans.renesans.data.*

interface RealmMapper {

    fun onCreate()

    fun getArticlesListFromRealm(articlesListRealm: ArticlesListRealm): ArticlesList

    fun getArticlesListToRealm(articlesList: ArticlesList): ArticlesListRealm

    fun setPropertiesOfArticlesListRealm(articlesList: ArticlesList?, articlesListRealm: ArticlesListRealm)

    fun getArticleFromRealm(articleRealm: ArticleRealm): Article

    fun getArticleToRealm(article: Article): ArticleRealm

    fun setPropertiesOfArticleRealm(article: Article?, articleRealm: ArticleRealm)

    fun getPhotoArticleToRealm(photoArticle: PhotoArticle): PhotoArticleRealm

    fun setPropertiesOfPhotoArticleRealm(photoArticle: PhotoArticle?, photoArticleRealm: PhotoArticleRealm)
}