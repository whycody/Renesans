package pl.renesans.renesans.data.realm

import pl.renesans.renesans.data.*

interface RealmMapper {

    fun onCreate()

    fun getArticlesListFromRealm(articlesListRealm: ArticlesListRealm?): ArticlesList

    fun getArticlesListToRealm(articlesList: ArticlesList?): ArticlesListRealm

    fun setPropertiesOfArticlesListRealm(articlesList: ArticlesList?, articlesListRealm: ArticlesListRealm)

    fun getArticleFromRealm(articleRealm: ArticleRealm?): Article?

    fun getArticleItemFromRealm(articleRealm: ArticleRealm?): ArticleItem

    fun getArticleToRealm(article: Article): ArticleRealm

    fun setPropertiesOfArticleRealm(article: Article?, articleRealm: ArticleRealm)

    fun getPhotoArticleToRealm(photoArticle: PhotoArticle): PhotoArticleRealm

    fun getPhotoArticleFromRealm(photoArticleRealm: PhotoArticleRealm?): PhotoArticle

    fun setPropertiesOfPhotoArticleRealm(photoArticle: PhotoArticle?, photoArticleRealm: PhotoArticleRealm)
}