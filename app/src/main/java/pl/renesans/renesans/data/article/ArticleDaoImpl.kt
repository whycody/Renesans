package pl.renesans.renesans.data.article

import android.content.Context
import pl.renesans.renesans.R
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.realm.RealmContract
import pl.renesans.renesans.data.realm.RealmDaoImpl
import pl.renesans.renesans.discover.recycler.DiscoverRecyclerFragment

class ArticleDaoImpl(private val context: Context? = null): ArticleDao {

    private var realmDao: RealmContract.RealmDao? = null

    init {
        if(context != null) realmDao = RealmDaoImpl(context)
    }

    override fun getRelatedArticlesList(article: Article): List<Article> {
        val relatedArticles = mutableListOf<Article>()
        if(article.tour!=null)
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.TOUR,
                title = context?.getString(R.string.tour), objectId = "Z3"))
        article.listOfRelatedArticlesIds?.forEach { relatedArticles.add(getArticleFromId(it)) }
        if(getObjectTypeFromObjectId(article.objectId!!) != DiscoverRecyclerFragment.OTHER_ERAS)
            relatedArticles.add(getArticleFromId("O4"))
        if(articleHasSources(article))
            relatedArticles.add(Article(objectType = DiscoverRecyclerFragment.SOURCES,
                title = context?.resources?.getString(R.string.sources), objectId = "Z0"))
        return relatedArticles
    }

    override fun articleHasSources(article: Article): Boolean {
        if (article.source?.url != null) return true
        if (article.listOfPhotos != null)
            article.listOfPhotos!!.forEach { photo -> if (photo.source != null) return true }
        return false
    }

    override fun getAllArticlesWithTextInTitle(text: String): List<Article> {
        val allFilteredArticles = getAllArticles()
        allFilteredArticles.filter { article ->  article.title!!.toLowerCase().contains(text.toLowerCase())}
        return allFilteredArticles
    }

    override fun getArticlesListTitle(id: String) = realmDao!!.getArticlesListWithId(id).name!!

    override fun getAllArticles(): List<Article> = realmDao!!.getAllArticles()

    override fun getArticleFromId(objectId: String) = realmDao!!.getArticleWithId(objectId)

    override fun getObjectTypeFromObjectId(objectID: String) = when(objectID.first()) {
            'P' -> DiscoverRecyclerFragment.PEOPLE
            'A' -> DiscoverRecyclerFragment.ARTS
            'E' -> DiscoverRecyclerFragment.EVENTS
            'O' -> DiscoverRecyclerFragment.OTHER_ERAS
            'S' -> DiscoverRecyclerFragment.SOURCES
            else -> DiscoverRecyclerFragment.PHOTOS
    }

    override fun getArticlesList(articleId: String): List<Article>
            = realmDao!!.getArticlesFromListWithId(articleId)

    override fun getArticlesItemsList(articleId: String): List<ArticleItem>
            = realmDao!!.getArticlesItemsFromListWithId(articleId)

    override fun getPhotoArticlesListBuiltToYear(year: Int): List<PhotoArticle> {
        val photoArticles = getPhotoArticlesList()
        val filteredPhotoArticles = mutableListOf<PhotoArticle>()
        for(article in photoArticles)
            if (article.objectType == CITY_TYPE &&
                filteredPhotoArticles.any{ articleItem -> articleItem.cityKey == article.cityKey})
                filteredPhotoArticles.add(article)
            else if (article.yearOfBuild != null && article.yearOfBuild!! <= year)
                filteredPhotoArticles.add(article)
        return filteredPhotoArticles
    }

    override fun getPhotoArticlesListBuiltInYears(fromYear: Int, toYear: Int): List<PhotoArticle> {
        val photoArticles = getPhotoArticlesList()
        val filteredPhotoArticles = mutableListOf<PhotoArticle>()
        for(article in photoArticles)
            if (article.objectType == CITY_TYPE &&
                filteredPhotoArticles.any{ articleItem -> articleItem.cityKey == article.cityKey})
                filteredPhotoArticles.add(article)
            else if (article.yearOfBuild != null
                && article.yearOfBuild!! >= fromYear && article.yearOfBuild!! <= toYear)
                filteredPhotoArticles.add(article)
        return filteredPhotoArticles
    }

    override fun getPhotoArticlesList() = realmDao!!.getPhotoArticles()

    companion object{
        const val PLACE_TYPE = 10
        const val CITY_TYPE = 11
    }
}