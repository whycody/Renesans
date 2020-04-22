package pl.renesans.renesans.data.converter

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleItem
import pl.renesans.renesans.data.PhotoArticle

interface ArticleConverter {

    fun convertPhotoArticleToArticle(photoArticle: PhotoArticle): Article

    fun convertArticleToArticleItem(article: Article): ArticleItem

    fun convertArticlesToArticleItemsList(articlesList: List<Article>): List<ArticleItem>
}