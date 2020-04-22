package pl.renesans.renesans.data.converter

import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleItem
import pl.renesans.renesans.data.PhotoArticle

class ArticleConverterImpl: ArticleConverter {

    override fun convertPhotoArticleToArticle(photoArticle: PhotoArticle): Article {
        val article = Article(photoArticle.objectId, photoArticle.objectType, photoArticle.title,
            photoArticle.header, photoArticle.source)
        if(photoArticle.paragraph!=null) article.listOfParagraphs = listOf(photoArticle.paragraph!!)
        if(photoArticle.photo!=null) article.listOfPhotos = listOf(photoArticle.photo!!)
        return article
    }

    override fun convertArticleToArticleItem(article: Article): ArticleItem {
        return ArticleItem(article.objectId, article.title)
    }

    override fun convertArticlesToArticleItemsList(articlesList: List<Article>): List<ArticleItem> {
        val articleItemsList = mutableListOf<ArticleItem>()
        for(article in articlesList)
            articleItemsList.add(ArticleItem(article.objectId, article.title))
        return articleItemsList
    }

}